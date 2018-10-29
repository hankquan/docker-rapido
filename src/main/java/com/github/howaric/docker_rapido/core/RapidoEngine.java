package com.github.howaric.docker_rapido.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Strings;
import com.github.howaric.docker_rapido.docker.RestartPolicy;
import com.github.howaric.docker_rapido.exceptions.IllegalImageTagsException;
import com.github.howaric.docker_rapido.exceptions.IllegalPolicyException;
import com.github.howaric.docker_rapido.exceptions.TemplateResolveException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.utils.RapidoLogCentre;
import com.github.howaric.docker_rapido.utils.ValidatorUtil;
import com.github.howaric.docker_rapido.utils.YamlUtil;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;
import com.github.howaric.docker_rapido.yaml_model.Service;

public class RapidoEngine {

	private static Logger logger = LoggerFactory.getLogger(RapidoEngine.class);

	private File templateFile;
	private List<String> imageTags;
	private Boolean isDeclaredOfficial;
	private RapidoTemplate rapidoTemplate;
	private Boolean isRollback;
	private String nodeLabel;

	private RapidoEngine(CliOptions cliOptions) {
		initialize(cliOptions);
	}

	private void initialize(CliOptions cliOptions) {
		String templateFilePath = cliOptions.getTemplateFilePath();
		templateFile = CommonUtil.readTemplateFile(templateFilePath);
		if (templateFile == null) {
			throw new TemplateResolveException("Failed to get template file");
		}
		try {
			logger.info("Template yml content:\n\n{}\n", FileUtils.readFileToString(templateFile, "utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		imageTags = cliOptions.getImageTag();
		isDeclaredOfficial = cliOptions.isDeclareOfficial();
		isRollback = cliOptions.isRollback();
		nodeLabel = cliOptions.getNodeLabel();
	}

	public static void run(CliOptions cliOptions) {
		new RapidoEngine(cliOptions).startRapido();
	}

	private void startRapido() {
		/**
		 * parse file to rapidoTemplate
		 */
		rapidoTemplate = YamlUtil.getObj(templateFile, RapidoTemplate.class);
		if (rapidoTemplate == null) {
			throw new TemplateResolveException("Failed to parse template to yaml bean");
		}

		logger.info("Rapido template:\n\n{}\n", CommonUtil.prettyJson(rapidoTemplate));

		/**
		 * basic check through hibernate validation
		 */
		Map<String, StringBuffer> validate = ValidatorUtil.validate(rapidoTemplate);
		if (validate != null) {
			throw new TemplateResolveException("Yaml bean validation failed: \n{}" + CommonUtil.prettyJson(validate));
		}

		/**
		 * check delivery type
		 */
		String deliveryType = rapidoTemplate.getDelivery_type();
		if (!DeliveryType.isDeliveryTypeLegal(deliveryType)) {
			throw new TemplateResolveException(
					"Unsupported delivery type: " + deliveryType + ", optionals:" + DeliveryType.supportedTypes());
		}

		/**
		 * check restart policy
		 */
		Map<String, Service> services = rapidoTemplate.getServices();
		Collection<Service> servicesInfo = services.values();
		for (Service service : servicesInfo) {
			String condition = service.getDeploy().getRestart_policy().getCondition();
			if (!RestartPolicy.isRestartPolicyLegal(condition)) {
				throw new IllegalPolicyException(
						"Unsupported restart policy: " + condition + ", optionals: " + RestartPolicy.supportedTypes());
			}
		}

		/**
		 * check restriction of delivery type and owner, only official deployment can
		 * use master as owner
		 */
		if (isDeclaredOfficial) {
			if (!DeliveryType.isOfficial(deliveryType) || !"master".equalsIgnoreCase(rapidoTemplate.getOwner())) {
				throw new TemplateResolveException("You must use official and master for an official deployment");
			}
		} else {
			if (DeliveryType.isOfficial(deliveryType) || "master".equalsIgnoreCase(rapidoTemplate.getOwner())) {
				throw new TemplateResolveException(
						"You are not allowed to use official or master for an developmental deployment, please use --official to declare this deployment as an official one");
			}
		}

		/**
		 * analyze service dependence
		 */
		List<String> orderedServices = validateDependence();

		/**
		 * check node connectivity
		 */

		/**
		 * format build path
		 */
		formatBuildRelativePath();

		Map<String, Node> nodes = rapidoTemplate.getNodes();
		List<ServiceTaskHandler> serviceTaskHandlers = new ArrayList<>();
		for (int i = 0; i < orderedServices.size(); i++) {
			String serviceName = orderedServices.get(i);// deploy service name
			Service service = services.get(serviceName);// deploy service
														// details
			List<Node> targetNodes = null;
			if (service.getDeploy() != null) {// target nodes
				targetNodes = service.getDeploy().getPlacement().targetNodes(nodes, nodeLabel);
			}
			String imageTag = null;
			if (service.getBuild() != null) {// need build image
				imageTag = getImageTag(serviceName);
			}
			if ((targetNodes == null || targetNodes.isEmpty()) && imageTag == null) {
				throw new TemplateResolveException("TargetNodes and imageTags are all empty at the same time, will do nothing");
			}
			logger.info("{} will be created or updated on following nodes:{}", serviceName, targetNodes);
			serviceTaskHandlers.add(new ServiceTaskHandler(rapidoTemplate, serviceName, targetNodes, imageTag, isRollback));
		}

		for (ServiceTaskHandler serviceTaskHandler : serviceTaskHandlers) {
			serviceTaskHandler.runTask();
		}
		RapidoLogCentre.successfulExit();
		System.exit(0);
	}

	private void formatBuildRelativePath() {
		Map<String, Service> services = rapidoTemplate.getServices();
		Collection<Service> values = services.values();
		for (Service service : values) {
			String build = service.getBuild();
			if (!Strings.isStringEmpty(build) && build.startsWith(".")) {
				String templateFileAbsolutePath = templateFile.getAbsolutePath();
				String relativePath = templateFileAbsolutePath.substring(0, templateFileAbsolutePath.lastIndexOf(File.separator) + 1);
				String newBuild = relativePath + build;
				logger.info("Set build to: " + newBuild);
				service.setBuild(newBuild);
			}
		}
	}

	private String getImageTag(String serviceName) {
		for (String tags : imageTags) {
			if (tags.contains(":")) {
				String[] split = tags.split(":");
				String _serviceName = split[0];
				String tag = split[1];
				if (serviceName.equals(_serviceName)) {
					return tag;
				}
			} else if (imageTags.size() > 1) {
				throw new IllegalImageTagsException("Image tags should be serviceName:tag when there are more than one");
			} else {
				return tags;
			}
		}
		return null;
	}

	private List<String> validateDependence() {
		List<String> services = new ArrayList<>();
		Map<String, List<String>> dependence = new HashMap<>();
		Map<String, Service> serviceMap = rapidoTemplate.getServices();
		for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
			String serviceName = entry.getKey();
			Service service = entry.getValue();
			List<String> depends_on = service.getDepends_on();
			services.add(serviceName);
			dependence.put(serviceName, depends_on);
		}
		logger.info("Raw services: {}", services);
		logger.info("Service dependencies: {}", dependence);
		analyseDependence(services, dependence);
		logger.info("Sorted services: {}", services);
		return services;
	}

	private void analyseDependence(List<String> services, Map<String, List<String>> dependence) {
		int size = services.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = 0; j < size - 1 - i; j++) {
				String before = services.get(j);
				String after = services.get(j + 1);
				if (dependence.get(before) != null && dependence.get(before).contains(after)) {
					String temp = before;
					services.set(j, after);
					services.set(j + 1, temp);
				}
			}
		}
	}
}

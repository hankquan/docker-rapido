package com.github.howaric.docker_rapido.core;

import com.beust.jcommander.Strings;
import com.github.howaric.docker_rapido.docker.RestartPolicy;
import com.github.howaric.docker_rapido.exceptions.IllegalPolicyException;
import com.github.howaric.docker_rapido.exceptions.TemplateResolveException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.utils.LogUtil;
import com.github.howaric.docker_rapido.utils.ValidatorUtil;
import com.github.howaric.docker_rapido.utils.YamlUtil;
import com.github.howaric.docker_rapido.yaml_model.Deploy;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;
import com.github.howaric.docker_rapido.yaml_model.Service;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RapidoEngine {

    private static Logger logger = LoggerFactory.getLogger(RapidoEngine.class);

    private File templateFile;
    private String imageTag;
    private Boolean isDeclaredOfficial;
    private RapidoTemplate rapidoTemplate;
    private Boolean isClean;
    private Boolean isForceClean;
    private Boolean isTagLatest;
    private Map<String, String> parameters;

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
            logger.info("Origin template content:\n\n{}\n", FileUtils.readFileToString(templateFile, "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageTag = cliOptions.getTag();
        isDeclaredOfficial = cliOptions.isDeclareOfficial();
        isClean = cliOptions.isClean();
        isForceClean = cliOptions.isForceClean();
        isTagLatest = cliOptions.isTagLatest();
        parameters = formatParameters(cliOptions.getParameters());
    }

    private static Map<String, String> formatParameters(List<String> parameters) {
        Map<String, String> result = new HashMap<>();
        parameters.forEach((param) -> {
            String[] split = param.split("=");
            result.put(split[0], split[1]);
        });
        return result;
    }

    public static void run(CliOptions cliOptions) {
        new RapidoEngine(cliOptions).startRapido();
    }

    private void startRapido() {

        // format and parse template file
        String templateContent = CommonUtil.readFileContent(templateFile);
        if (Strings.isStringEmpty(templateContent)) {
            throw new TemplateResolveException("Read template file error");
        }

        Set<Map.Entry<String, String>> entries = parameters.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            templateContent = templateContent.replaceAll(String.format("@%s@", entry.getKey()), entry.getValue());
        }

        rapidoTemplate = YamlUtil.getObj(templateContent, RapidoTemplate.class);
        if (rapidoTemplate == null) {
            throw new TemplateResolveException("Failed to parse template to yaml bean");
        }

        logger.info("Formatted template:\n\n{}\n", CommonUtil.prettyJson(rapidoTemplate));

        /**
         * basic check through hibernate validation
         */
        Map<String, StringBuffer> validate = ValidatorUtil.validate(rapidoTemplate);
        if (validate != null) {
            throw new TemplateResolveException("Yaml bean validation failed: \n{}" + CommonUtil.prettyJson(validate));
        }

        /**
         * get delivery type
         */
        DeliveryType deliveryType = DeliveryType.getType(rapidoTemplate.getDelivery_type());

        /**
         * check restart policy
         */
        Map<String, Service> services = rapidoTemplate.getServices();
        Collection<Service> servicesInfo = services.values();
        for (Service service : servicesInfo) {
            Deploy deploy = service.getDeploy();
            if (deploy == null) {
                continue;
            }
            String condition = deploy.getRestart_policy().getCondition();
            if (!RestartPolicy.isRestartPolicyLegal(condition)) {
                throw new IllegalPolicyException(
                        "Unsupported restart policy: " + condition + ", optionals: " + RestartPolicy.supportedTypes());
            }
        }

        /**
         * check restriction of delivery type and owner, only official
         * deployment can use master as owner
         */
        if (isDeclaredOfficial) {
            if (!deliveryType.isOfficial() || !"master".equalsIgnoreCase(rapidoTemplate.getOwner())) {
                throw new TemplateResolveException("You must use official and master for official deployment");
            }
            if (Strings.isStringEmpty(imageTag)) {
                imageTag = "latest";
            }
        } else {
            if (deliveryType.isOfficial() || "master".equalsIgnoreCase(rapidoTemplate.getOwner())) {
                throw new TemplateResolveException(
                        "You are not allowed to use official or master for an developmental deployment, please use --official to declare this deployment as an official one");
            }
        }

        /**
         * analyze service dependence
         */
        List<String> orderedServices = validateDependency();
        if (isClean || isForceClean) {
            Collections.reverse(orderedServices);
        }
        /**
         * check node connectivity
         */

        /**
         * format build path
         */
        formatBuildRelativePath();

        Map<String, Node> nodes = rapidoTemplate.getNodes();
        List<TaskHandler> taskHandlers = new ArrayList<>();
        for (int i = 0; i < orderedServices.size(); i++) {
            String serviceName = orderedServices.get(i);// deploy service name
            Service service = services.get(serviceName);// deploy service
            // details
            List<Node> targetNodes = null;
            if (service.getDeploy() != null) {// target nodes
                targetNodes = service.getDeploy().getPlacement().targetNodes(nodes);
            }

            if (isForceClean || (isClean && service.getBuild() != null)) {
                taskHandlers.add(new CleanTaskHandler(rapidoTemplate, serviceName, service, targetNodes));
                continue;
            }

            if (isClean) {
                continue;
            }

            if ((targetNodes == null || targetNodes.isEmpty()) && imageTag == null) {
                throw new TemplateResolveException("TargetNodes and imageTags are all empty at the same time, will do nothing");
            }
            logger.info("{} will be created or updated on following nodes:{}", serviceName, targetNodes);
            taskHandlers.add(new DeployTaskHandler(rapidoTemplate, serviceName, targetNodes, imageTag, isTagLatest));
        }

        for (TaskHandler taskHandler : taskHandlers) {
            taskHandler.runTask();
        }

        LogUtil.successfulExit();
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

    private List<String> validateDependency() {
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
        sortDependency(services, dependence);
        logger.info("Sorted services: {}", services);
        return services;
    }

    private void sortDependency(List<String> services, Map<String, List<String>> dependence) {
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

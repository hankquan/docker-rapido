package com.github.howaric.docker_rapido.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.howaric.docker_rapido.exceptions.TemplateResolveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.exceptions.GenerateRapidoJobFailedException;
import com.github.howaric.docker_rapido.exceptions.IllegalImageTagsException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
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

    public RapidoEngine(File templateFile, List<String> imageTags, Boolean isDeclaredOfficial) {
        super();
        this.templateFile = templateFile;
        this.imageTags = imageTags;
        this.isDeclaredOfficial = isDeclaredOfficial;
    }

    public void startRapido() {
        // 1 parse file to rapidoTemplate
        rapidoTemplate = YamlUtil.getObj(templateFile, RapidoTemplate.class);
        if (rapidoTemplate == null) {
            throw new TemplateResolveException("Failed to parse template to yaml bean");
        }
        Map<String, StringBuffer> validate = ValidatorUtil.validate(rapidoTemplate);
        if (validate != null) {
            throw new TemplateResolveException("Yaml bean validation failed: \n{}" + CommonUtil.prettyJson(validate));
        }

        // TODO more detailed validation
        if ("official".equalsIgnoreCase(rapidoTemplate.getDeliver_type())) {
            if (!isDeclaredOfficial) {
                throw new TemplateResolveException("You must use --official if you want to deliver an official deployment");
            }
        }

        if (isDeclaredOfficial) {
            if (!"official".equalsIgnoreCase(rapidoTemplate.getDeliver_type()) || !"master".equalsIgnoreCase(rapidoTemplate.getOwner())) {
                throw new TemplateResolveException("You must use official and master for an official deployment");
            }
        } else {
            if ("official".equalsIgnoreCase(rapidoTemplate.getDeliver_type()) || "master".equalsIgnoreCase(rapidoTemplate.getOwner())) {
                throw new TemplateResolveException(
                        "You can not use official or master for an development deployment, please use --official to declare this deployment as an official one");
            }
        }

        // validate template dependence and sort out
        List<String> orderedServices = validateDependence();

        // TODO check node connectivity

        Map<String, Service> services = rapidoTemplate.getServices();
        Map<String, Node> nodes = rapidoTemplate.getNodes();
        List<ServiceTaskHandler> serviceTaskHandlers = new ArrayList<>();
        for (int i = 0; i < orderedServices.size(); i++) {
            String serviceName = orderedServices.get(i);// deploy service name
            Service service = services.get(serviceName);// deploy service
                                                        // details
            List<Node> targetNodes = null;
            if (service.getDeploy() != null) {// target nodes
                targetNodes = service.getDeploy().getPlacement().getNodes(nodes);
            }
            String imageTag = null;
            if (service.getBuild() != null) {// need build image
                imageTag = getImageTag(serviceName);
            }
            if ((targetNodes == null || targetNodes.isEmpty()) && imageTag == null) {
                throw new GenerateRapidoJobFailedException("TargetNodes and imageTags are all empty at the same time, will do nothing");
            }
            logger.info("{} will be created or updated on following nodes:{}", serviceName, targetNodes);
            serviceTaskHandlers.add(new ServiceTaskHandler(rapidoTemplate, serviceName, targetNodes, imageTag));
        }

        try {
            for (ServiceTaskHandler serviceTaskHandler : serviceTaskHandlers) {
                serviceTaskHandler.runTask();
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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

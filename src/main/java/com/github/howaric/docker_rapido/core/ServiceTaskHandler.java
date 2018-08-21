package com.github.howaric.docker_rapido.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.docker.DockerProxy;
import com.github.howaric.docker_rapido.docker.DockerProxyFactory;
import com.github.howaric.docker_rapido.exceptions.IllegalImageTagsException;
import com.github.howaric.docker_rapido.utils.RapidoLogCentre;
import com.github.howaric.docker_rapido.yaml_model.Deploy;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;
import com.github.howaric.docker_rapido.yaml_model.Service;

public class ServiceTaskHandler {

    private static Logger logger = LoggerFactory.getLogger(ServiceTaskHandler.class);

    private RapidoTemplate rapidoTemplate;
    private String serviceName;
    private List<Node> targetNodes;
    private String imageTag;
    private Boolean isRollback;

    public static final String LATEST = ":latest";

    public ServiceTaskHandler(RapidoTemplate rapidoTemplate, String serviceName, List<Node> targetNodes, String imageTag,
            Boolean isRollback) {
        super();
        this.rapidoTemplate = rapidoTemplate;
        this.serviceName = serviceName;
        this.targetNodes = targetNodes;
        this.imageTag = imageTag;
        this.isRollback = isRollback;
    }

    public void runTask() {
        logger.info("");
        RapidoLogCentre.printInCentreWithStar("Start service task: " + serviceName);
        logger.info("Get service task: {}, to build image-tag: {}, rapido will deploy this service to nodes: {}", serviceName, imageTag,
                targetNodes);
        // build image if need
        Service service = rapidoTemplate.getServices().get(serviceName);
        String imageName = service.getImage();

        boolean isBuildImage = false;
        if (service.getBuild() != null && !isRollback) {
            if (imageTag == null) {
                if (DeliveryType.isDevelopmental(rapidoTemplate.getDelivery_type())) {
                    imageTag = rapidoTemplate.getOwner();
                    isBuildImage = true;
                } else {
                    throw new IllegalImageTagsException("Image tag can not be empty when build is specific in official delivery");
                }
            } else {
                isBuildImage = true;
            }
        }

        if (isRollback) {
            imageName = combineImageNameWithRapoAndTag(imageName);
            isBuildImage = false;
        }

        String existedImageId = null;
        DockerProxy optDocker = DockerProxyFactory.getInstance(rapidoTemplate.getRemote_docker());
        if (isBuildImage) {
            imageName = combineImageNameWithRapoAndTag(imageName);
            // remove if specific image has existed
            existedImageId = optDocker.isImageExited(imageName);
            logger.info("Find local image with same image-tag: {}, rapido will try to remove it", imageName);
            optDocker.tryToRemoveImage(existedImageId);
            logger.info("Start to build image: {}", imageName);
            String imageId = optDocker.buildImage(service.getBuild(), imageName);
            logger.info("Building successfully, imageId is {}", imageId);
            logger.info("Start to push image");
            optDocker.pushImage(imageName, rapidoTemplate.getRepository().getUsername(), rapidoTemplate.getRepository().getPassword());
            logger.info("Pushing successfully");
        }

        Deploy deploy = service.getDeploy();
        if (deploy == null) {
            logger.info("No deployment description found, deploy skipped");
            RapidoLogCentre.printInCentreWithStar("End service task");
            return;
        }

        imageName = imageName.contains(":") ? imageName : imageName + LATEST;
        logger.info("Rapido will use image {} to create containers of service {}", imageName, serviceName);

        // go to each node and do operation
        String deploy_policy = deploy.getDeploy_policy();
        DeployPolicy deployPolicy = DeployPolicy.getType(deploy_policy);
        logger.info("Deploy policy is {}", deployPolicy.getValue());
        for (Node node : targetNodes) {
            DockerHostDeployer deployer = DockerHostDeployerFactory.getInstance(deployPolicy);
            deployer.deploy(rapidoTemplate.getRepository(), rapidoTemplate.getDelivery_type(), rapidoTemplate.getOwner(), node, serviceName,
                    service, imageName);
        }
        optDocker.tryToRemoveImage(existedImageId);
        RapidoLogCentre.printInCentreWithStar("End service task");
    }

    private String combineImageNameWithRapoAndTag(String imageName) {
        return rapidoTemplate.getRepository().repo() + "/" + imageName + ":" + imageTag;
    }

}

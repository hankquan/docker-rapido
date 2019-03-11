package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.core.dto.ProcessorInfo;
import com.github.howaric.docker_rapido.core.dto.ProcessorInfoFactory;
import com.github.howaric.docker_rapido.docker.DockerProxy;
import com.github.howaric.docker_rapido.docker.DockerProxyFactory;
import com.github.howaric.docker_rapido.exceptions.IllegalImageTagsException;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.utils.LogUtil;
import com.github.howaric.docker_rapido.yaml_model.Deploy;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;
import com.github.howaric.docker_rapido.yaml_model.Service;
import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DeployTaskHandler implements TaskHandler {

    private static Logger logger = LoggerFactory.getLogger(DeployTaskHandler.class);

    private RapidoTemplate rapidoTemplate;
    private String serviceName;
    private List<Node> targetNodes;
    private String imageTag;
    private Boolean isTagLatest;

    public static final String LATEST = "latest";

    public DeployTaskHandler(RapidoTemplate rapidoTemplate, String serviceName, List<Node> targetNodes, String imageTag,
            Boolean isTagLatest) {
        this.rapidoTemplate = rapidoTemplate;
        this.serviceName = serviceName;
        this.targetNodes = targetNodes;
        this.imageTag = imageTag;
        this.isTagLatest = isTagLatest;
    }

    @Override
    public void runTask() {
        LogUtil.printEmptyLine();
        LogUtil.printInCentreWithStar("Start deploy task: " + serviceName);
        logger.info("Get deploy task: {},  rapido will deploy this service to nodes: {}", serviceName, targetNodes);
        // build image if need
        Service service = rapidoTemplate.getServices().get(serviceName);
        String imageName = service.getImage();
        String imageNameWithRepo = combineImageNameWithRepo(imageName);
        boolean isBuildImage = false;

        DeliveryType deliveryType = DeliveryType.getType(rapidoTemplate.getDelivery_type());
        if (service.getBuild() != null) {
            if (imageTag == null) {
                if (!deliveryType.isOfficial()) {
                    imageTag = rapidoTemplate.getOwner();
                    isBuildImage = true;
                } else {
                    throw new IllegalImageTagsException("Image tag can not be empty when build is specific in official delivery");
                }
            } else {
                isBuildImage = true;
            }
        }

        DockerProxy optDocker = DockerProxyFactory.getInstance(rapidoTemplate.getRemote_docker());
        String existedImageId = optDocker.isImageExsited(combineImageNameWithRepoAndTag(imageName));
        if (isBuildImage && existedImageId != null) {
            logger.info("Find local image with same image-tag, rapido will try to remove it");
            optDocker.tryToRemoveImage(existedImageId);
        }

        if (isBuildImage) {
            imageName = combineImageNameWithRepoAndTag(imageName);
            logger.info("Start to build image: {}", imageName);
            File dockerfile = CommonUtil.readTemplateFile(service.getBuild() + File.separator + "Dockerfile");
            String baseImg = getBaseImg(dockerfile);
            if (!Strings.isNullOrEmpty(baseImg) && optDocker.isImageExsited(baseImg) == null) {
                logger.info("Start to pull base image: {}", baseImg);
                optDocker.pullImage(baseImg, rapidoTemplate.getRepository().getUsername(), rapidoTemplate.getRepository().getPassword());
            }
            String imageId = optDocker.buildImage(service.getBuild(), imageName);
            logger.info("Building successfully, imageId is {}", imageId);
            logger.info("Start to push image");
            optDocker.pushImage(imageName, rapidoTemplate.getRepository().getUsername(), rapidoTemplate.getRepository().getPassword());
            if (deliveryType.isOfficial() && isTagLatest) {
                logger.info("Start to tag and push image with latest tag");
                optDocker.tagImage(imageId, imageNameWithRepo, LATEST);
                optDocker.pushImage(imageNameWithRepo + ":latest", rapidoTemplate.getRepository().getUsername(),
                        rapidoTemplate.getRepository().getPassword());
            }
            logger.info("Pushing successfully");
        }

        Deploy deploy = service.getDeploy();
        if (deploy == null) {
            logger.info("No deployment description found, deploy skipped");
            LogUtil.printInCentreWithStar("End service task");
            return;
        }

        imageName = imageName.contains(":") ? imageName : imageName + ":" + LATEST;
        logger.info("Rapido will use image {} to create containers of service {}", imageName, serviceName);

        // go to each node and do operation
        String deploy_policy = deploy.getDeploy_policy();
        DeployPolicy deployPolicy = DeployPolicy.getType(deploy_policy);
        logger.info("Deploy policy is {}", deployPolicy.getValue());
        for (Node node : targetNodes) {
            ProcessorInfo processorInfo = ProcessorInfoFactory
                    .getDeployProcessorInfo(rapidoTemplate.getRepository(), deliveryType, rapidoTemplate.getOwner(), node, serviceName,
                            service, imageName);
            NodeProcessorFactory.getDeployProcessor(deployPolicy).process(processorInfo);
        }
        optDocker.tryToRemoveImage(existedImageId);
    }

    private String combineImageNameWithRepoAndTag(String imageName) {
        if (rapidoTemplate.getRepository() == null) {
            return imageName + ":" + imageTag;
        }
        return rapidoTemplate.getRepository().repo() + "/" + imageName + ":" + imageTag;
    }

    private String combineImageNameWithRepo(String imageName) {
        if (rapidoTemplate.getRepository() == null) {
            return imageName;
        }
        return rapidoTemplate.getRepository().repo() + "/" + imageName;
    }

    private String getBaseImg(File dockerfile) {
        String baseImg = "";
        try {
            List<String> lines = FileUtils.readLines(dockerfile, "utf-8");
            String firstLine = lines.get(0);
            return firstLine.substring(firstLine.indexOf("FROM") + 4).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baseImg;
    }
}

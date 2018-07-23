package com.github.howaric.docker_rapido.core;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.core.RapidoDockerRunner.RapidoDockerRunnerFactory;
import com.github.howaric.docker_rapido.docker.DockerProxy;
import com.github.howaric.docker_rapido.docker.DockerProxyFactory;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.Service;

public class RapidoJob implements Callable<Integer> {

    private static Logger logger = LoggerFactory.getLogger(RapidoJob.class);

    private JobModel jobModel;

    public RapidoJob(JobModel jobModel) {
        super();
        this.jobModel = jobModel;
    }

    @Override
    public Integer call() throws Exception {
        logger.info("Get job: " + jobModel);
        String repo = "172.24.94.60:5000";
        Service service = jobModel.getService();

        // build image if need
        String image = jobModel.getService().getImage();
        String imageNameWithRepoAndTag = repo + "/" + image;
        if (service.getBuild() != null) {
            DockerProxy optDocker = DockerProxyFactory.getInstance("tcp://39.104.164.56:6066");
            imageNameWithRepoAndTag = imageNameWithRepoAndTag + ":" + jobModel.getImageTag();
            String imageId = optDocker.buildImage(service.getBuild(), imageNameWithRepoAndTag);
            logger.info("Build successfully, imageId is {}", imageId);
        }

        // go to each node and do operation
        DeployPolicy deployPolicy = service.getDeploy().getDeployPolicy();

        // rolling-update 起新的，删除之前的。。。
        // on-absent 存在跳过，不存在启动
        for (Node node : jobModel.getTargetNodes()) {
            RapidoDockerRunner runner = RapidoDockerRunnerFactory.getInstance(deployPolicy);
            runner.start(jobModel.getDeliver_type(), jobModel.getOwner(), node, jobModel.getServiceName(), service,
                    imageNameWithRepoAndTag);
        }

        return 1;
    }

}

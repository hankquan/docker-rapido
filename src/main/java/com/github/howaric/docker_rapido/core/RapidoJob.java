package com.github.howaric.docker_rapido.core;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		Service service = jobModel.getService();

		// build image if need
		String imageId = null;
		if (service.getBuild() != null) {
			DockerProxy optDocker = DockerProxyFactory.getInstance("tcp://39.104.164.56:6066");
			String image = jobModel.getService().getImage();
			String repo = "172.24.94.60:5000";
			imageId = optDocker.buildImage(service.getBuild(), repo + "/" + image + ":" + jobModel.getImageTag());
			logger.info("Build successfully, imageId is {}", imageId);
		}

		List<Node> targetNodes = jobModel.getTargetNodes();
		for (Node node : targetNodes) {
			
			
			
		}
		
		return 1;
	}

}

package com.github.howaric.docker_rapido.docker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.howaric.docker_rapido.utils.CommonUtil;

public class DefaultDockerProxy implements DockerProxy {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultDockerProxy.class);

	private DockerClient dockerClient;

	DefaultDockerProxy() {
		super();
	}

	DefaultDockerProxy(String endPoint) {
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(endPoint)
				.build();
		dockerClient = DockerClientBuilder.getInstance(config).build();
	}

	@Override
	public String buildImage(String localDockerfilePath, String... imageTag) {
		File file = new File(localDockerfilePath);
		BuildImageResultCallback buildImageResultCallback = new BuildImageResultCallback() {
			@Override
			public void onNext(BuildResponseItem item) {
				super.onNext(item);
			}
		};
		Set<String> tags = new HashSet<>();
		for (int i = 0; i < imageTag.length; i++) {
			tags.add(imageTag[i]);
		}
		String imageId = dockerClient.buildImageCmd(file).withTags(tags).exec(buildImageResultCallback).awaitImageId();
		return imageId;
	}

	@Override
	public void pushImage(String imageNameWithRepoAndTag, String username, String password) {
		PushImageResultCallback pushImageResultCallback = new PushImageResultCallback() {
			@Override
			public void onNext(PushResponseItem item) {
				super.onNext(item);
			}
		};
		if (username != null && password != null) {
			AuthConfig authConfig = dockerClient.authConfig().withUsername(username).withPassword(password);
			dockerClient.pushImageCmd(imageNameWithRepoAndTag).withAuthConfig(authConfig).exec(pushImageResultCallback)
					.awaitSuccess();
		} else {
			dockerClient.pushImageCmd(imageNameWithRepoAndTag).exec(pushImageResultCallback).awaitSuccess();
		}
	}

	@Override
	public void removeImage(String imageId) {
		dockerClient.removeImageCmd(imageId).exec();
	}

	@Override
	public void pullImage(String imageNameWithTag, String username, String password) {
		PullImageResultCallback pullImageResultCallback = new PullImageResultCallback();
		if (username != null && password != null) {
			AuthConfig authConfig = dockerClient.authConfig().withUsername(username).withPassword(password);
			dockerClient.pullImageCmd(imageNameWithTag).withAuthConfig(authConfig).exec(pullImageResultCallback)
					.awaitSuccess();
		} else {
			dockerClient.pullImageCmd(imageNameWithTag).exec(pullImageResultCallback).awaitSuccess();
		}
	}

	@Override
	public boolean isImageExits(String imageNameWithTag) {
		List<Image> imageList = dockerClient.listImagesCmd().exec();
		for (Image image : imageList) {
			String[] repoTags = image.getRepoTags();
			for (String repoTag : repoTags) {
				if (imageNameWithTag.equals(repoTag)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<Container> listContainers(boolean isShowAll) {
		List<Container> containerList = dockerClient.listContainersCmd().withShowAll(isShowAll).exec();
		return containerList;
	}

	@Override
	public void stopContainer(String containerId) {
		try {
			dockerClient.stopContainerCmd(containerId).exec();
		} catch (NotModifiedException e) {
			logger.error("No such Container " + containerId);
			// throw new NoSuchContainerException("No such Container " +
			// containerId);
		}
	}

	@Override
	public void startContainer(String containerId) {
		try {
			dockerClient.startContainerCmd(containerId).exec();
		} catch (NotModifiedException e) {
			logger.error("No such Container " + containerId);
			// throw new NoSuchContainerException("No such Container " +
			// containerId);
		}
	}

	@Override
	public void restartContainer(String containerId) {
		try {
			dockerClient.restartContainerCmd(containerId).exec();
		} catch (NotModifiedException e) {
			logger.error("No such Container " + containerId);
			// throw new NoSuchContainerException("No such Container " +
			// containerId);
		}
	}

	@Override
	public String createContainer(String name, String imageNameWithTag, Integer port, Map<String, String> envs,
			Map<String, String> links, Map<String, String> volumes) {
		ExposedPort tcpInner = ExposedPort.tcp(port);
		Ports portBindings = new Ports();
		portBindings.bind(tcpInner, Ports.Binding.bindPort(port));

		CreateContainerCmd cmd = dockerClient.createContainerCmd(imageNameWithTag).withExposedPorts(tcpInner)
				.withName(name).withPortBindings(portBindings);

		List<String> env = new ArrayList<>();
		if (CommonUtil.hasElement(envs)) {
			for (Map.Entry<String, String> entry : envs.entrySet()) {
				env.add(entry.getKey() + "=" + entry.getValue());
			}
		}
		if (!env.isEmpty()) {
			cmd = cmd.withEnv(env);
		}

		List<Link> link = new ArrayList<>();
		if (CommonUtil.hasElement(links)) {
			for (Map.Entry<String, String> entry : links.entrySet()) {
				link.add(new Link(entry.getKey(), entry.getValue()));
			}
		}
		if (!link.isEmpty()) {
			cmd = cmd.withLinks(link);
		}

		List<Bind> bindList = new ArrayList<>();
		List<Volume> volumeList = new ArrayList<>();
		if (CommonUtil.hasElement(volumes)) {
			for (Map.Entry<String, String> entry : volumes.entrySet()) {
				String hostPath = entry.getKey();
				String containerPath = entry.getValue();
				Volume innerVolume = new Volume(containerPath);
				volumeList.add(innerVolume);
				bindList.add(new Bind(hostPath, innerVolume));
			}
		}
		if (!volumes.isEmpty()) {
			cmd = cmd.withVolumes(volumeList);
			cmd = cmd.withBinds(bindList);
		}
		cmd = cmd.withCmd("true");
		String containerId = cmd.exec().getId();
		logger.info("Created containerId {}", containerId);
		return containerId;
	}

	@Override
	public void removeContainer(String containerId) {
		try {
			dockerClient.removeContainerCmd(containerId).exec();
		} catch (NotModifiedException e) {
			logger.error("No such Container " + containerId);
			// throw new NoSuchContainerException("No such Container " +
			// containerId);
		}
	}

	@Override
	public InspectContainerResponse inspectContainer(String containerId) {
		InspectContainerResponse containerResponse = dockerClient.inspectContainerCmd(containerId).exec();
		return containerResponse;
	}
}

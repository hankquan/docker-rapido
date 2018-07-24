package com.github.howaric.docker_rapido.docker;

import java.util.List;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;

public interface DockerProxy {
    String buildImage(String localDockerfilePath, String... imageTag);

    void pushImage(String imageNameWithRepoAndTag, String username, String password);

    void pullImage(String imageNameWithTag, String username, String password);

    void removeImage(String imageId);

    boolean isImageExits(String imageNameWithTag);

    List<Container> listContainers(boolean isShowAll);

    void stopContainer(String containerId, Integer timeout);

    void startContainer(String containerId);

    void restartContainer(String containerId);

    String createContainer(String name, String imageNameWithTag, List<String> ports, List<String> envs, List<String> links,
            List<String> volumes, List<String> extraHosts);

    void removeContainer(String containerId);

    InspectContainerResponse inspectContainer(String containerId);
}

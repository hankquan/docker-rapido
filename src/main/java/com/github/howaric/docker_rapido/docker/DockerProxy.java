package com.github.howaric.docker_rapido.docker;

import java.util.List;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.howaric.docker_rapido.yaml_model.Service;

public interface DockerProxy {

    String buildImage(String localDockerfilePath, String... imageTag);

    void pushImage(String imageNameWithRepoAndTag, String username, String password);

    void pullImage(String imageNameWithTag, String username, String password);

    void tagImage(String imageId, String imageNameWithRepo, String tag);

    void removeImage(String imageId);

    void tryToRemoveImage(String imageId);

    String isImageExsited(String imageNameWithTag);

    List<Container> listContainers(boolean isShowAll);

    void stopContainer(String containerId, Integer timeout);

    void startContainer(String containerId);

    void restartContainer(String containerId);

    String createContainer(String name, String imageNameWithTag, Service service);

    void removeContainer(String containerId);

    InspectContainerResponse inspectContainer(String containerId);

    void printLogs(String containerId);

}

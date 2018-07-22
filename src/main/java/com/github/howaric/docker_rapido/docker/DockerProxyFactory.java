package com.github.howaric.docker_rapido.docker;

import java.util.HashMap;
import java.util.Map;

public class DockerProxyFactory {

	private static Map<String, DockerProxy> proxyPool = new HashMap<>();

	public static DockerProxy getInstance(String endPoint) {
		DockerProxy dockerProxy = proxyPool.get(endPoint);
		if (dockerProxy == null) {
			dockerProxy = new DefaultDockerProxy(endPoint);
			proxyPool.put(endPoint, dockerProxy);
		}
		return dockerProxy;
	}
	
}

package com.github.howaric.docker_rapido.excutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorsCentre {

	private static ExecutorService mainExecutor = Executors.newFixedThreadPool(5);

	public static ExecutorService getMainExecutor() {
		return mainExecutor;
	}

}

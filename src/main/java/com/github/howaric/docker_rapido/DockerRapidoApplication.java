package com.github.howaric.docker_rapido;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beust.jcommander.JCommander;
import com.github.howaric.docker_rapido.cli.RapidoCliApplication;
import com.github.howaric.docker_rapido.cli.CliOptions;

@SpringBootApplication
public class DockerRapidoApplication {

	private static Logger logger = LoggerFactory.getLogger(DockerRapidoApplication.class);

	public static void main(String[] args) {
		CliOptions cliOptions = new CliOptions();
		JCommander jcommander = JCommander.newBuilder().addObject(cliOptions).build();
		jcommander.parse(args);
		logger.info("Get cli params: " + cliOptions);
		if (cliOptions.isWebMode()) {
			SpringApplication.run(DockerRapidoApplication.class, args);
		} else {
			RapidoCliApplication.run(cliOptions);
		}
	}
}

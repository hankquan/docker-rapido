package com.github.howaric.docker_rapido;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beust.jcommander.JCommander;
import com.github.howaric.docker_rapido.cli.CliOptions;
import com.github.howaric.docker_rapido.cli.RapidoCliApplication;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.utils.RapidoLogCentre;

@SpringBootApplication
public class DockerRapidoApplication {

	private static final String logDir = "log.dir";
	private static final String DOCKER_RAPIDO = "docker-rapido";

	public static void main(String[] args) {
		// RapidoLogCentre.printLinsInBox(getBanner());
		printBanner();
		CliOptions cliOptions = new CliOptions();
		JCommander jcommander = JCommander.newBuilder().addObject(cliOptions).build();
		jcommander.parse(args);
		jcommander.setProgramName(DOCKER_RAPIDO);
		if (cliOptions.isHelp() || cliOptions.needShowUsage()) {
			jcommander.usage();
			return;
		}
		setLogDir(cliOptions);
		Logger logger = LoggerFactory.getLogger(DockerRapidoApplication.class);
		RapidoLogCentre.printLinsInBox(getBanner());
		logger.info("Get cli params:\n\n{}\n", CommonUtil.prettyJson(cliOptions));
		if (cliOptions.isWebMode()) {
			SpringApplication.run(DockerRapidoApplication.class, args);
		} else {
			RapidoCliApplication.run(cliOptions);
		}
	}

	private static void printBanner() {
		File bannerFile = CommonUtil.readTemplateFile("classpath:banner.txt");
		try {
			System.out.println(FileUtils.readFileToString(bannerFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> getBanner() {
		Arrays.asList("Welcome to use docker-rapido!");
		List<String> result = new ArrayList<>();
		File bannerFile = CommonUtil.readResourcesFile("banner.txt");
		try {
			result = FileUtils.readLines(bannerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void setLogDir(CliOptions cliOptions) {
		System.setProperty(logDir, cliOptions.getLogDir() + File.separator);
	}
}

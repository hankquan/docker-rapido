package com.github.howaric.docker_rapido.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.github.howaric.docker_rapido.DockerRapidoApplication;
import com.github.howaric.docker_rapido.core.RapidoEngine;

public class RapidoCliApplication {

	private static Logger logger = LoggerFactory.getLogger(DockerRapidoApplication.class);

	public static int run(CliOptions cliOptions) {
		// String templateFilePath = cliOptions.getTemplateFilePath();
		// validate
		File templateFile = readTemplateFile("classpath:template3.yml");
		if (templateFile == null) {
			return 1;
		}
		List<String> imageTags = cliOptions.getImageTag();
		RapidoEngine rapidoEngine = new RapidoEngine(templateFile, imageTags);
		rapidoEngine.startRapido();
		return 0;
	}

	private static File readTemplateFile(String path) {
		try {
			File file = ResourceUtils.getFile(path);
			return file;
		} catch (FileNotFoundException e) {
			logger.error("Template file doesn't exist: {}", path);
			e.printStackTrace();
		}
		return null;
	}

}

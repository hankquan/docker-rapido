package com.github.howaric.docker_rapido.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.core.RapidoEngine;
import com.github.howaric.docker_rapido.exceptions.TemplateResolveException;
import com.github.howaric.docker_rapido.utils.CommonUtil;

public class RapidoCliApplication {

	private static Logger logger = LoggerFactory.getLogger(RapidoCliApplication.class);

	public static void run(CliOptions cliOptions) {
		String templateFilePath = cliOptions.getTemplateFilePath();
		File templateFile = CommonUtil.readTemplateFile(templateFilePath);
		if (templateFile == null) {
			throw new TemplateResolveException("Failed to get template file");
		}
		try {
			logger.info("Template yml content:\n\n{}\n", FileUtils.readFileToString(templateFile, "utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> imageTags = cliOptions.getImageTag();
		RapidoEngine rapidoEngine = new RapidoEngine(templateFile, imageTags, cliOptions.isDeclareOfficial());
		rapidoEngine.startRapido();
	}

}

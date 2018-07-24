package com.github.howaric.docker_rapido.cli;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.github.howaric.docker_rapido.DockerRapidoApplication;
import com.github.howaric.docker_rapido.core.RapidoEngine;
import com.github.howaric.docker_rapido.exceptions.TemplateResolveException;

public class RapidoCliApplication {

    private static Logger logger = LoggerFactory.getLogger(DockerRapidoApplication.class);

    public static void run(CliOptions cliOptions) {
        String templateFilePath = cliOptions.getTemplateFilePath();
        File templateFile = readTemplateFile(templateFilePath);
        if (templateFile == null) {
            throw new TemplateResolveException("Failed to get template file");
        }
        List<String> imageTags = cliOptions.getImageTag();
        RapidoEngine rapidoEngine = new RapidoEngine(templateFile, imageTags, cliOptions.isDeclareOfficial());
        rapidoEngine.startRapido();
    }

    private static File readTemplateFile(String path) {
        try {
            File file = ResourceUtils.getFile(path);
            logger.info("Template yml:\n{}", FileUtils.readFileToString(file));
            return file;
        } catch (Exception e) {
            logger.error("Template file doesn't exist: {}", path);
            e.printStackTrace();
        }
        return null;
    }

}

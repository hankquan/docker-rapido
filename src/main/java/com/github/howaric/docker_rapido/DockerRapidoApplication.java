package com.github.howaric.docker_rapido;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beust.jcommander.JCommander;
import com.github.howaric.docker_rapido.cli.RapidoCliApplication;
import com.github.howaric.docker_rapido.cli.CliOptions;

import java.io.File;

@SpringBootApplication
public class DockerRapidoApplication {

    private static final String logDir = "log.dir";

    public static void main(String[] args) {
        CliOptions cliOptions = new CliOptions();
        JCommander jcommander = JCommander.newBuilder().addObject(cliOptions).build();
        jcommander.parse(args);
        setLogDir(cliOptions);
        Logger logger = LoggerFactory.getLogger(DockerRapidoApplication.class);
        logger.info("Get cli params: " + cliOptions);
        if (cliOptions.isWebMode()) {
            SpringApplication.run(DockerRapidoApplication.class, args);
        } else {
            RapidoCliApplication.run(cliOptions);
        }
    }

    private static void setLogDir(CliOptions cliOptions) {
        if (!Strings.isNullOrEmpty(cliOptions.getLogDir())) {
            System.setProperty(logDir, cliOptions.getLogDir() + File.separator);
        } else {
            System.setProperty(logDir, "." + File.separator);
        }
    }
}

package com.github.howaric.docker_rapido;

import java.io.File;
import java.util.Arrays;

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
        CliOptions cliOptions = new CliOptions();
        JCommander jcommander = JCommander.newBuilder().addObject(cliOptions).build();
        jcommander.parse(args);
        jcommander.setProgramName(DOCKER_RAPIDO);
        if (cliOptions.isHelp() || cliOptions.needShowUsage()) {
            jcommander.usage();
            return;
        }
        setLogDir(cliOptions);
        printBanner();
        RapidoLogCentre.printLinsInBox(Arrays.asList("IsDeclaredOfficial: " + cliOptions.isDeclareOfficial(),
                "TemplateFile: " + cliOptions.getTemplateFilePath(), "ToBuildImageTag: " + cliOptions.getImageTag()));
        if (cliOptions.isWebMode()) {
            SpringApplication.run(DockerRapidoApplication.class, args);
        } else {
            RapidoCliApplication.run(cliOptions);
        }
    }

    private static void printBanner() {
        String result = CommonUtil.readResourcesFileToString("banner.txt");
        System.out.println(result);
    }

    private static void setLogDir(CliOptions cliOptions) {
        System.setProperty(logDir, cliOptions.getLogDir() + File.separator);
    }
}

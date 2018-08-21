package com.github.howaric.docker_rapido;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beust.jcommander.JCommander;
import com.github.howaric.docker_rapido.core.CliOptions;
import com.github.howaric.docker_rapido.core.RapidoEngine;
import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.utils.RapidoLogCentre;
import com.google.common.base.Strings;

@SpringBootApplication
public class DockerRapidoApplication {

    private static final String logDir = "log.dir";
    private static final String DOCKER_RAPIDO = "docker-rapido";
    private static final String BANNER = "banner.txt";

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
        printCliOptions(cliOptions);
        if (cliOptions.isWebMode()) {
            SpringApplication.run(DockerRapidoApplication.class, args);
        } else {
            RapidoEngine.run(cliOptions);
        }
    }

    private static void printBanner() {
        System.out.println(CommonUtil.readResourcesFileToString(BANNER));
    }

    private static void printCliOptions(CliOptions cliOptions) {
        List<String> lines = new ArrayList<>();
        Field[] fields = cliOptions.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object object = field.get(cliOptions);
                if (object == null) {
                    continue;
                }
                String value = object.toString();
                if (!Strings.isNullOrEmpty(value)) {
                    lines.add(field.getName() + ": " + value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RapidoLogCentre.printLinsInBox(lines, 20);
    }

    private static void setLogDir(CliOptions cliOptions) {
        System.setProperty(logDir, cliOptions.getLogDir() + File.separator);
    }
}

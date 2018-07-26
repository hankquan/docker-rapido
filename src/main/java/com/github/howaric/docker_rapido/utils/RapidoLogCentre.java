package com.github.howaric.docker_rapido.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapidoLogCentre {

    private static Logger logger = LoggerFactory.getLogger(RapidoLogCentre.class);

    public static void successfulExit() {
        logger.info("");
        logger.info("+----------------------------------------------------------------------+");
        logger.info("|                Delivery successfully finished!!!                     |");
        logger.info("|    Thank you for using docker-rapido, any questions or suggestions   |");
        logger.info("|    Please contact team sloth: PDLIDTAOTE@pdl.internal.ericsson.com   |");
        logger.info("+----------------------------------------------------------------------+");
    }

}

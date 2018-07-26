package com.github.howaric.docker_rapido.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapidoLogCentre {

    private static Logger logger = LoggerFactory.getLogger(RapidoLogCentre.class);

    public static void successfulExit() {
        logger.info("");
        logger.info("+----------------------------------------------------------------------+");
        logger.info("|                Delivery successfully finished.                       |");
        logger.info("|     Thank you for using docker-rapido, have a nice day!              |");
        logger.info("+----------------------------------------------------------------------+");
    }

}

package com.github.howaric.docker_rapido.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.utils.RapidoLogCentre;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;

public class CleanTaskHandler implements TaskHandler {

    private static Logger logger = LoggerFactory.getLogger(CleanTaskHandler.class);

    private RapidoTemplate rapidoTemplate;
    private String serviceName;
    private List<Node> targetNodes;

    @Override
    public void runTask() {
        RapidoLogCentre.printEmptyLine();
        RapidoLogCentre.printInCentreWithStar("Start clean task: " + serviceName);
        logger.info("Get clean task: {}, rapido will clean this service from nodes: {}", serviceName, targetNodes);
        
        
        
        
    }

}

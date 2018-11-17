package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.core.dto.ProcessorInfo;
import com.github.howaric.docker_rapido.core.dto.ProcessorInfoFactory;
import com.github.howaric.docker_rapido.utils.LogUtil;
import com.github.howaric.docker_rapido.yaml_model.Node;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;
import com.github.howaric.docker_rapido.yaml_model.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CleanTaskHandler implements TaskHandler {

    private static Logger logger = LoggerFactory.getLogger(CleanTaskHandler.class);

    private RapidoTemplate rapidoTemplate;
    private String serviceName;
    private List<Node> targetNodes;
    private Service service;

    public CleanTaskHandler(RapidoTemplate rapidoTemplate, String serviceName, Service service, List<Node> targetNodes) {
        this.rapidoTemplate = rapidoTemplate;
        this.serviceName = serviceName;
        this.targetNodes = targetNodes;
        this.service = service;
    }

    @Override
    public void runTask() {
        LogUtil.printEmptyLine();
        LogUtil.printInCentreWithStar("Start clean task: " + serviceName);
        logger.info("Get clean task: {}, rapido will clean this service from nodes: {}", serviceName, targetNodes);

        for (Node node : targetNodes) {
            String owner = rapidoTemplate.getOwner();
            ProcessorInfo processorInfo = ProcessorInfoFactory
                    .getCleanProcessorInfo(DeliveryType.getType(rapidoTemplate.getDelivery_type()), owner, node, service, serviceName);
            NodeProcessorFactory.getCleanProcessor().process(processorInfo);
        }

    }

}

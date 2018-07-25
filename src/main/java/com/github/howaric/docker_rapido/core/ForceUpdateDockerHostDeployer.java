package com.github.howaric.docker_rapido.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.utils.CommonUtil;

public class ForceUpdateDockerHostDeployer extends RollingUpdateDockerHostDeployer {

    private static Logger logger = LoggerFactory.getLogger(ForceUpdateDockerHostDeployer.class);

    @Override
    protected void perform() {
        logger.info("Delete all existed containers");
        while (CommonUtil.hasElement(current)) {
            removeCurrentContainer();
        }
        super.perform();
    }


}

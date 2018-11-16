package com.github.howaric.docker_rapido.core;

import com.github.howaric.docker_rapido.utils.CommonUtil;

public class CommonCleanProcessor extends AbstractNodeProcessor {

    @Override
    protected void perform() {
        while (CommonUtil.hasElement(current)) {
            removeCurrentContainer();
        }
    }

}

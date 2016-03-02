package com.mobanker.tkj.cs.mns.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.mns.handler
 * Description :
 * Author : cailinfeng
 * Date : 2016/2/26
 */
public class TestConsumer extends SimplifiedComsumer {

    private static final Logger logger  = LoggerFactory.getLogger(TestConsumer.class);

    @Override
    public void consume(String data) {
        logger.debug("===============>"+data);
    }
}

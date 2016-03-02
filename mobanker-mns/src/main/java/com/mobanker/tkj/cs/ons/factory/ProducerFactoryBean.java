package com.mobanker.tkj.cs.ons.factory;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Properties;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.ons
 * Description :
 * Author : cailinfeng
 * Date : 2016/3/1
 */
public class ProducerFactoryBean implements FactoryBean<Producer>,ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Properties configProperties;

    @Override
    public Producer getObject() throws Exception {
        Producer producer = ONSFactory.createProducer(configProperties);
        producer.start();
        return producer;
    }

    @Override
    public Class<?> getObjectType() {
        return Producer.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }
}

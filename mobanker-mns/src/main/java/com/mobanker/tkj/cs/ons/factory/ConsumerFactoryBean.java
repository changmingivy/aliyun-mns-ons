package com.mobanker.tkj.cs.ons.factory;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
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
public class ConsumerFactoryBean implements FactoryBean<Consumer>,ApplicationContextAware,InitializingBean {

    private ApplicationContext applicationContext;

    private Properties configProperties;

    private MessageListener messageListener;

    private String topicName;

    private String subExpression;

    public ConsumerFactoryBean(Properties configProperties){
        this.configProperties = configProperties;
    }


    @Override
    public Consumer getObject() throws Exception {
        Consumer consumer = ONSFactory.createConsumer(configProperties);
        consumer.subscribe(topicName,subExpression,messageListener);
        consumer.start();
        return consumer;
    }

    @Override
    public Class<?> getObjectType() {
        return Consumer.class;
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

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getObject();
    }
}

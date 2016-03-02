package com.mobanker.tkj.cs.mns.factory;

import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.TopicMeta;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.mns.factory
 * Description :
 * Author : cailinfeng
 * Date : 2016/2/26
 */
public class CloudTopicFactoryBean implements FactoryBean<CloudTopic>,ApplicationContextAware {

    private ApplicationContext applicationContext;

    private MNSClient client;

    private TopicMeta topicMeta;

    @Override
    public CloudTopic getObject() throws Exception {
        return getClient().createTopic(topicMeta);
    }

    @Override
    public Class<?> getObjectType() {
        return CloudTopic.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public MNSClient getClient() {
        return (MNSClient) applicationContext.getBean("client");
    }

    public TopicMeta getTopicMeta() {
        return topicMeta;
    }

    public void setTopicMeta(TopicMeta queueMeta) {
        this.topicMeta = queueMeta;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}

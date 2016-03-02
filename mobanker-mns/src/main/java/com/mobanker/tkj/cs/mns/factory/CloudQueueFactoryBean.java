package com.mobanker.tkj.cs.mns.factory;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.QueueMeta;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.mns.factory
 * Description :
 * Author : cailinfeng
 * Date : 2016/2/24
 */
public class CloudQueueFactoryBean implements FactoryBean<CloudQueue>,ApplicationContextAware {

    private ApplicationContext applicationContext;

    private MNSClient client;

    private QueueMeta queueMeta;

    @Override
    public CloudQueue getObject() throws Exception {
        return getClient().createQueue(queueMeta);
    }

    @Override
    public Class<?> getObjectType() {
        return CloudQueue.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public MNSClient getClient() {
        return (MNSClient) applicationContext.getBean("client");
    }

    public QueueMeta getQueueMeta() {
        return queueMeta;
    }

    public void setQueueMeta(QueueMeta queueMeta) {
        this.queueMeta = queueMeta;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}

package com.mobanker.tkj.cs.mns.factory;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
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
public class MNSClientFactoryBean implements FactoryBean<MNSClient>, BeanNameAware, ApplicationContextAware{

    private String beanName;

    private ApplicationContext applicationContext;

    private CloudAccount cloudAccount;

    private MNSClient client;

    private MNSClientFactoryBean(){

    }

    public MNSClientFactoryBean(CloudAccount cloudAccount){
        this.cloudAccount = cloudAccount;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName=beanName;
    }

    @Override
    public MNSClient getObject() throws Exception {
        this.client = cloudAccount.getMNSClient();
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return MNSClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void clear(){
        if (client.isOpen()) {
            client.close();
        }
    }
}

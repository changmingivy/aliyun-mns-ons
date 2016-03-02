package com.mobanker.tkj.cs.listener;

import com.mobanker.tkj.cs.mns.mqService.HttpEndpointCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

/**
 * Created by fancongchun on 2015/11/26.
 */
@Component
public class InitListener implements InitializingBean,ServletContextAware {

    private static final Logger logger = LoggerFactory.getLogger(InitListener.class);

    @Resource
    private HttpEndpointCommon httpEndpointCommon;

    @Override
    public void afterPropertiesSet() throws Exception {

    }
    @Override
    public void setServletContext(ServletContext servletContext) {
        try {
            httpEndpointCommon.start();
        } catch (Exception e) {
            logger.error("httpEndPoint 启动异常 :"+e.getMessage());
        }
    }

}

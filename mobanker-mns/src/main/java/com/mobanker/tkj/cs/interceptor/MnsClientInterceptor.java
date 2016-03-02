package com.mobanker.tkj.cs.interceptor;

import com.aliyun.mns.client.MNSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs
 * Description : 用户系统拦截器，拦截所有Controller
 * Author : cailinfeng
 * Date : 2015/12/1
 */
public class MnsClientInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MnsClientInterceptor.class);

    @Resource
    private MNSClient client;

    @Override
    /**
    * 方法 afterCompletion 功能描述 ：
     * 请求结束后，清空SessionUtils线程绑定的user ThreadLocal变量，防止tomcat线程池的线程复用而导致变量串用
    * @author cailinfeng
    * @createTime 2015/12/1
    * @param [request, response, handler, ex]
    * @return void
    *
    */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(client.isOpen()){
//            client.close();
        }
        logger.debug(client.isOpen() ? "true" : "false");
    }
}

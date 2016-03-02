package com.mobanker.tkj.cs.web;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.TopicMessage;
import com.mobanker.framework.constant.Constants;
import com.mobanker.framework.dto.ResponseEntity;
import com.mobanker.tkj.cs.mns.mqService.HttpEndpointCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.web
 * Description :
 * Author : cailinfeng
 * Date : 2016/2/25
 */
@Controller
@RequestMapping("mq")
public class MqController {

    private static final Logger logger = LoggerFactory.getLogger(MqController.class);

    @Resource
    private CloudQueue sendPollQueue;

    @Resource
    private CloudTopic sendPollTopic;


    @ResponseBody
    @RequestMapping(value = "send")
    public ResponseEntity send(){
        ResponseEntity responseEntity = new ResponseEntity();
        String msg = "mobanker-队列测试";
        Message msgBody = new Message();
        msgBody.setMessageBody(msg);
        try {
            Message sendResult = sendPollQueue.putMessage(msgBody);
            if (sendResult != null && !StringUtils.isEmpty(sendResult.getMessageId())) {
                responseEntity.setStatus(Constants.System.OK);
                responseEntity.setMsg("success");
            }else{
                responseEntity.setStatus(Constants.System.FAIL);
                responseEntity.setMsg("fail");
            }
        } catch (Exception e) {
            responseEntity.setStatus(Constants.System.FAIL);
            responseEntity.setMsg("fail");
        }
        return responseEntity;
    }

    @ResponseBody
    @RequestMapping(value = "poll")
    public ResponseEntity poll(){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            Message sendResult = sendPollQueue.popMessage();
            if (sendResult != null && !StringUtils.isEmpty(sendResult.getMessageId())) {
                responseEntity.setStatus(Constants.System.OK);
                responseEntity.setMsg("success");
                responseEntity.setData(sendResult.getMessageBodyAsString("utf-8"));
                if(!StringUtils.isEmpty(sendResult.getReceiptHandle())){
                    sendPollQueue.deleteMessage(sendResult.getReceiptHandle());
                }
            }else{
                responseEntity.setStatus("-1");
                responseEntity.setMsg("no more message");
            }
        } catch (Exception e) {
            responseEntity.setStatus(Constants.System.FAIL);
            responseEntity.setMsg("fail");
        }
        return responseEntity;
    }


    @ResponseBody
    @RequestMapping(value = "sendTopic")
    public ResponseEntity sendTopic(){
        ResponseEntity responseEntity = new ResponseEntity();
        String msg = "mobanker-topicTestMsg";
        TopicMessage topicMessage = new Base64TopicMessage();
        try {
            topicMessage.setMessageBody(msg);
            TopicMessage sendResult = sendPollTopic.publishMessage(topicMessage);
            if (sendResult != null && !StringUtils.isEmpty(sendResult.getMessageId())) {
                responseEntity.setStatus(Constants.System.OK);
                responseEntity.setMsg("success");
            }else{
                responseEntity.setStatus(Constants.System.FAIL);
                responseEntity.setMsg("fail");
            }
        } catch (Exception e) {
            responseEntity.setStatus(Constants.System.FAIL);
            responseEntity.setMsg("fail");
        }
        return responseEntity;
    }

    @ResponseBody
    @RequestMapping(value = "subTopic")
    public ResponseEntity subTopic(){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            SubscriptionMeta subMeta = new SubscriptionMeta();
            subMeta.setSubscriptionName("sendPollTopicSub");
            //Endpoint 的一级目录设置为 "/simplified"，需要在HttpServer有对应的请求处理Handler
//            String endpoint = HttpEndpointCommon.GenEndpointLocal(11223) + "/notifications";
//            String endpoint = HttpEndpointCommon.GenEndpoint() + ":11223/notifications";
            String endpoint = HttpEndpointCommon.GenEndpointLocal() + "/notifications";
            subMeta.setEndpoint(endpoint);
            subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.XML);
            sendPollTopic.subscribe(subMeta);
            responseEntity.setStatus(Constants.System.OK);
            responseEntity.setMsg("success");
        } catch (Exception e) {
            responseEntity.setStatus(Constants.System.FAIL);
            responseEntity.setMsg("fail");
        }finally {
//            ep.stop();
        }
        return responseEntity;
    }
}

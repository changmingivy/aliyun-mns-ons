package com.mobanker.tkj.cs.mns.mqService;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.mobanker.tkj.cs.mns.utils.ObjectByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.mns.mqService
 * Description :
 * Author : cailinfeng
 * Date : 2016/2/24
 */
//@Component
public class Producer {

    private static String QUEUE_NAME = "MOBANKER-TEST-QUEUE";

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Resource
    private CloudQueue mobankerQueue;

    public void testClient(){
//        ResponseEntity responseEntity = new ResponseEntity();
//        responseEntity.setStatus("1");
//        responseEntity.setMsg("mobanke测试消息");

        // 发送消息
        Message message = new Message();
        message.setMessageBody(ObjectByteUtil.toByteArray("mobanke测试消息"));

        //设置被消费次数
        message.setDequeueCount(1);
        Message putMsg = mobankerQueue.putMessage(message);
        System.out.println("PutMessage has MsgId: " + putMsg.getMessageId());

        Message receiveMessage = mobankerQueue.popMessage();
        if (receiveMessage != null ) {
//            mobankerQueue.deleteMessage(receiveMessage.getReceiptHandle());
            String reveiveResponse = (String) ObjectByteUtil.toObject(receiveMessage.getMessageBodyAsBytes());
            logger.debug(reveiveResponse.toString());
        }else{
            logger.debug("no message pop");
        }
    }

}

package com.mobanker.tkj.cs.ons.messageListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.ons.messageListener
 * Description :
 * Author : cailinfeng
 * Date : 2016/3/1
 */
public class MobMessageListener implements MessageListener {

    public Logger logger = LoggerFactory.getLogger(MobMessageListener.class);

    private static Long receiveCount = 0l;

    private static Long receiveCountActual = 0l;

    private static String tempId = "";

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        receiveCount += 1;
        String msgid = message.getMsgID();

        String tempMsgId = msgid;
        if( !tempId.equals(tempMsgId) ){
            receiveCountActual += 1 ;
            tempId = msgid;
        }
        if(receiveCount%100 == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("message received from server: totalSend :"+receiveCount+", actualReceived :" + receiveCountActual);
            logger.warn(stringBuilder.toString());
        }
//        try {
//            System.err.println("+====>"+new String(message.getBody(),"utf-8"));
//        } catch (UnsupportedEncodingException e) {
//        }
        return Action.CommitMessage;
    }
}

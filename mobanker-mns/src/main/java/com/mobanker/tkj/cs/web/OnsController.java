package com.mobanker.tkj.cs.web;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.mobanker.framework.constant.Constants;
import com.mobanker.framework.dto.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.web
 * Description :
 * Author : cailinfeng
 * Date : 2016/3/1
 */
@Controller
@RequestMapping("ons")
public class OnsController {

    @Resource
    private Message TopicMobanker;

    @Resource
    private Producer producer;

    private static final Logger logger = LoggerFactory.getLogger(OnsController.class);

    @ResponseBody
    @RequestMapping(value = "send",method = RequestMethod.GET)
    public ResponseEntity send(@RequestParam String content){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            TopicMobanker.setBody(content.getBytes());
//            TopicMobanker.setKey("mobMnsKey");
            producer.send(TopicMobanker);
            responseEntity.setStatus(Constants.System.OK);
            responseEntity.setMsg("success");
        } catch (Exception e) {
            logger.error(e.getMessage());
            responseEntity.setStatus(Constants.System.FAIL);
            responseEntity.setMsg("fail"+e.getMessage());
        }
        return responseEntity;
    }
}

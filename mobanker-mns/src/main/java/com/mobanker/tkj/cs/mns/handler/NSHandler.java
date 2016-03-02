package com.mobanker.tkj.cs.mns.handler;

import com.mobanker.tkj.cs.mns.utils.MnsUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.mns.handler
 * Description :
 * Author : cailinfeng
 * Date : 2016/3/1
 */
public class NSHandler implements HttpRequestHandler {
    public Logger logger = LoggerFactory.getLogger(NSHandler.class);

    private static Long receiveCount = 0l;

    private static Long receiveCountActual = 0l;

    private static String tempId = "";

    public NSHandler() {
        super();
    }

    private String safeGetElementContent(Element element, String tag) {
        NodeList nl = element.getElementsByTagName(tag);
        if (nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        } else {
            logger.warn("get " + tag + " from xml fail");
            return "";
        }
    }

    /**
     * parser /notifications message content
     * @param notify, xml element
     */
    private void paserContent(Element notify) {
        try {
            String topicOwner = safeGetElementContent(notify, "TopicOwner");
//            System.out.println("TopicOwner:\t" + topicOwner);

            String topicName = safeGetElementContent(notify, "TopicName");
//            System.out.println("TopicName:\t" + topicName);

            String subscriber = safeGetElementContent(notify, "Subscriber");
//            System.out.println("Subscriber:\t" + subscriber);

            String subscriptionName = safeGetElementContent(notify, "SubscriptionName");
//            System.out.println("SubscriptionName:\t" + subscriptionName);

            String msgid = safeGetElementContent(notify, "MessageId");
//            System.out.println("MessageId:\t" + msgid);

            // if PublishMessage with base64 message
            String msg = safeGetElementContent(notify, "Message");
//            System.out.println("Message:\t" + new String(Base64.decodeBase64(msg)));

            //if PublishMessage with string message
//            msg = safeGetElementContent(notify, "Message");
//            System.out.println("Message:\t" + msg);
//            logger.debug("Message:\t" + msg);

            String msgMD5 = safeGetElementContent(notify, "MessageMD5");
//            System.out.println("MessageMD5:\t" + msgMD5);

            String msgPublishTime = safeGetElementContent(notify, "PublishTime");
            Date d = new Date(Long.parseLong(msgPublishTime));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strdate = sdf.format(d);
//            System.out.println("PublishTime:\t" + strdate);
            receiveCount += 1;


            String tempMsgId = msgid;
            if( !tempId.equals(tempMsgId) ){
                receiveCountActual += 1 ;
                tempId = msgid;
            }

            if(receiveCount%100 == 0){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("TopicOwner:\t" + topicOwner);
                stringBuilder.append("TopicName:\t" + topicName);
                stringBuilder.append("Subscriber:\t" + subscriber);
                stringBuilder.append("SubscriptionName:\t" + subscriptionName);
                stringBuilder.append("Message:\t" + new String(Base64.decodeBase64(msg)));
                stringBuilder.append("MessageMD5:\t" + msgMD5);
                stringBuilder.append("MessagePublishTime:\t" + strdate);
                stringBuilder.append("message received from server: totalSend :"+receiveCount+", actualReceived :" + receiveCountActual);
                logger.warn(stringBuilder.toString());
            }
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            e.printStackTrace();
            logger.warn(e.getMessage());
        }


    }


    /**
     * process method for NSHandler
     * @param request, http request
     * @param response, http responst
     * @param context, http context
     * @throws HttpException
     * @throws IOException
     */
    public void handle(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);

        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }

        Header[] headers = request.getAllHeaders();
        Map<String, String> hm = new HashMap<String, String>();
        for (Header h : headers) {
//            System.out.println(h.getName() + ":" + h.getValue());
            hm.put(h.getName(), h.getValue());
        }

        String target = request.getRequestLine().getUri();
//        System.out.println(target);


        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

            //parser xml content
            InputStream content = entity.getContent();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Element notify = null;
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(content);
                NodeList nl = document.getElementsByTagName("Notification");
                if (nl == null || nl.getLength() == 0) {
//                    System.out.println("xml tag error");
                    logger.warn("xml tag error");
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                    return;
                }
                notify = (Element) nl.item(0);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                logger.warn("xml parser fail! " + e.getMessage());
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            } catch (SAXException e) {
                e.printStackTrace();
                logger.warn("xml parser fail! " + e.getMessage());
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }

            //verify request
            Header certHeader = request.getFirstHeader("x-mns-signing-cert-url");
            if (certHeader == null) {
//                System.out.println("SigningCerURL Header not found");
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }

            String cert = certHeader.getValue();
            if (cert.isEmpty()) {
//                System.out.println("SigningCertURL empty");
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }
            cert = new String(Base64.decodeBase64(cert));
//            System.out.println("SigningCertURL:\t" + cert);
            logger.debug("SigningCertURL:\t" + cert);


            if (!MnsUtil.authenticate(method, target, hm, cert)) {
//                System.out.println("authenticate fail");
                logger.warn("authenticate fail");
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }
            paserContent(notify);

        }

        response.setStatusCode(HttpStatus.SC_NO_CONTENT);
    }

}

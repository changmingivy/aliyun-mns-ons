package com.mobanker.tkj.cs.mns.handler;

import com.mobanker.tkj.cs.mns.utils.MnsUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimplifiedNSHandler implements HttpRequestHandler {

    private static final Logger logger  = LoggerFactory.getLogger(SimplifiedNSHandler.class);
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
            System.out.println(h.getName() + ":" + h.getValue());
            hm.put(h.getName(), h.getValue());
        }

        String target = request.getRequestLine().getUri();
        System.out.println(target);


        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();


            //verify request
            Header certHeader = request.getFirstHeader("x-mns-signing-cert-url");
            if (certHeader == null) {
                System.out.println("SigningCerURL Header not found");
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }

            String cert = certHeader.getValue();
            if (cert.isEmpty()) {
                System.out.println("SigningCertURL empty");
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }
            cert = new String(Base64.decodeBase64(cert));
            System.out.println("SigningCertURL:\t" + cert);
            logger.debug("SigningCertURL:\t" + cert);


            if (!MnsUtil.authenticate(method, target, hm, cert)) {
                System.out.println("authenticate fail");
                logger.warn("authenticate fail");
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }

            //parser content of simplified notification
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String content = buffer.toString();
//            this.consume(content);
            System.out.println("Simplified Notification: \n" + content);
        }
        response.setStatusCode(HttpStatus.SC_NO_CONTENT);
    }
}

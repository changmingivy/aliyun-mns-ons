package com.mobanker.tkj.cs.mns.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.mns.utils
 * Description :
 * Author : cailinfeng
 * Date : 2016/2/26
 */
public class MnsUtil {

    private static final Logger logger  = LoggerFactory.getLogger(MnsUtil.class);

    /**
     * check if this request comes from MNS Server
     * @param method, http method
     * @param uri, http uri
     * @param headers, http headers
     * @param cert, cert url
     * @return true if verify pass
     */
    public static Boolean authenticate(String method, String uri, Map<String, String> headers, String cert) {
        String str2sign = getSignStr(method, uri, headers);
        //System.out.println(str2sign);
        String signature = headers.get("Authorization");
        byte[] decodedSign = Base64.decodeBase64(signature);
        //get cert, and verify this request with this cert
        try {
            //String cert = "http://mnstest.oss-cn-hangzhou.aliyuncs.com/x509_public_certificate.pem";
            URL url = new URL(cert);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            DataInputStream in = new DataInputStream(conn.getInputStream());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            Certificate c = cf.generateCertificate(in);
            PublicKey pk = c.getPublicKey();

            java.security.Signature signetcheck = java.security.Signature.getInstance("SHA1withRSA");
            signetcheck.initVerify(pk);
            signetcheck.update(str2sign.getBytes());
            Boolean res = signetcheck.verify(decodedSign);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("authenticate fail, " + e.getMessage());
            return false;
        }
    }


    /**
     * build string for sign
     * @param method, http method
     * @param uri, http uri
     * @param headers, http headers
     * @return String fro sign
     */
    public static String getSignStr(String method, String uri, Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append(method);
        sb.append("\n");
        sb.append(safeGetHeader(headers, "Content-md5"));
        sb.append("\n");
        sb.append(safeGetHeader(headers, "Content-Type"));
        sb.append("\n");
        sb.append(safeGetHeader(headers, "Date"));
        sb.append("\n");

        List<String> tmp = new ArrayList<String>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().startsWith("x-mns-"))
                tmp.add(entry.getKey() + ":" + entry.getValue());
        }
        Collections.sort(tmp);

        for (String kv : tmp) {
            sb.append(kv);
            sb.append("\n");
        }

        sb.append(uri);
        return sb.toString();
    }

    public static String safeGetHeader(Map<String, String> headers, String name) {
        if (headers.containsKey(name))
            return headers.get(name);
        else
            return "";
    }

}

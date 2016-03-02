/**
 * HttpEndpoint类可以作为一个完整的MNS的notification的Endpint实现使用。
 * 实现功能：
 * 1：在本起启动一个http服务
 * 2：接收发到/notifications的请求
 * 3：解析并验证发送到/notifications的请求
 *
 * HttpEndpoint类不依赖MNS的JAVA SDK, 但依赖apache的httpcomponents。如果你的项目用maven管理，
 * 请在pom中添加以下依赖：
 * <dependency>
 *   <groupId>org.apache.httpcomponents</groupId>
 *   <artifactId>httpasyncclient</artifactId>
 *   <version>4.0.1</version>
 * </dependency>
 */


package com.mobanker.tkj.cs.mns.mqService;

import com.mobanker.tkj.cs.mns.handler.NSHandler;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * HTTP/1.1 file server,处理发送到/notifications的请求
 */
public class HttpEndpointCommon {
    public static Logger logger = LoggerFactory.getLogger(HttpEndpointCommon.class);
    public static Thread t;
    private int port;

    /**
     * 静态方法，使用本机地址用于生成一个endpoint地址
     * @return http endpoint
     */
    public static String GenEndpointLocal() {
        return HttpEndpointCommon.GenEndpointLocal(11223);
    }

    /**
     * 静态方法，使用本机地址用于生成一个endpoint地址
     * @param port, http server port
     * @return http endpoint
     */
    public static String GenEndpointLocal(int port) {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress().toString();
            return "http://" + ip + ":" + port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.warn("get local host fail," + e.getMessage());
            return "http://127.0.0.1:" + port;
        }

    }

    public static String GenEndpoint() {
        return "http://116.228.32.182" ;
    }

    /**
     * 构造函数，用指定端口构造HttpEndpoint对象
     * @param port， http server port
     */
    public HttpEndpointCommon(int port) {
        init(port);
    }

    /**
     * 构造函数，构造HttpEndpoint对象,默认80端口
     *
     */
    public HttpEndpointCommon() {
        init(11223);
    }

    private void init(int port){
        this.port = port;
        t = null;
    }

    /**
     * start http server
     * @throws Exception
     */
    public void start() throws Exception {
        //check port if used
        try {
            new Socket(InetAddress.getLocalHost(), this.port);
            System.out.println("port is used!");
            logger.error("port already in use, http server start failed");
            throw new BindException("port already in use");
        } catch (IOException e) {
            //e.printStackTrace();

        }


        // Set up the HTTP protocol processor
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("MNS-Endpoint/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();

        // Set up request handlers, listen /notifications request whit NSHandler class
        UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
        reqistry.register("/notifications", new NSHandler());
//        reqistry.register("/simplified", new SimplifiedNSHandler());

        // Set up the HTTP service
        HttpService httpService = new HttpService(httpproc, reqistry);

        //start thread for http server
        t = new RequestListenerThread(port, httpService, null);
        t.setDaemon(false);
        t.start();
    }

    /**
     * stop http endpoint
     */
    public void stop() {
        if (t != null) {
            t.interrupt();
            try {
                t.join(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("endpoint stop");
    }
    
    /**
     * check if this request comes from MNS Server
     * @param method, http method
     * @param uri, http uri
     * @param headers, http headers
     * @param cert, cert url
     * @return true if verify pass
     */
    private Boolean authenticate(String method, String uri, Map<String, String> headers, String cert) {
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
    private String getSignStr(String method, String uri, Map<String, String> headers) {
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
    
    private String safeGetHeader(Map<String, String> headers, String name) {
        if (headers.containsKey(name))
            return headers.get(name);
        else
            return "";
    }
    


    /**
     * core class for processing /notifications request
     */


    /**
     * http listen work thread
     */
    public class RequestListenerThread extends Thread {

        private final HttpConnectionFactory<DefaultBHttpServerConnection> connFactory;
        private final ServerSocket serversocket;
        private final HttpService httpService;

        public RequestListenerThread(
                final int port,
                final HttpService httpService,
                final SSLServerSocketFactory sf) throws IOException {
            this.connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
            this.serversocket = sf != null ? sf.createServerSocket(port) : new ServerSocket(port);
            this.httpService = httpService;
        }

        @Override
        public void run() {
            System.out.println("Listening on port " + this.serversocket.getLocalPort());
            Thread t = null;
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    System.out.println("Incoming connection from " + socket.getInetAddress());
                    HttpServerConnection conn = this.connFactory.createConnection(socket);

                    // Start worker thread
                    t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (IOException e) {
                    System.err.println("Endpoint http server stop or IO error: "
                            + e.getMessage());
                    try {
                        if (t != null)
                            t.join(5*1000);
                    } catch (InterruptedException e1) {
                        //e1.printStackTrace();
                    }
                    break;
                }
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            try {
                this.serversocket.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * http work thread, it will dispatch /notifications to NSHandler
     */
    public class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(
                final HttpService httpservice,
                final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        @Override
        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }

    }

    /**
     * 简单的使用， main函数demo
     */
    public static void main(String[] args) {
        int port = 8080;
        HttpEndpointCommon httpEndpoint = null;
        try {
            httpEndpoint = new HttpEndpointCommon(port);
            httpEndpoint.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpEndpoint.stop();
        }
    }

}


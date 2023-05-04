package com.fictio.parrot.http.client;

import com.google.gson.Gson;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.net.ssl.*;
import java.lang.reflect.Field;
import java.security.cert.X509Certificate;
import java.util.Arrays;

@SuppressWarnings("SameParameterValue")
@Slf4j
public class WsClientTests {
    private final String URL = "https://192.168.110.13:8100/sys/login";
    private final String CONTENT_TYPE = "application/json";

    static class LogInBody {
        String mobile;
        String password;
        LogInBody setUid(String uid){
            mobile = uid;
            return this;
        }
        LogInBody setPwd(String pwd){
            password = DigestUtils.md5Hex(pwd);
            return this;
        }
        String toJson(){
            return new Gson().toJson(this);
        }
    }


    @Test
    public void tests() {
        HTTPRequest req = WS.url(URL)
                .contentType(CONTENT_TYPE).body(new LogInBody().setUid("15155410918")
                        .setPwd("stpass").toJson());
        resetClient();
        try(final HTTPResponse resp = req.post()){
            log.info("{},{}",resp.getStatus(),resp.getResponseAsString());
        }catch (Exception e){
            e.printStackTrace();
            log.error("{}",e.toString());
        }
    }

    private void resetClient() {
        try {
            Class<HTTPRequest> reqClazz = HTTPRequest.class;
            log.info("{}", Arrays.asList(reqClazz.getDeclaredFields()));
            Field client = reqClazz.getDeclaredField("DEFAULT_HTTP_CLIENT");
            client.setAccessible(true);
            client.set(null, buildSSLIgnoreClient());
        }catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("ResetCloseableHttpClientErr,"+e);
        }
    }

    @Test
    public void sslIgnoreTests(){
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            HostnameVerifier allHostsValid = (s, sslSession) -> true;
            SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(sc,allHostsValid);
            HttpPost post = new HttpPost(URL);

            post.setEntity(new StringEntity(new LogInBody().setUid("15155410918")
                    .setPwd("stpass").toJson(), ContentType.create(CONTENT_TYPE,"utf-8")));
            try(CloseableHttpClient client = HttpClientBuilder.create().setSSLSocketFactory(scsf).build()) {
                CloseableHttpResponse resp = client.execute(post);
                log.info("RS={}", EntityUtils.toString(resp.getEntity(), "utf-8"));
            }
        }catch (Exception e){
            log.error("E={}",e.toString());
            e.printStackTrace();
        }
    }

    private HttpClient buildSSLIgnoreClient(){
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                        }
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            }, null);
            HostnameVerifier allHostsValid = (s, sslSession) -> true;
            SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(sc,allHostsValid);
            return HttpClientBuilder.create().setSSLSocketFactory(scsf).build();
        }catch (Exception e){
            throw new RuntimeException("BuildSSLIgnoreClientErr,"+e);
        }
    }


}

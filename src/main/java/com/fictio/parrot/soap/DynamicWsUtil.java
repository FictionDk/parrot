package com.fictio.parrot.soap;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transport.http.auth.DefaultBasicAuthSupplier;
import org.apache.cxf.transport.http.auth.HttpAuthSupplier;
import org.junit.Test;

import javax.xml.namespace.QName;

@Slf4j
public class DynamicWsUtil {

    @Test public void test(){
        Bus bus = BusFactory.getThreadDefaultBus();
        bus.setExtension((name,address, httpConduit)->{
            final AuthorizationPolicy auth = new AuthorizationPolicy();
            auth.setUserName("stpass");
            auth.setPassword("passinside");
            httpConduit.setAuthorization(auth);
            final HttpAuthSupplier supplier = new DefaultBasicAuthSupplier();
            httpConduit.setAuthSupplier(supplier);
        }, HTTPConduitConfigurer.class);

        String url = "http://127.0.0.1:32009/spring/soap/BloodInfoService?wsdl";
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance(bus);
        Client client = clientFactory.createClient(url);
        QName name = new QName("http://www.stpass.com/aladdin","listAllBlood");
        Object[] objects = null;
        try {
            objects = client.invoke(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("{}", new Gson().toJson(objects));

/*        url = "http://127.0.0.1:32009/spring/soap/LoginService?wsdl";
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance(bus);
        Client client = clientFactory.createClient(url);
        QName name = new QName("http://www.stpass.com/aladdin","login1WithClientId");
        Object[] objects = null;
        try {
            objects = client.invoke(name,"PDA");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("{}", new Gson().toJson(objects));*/
    }



}



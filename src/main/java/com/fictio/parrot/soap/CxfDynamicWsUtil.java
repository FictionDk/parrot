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

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CxfDynamicWsUtil {

    private JaxWsDynamicClientFactory buildClientFactory(){
        Bus bus = BusFactory.getThreadDefaultBus();
        bus.setExtension((name,address, httpConduit)->{
            final AuthorizationPolicy auth = new AuthorizationPolicy();
            auth.setUserName("stpass");
            auth.setPassword("passinside");
            httpConduit.setAuthorization(auth);
            final HttpAuthSupplier supplier = new DefaultBasicAuthSupplier();
            httpConduit.setAuthSupplier(supplier);
        }, HTTPConduitConfigurer.class);
        return JaxWsDynamicClientFactory.newInstance(bus);
    }

    public void invokeWithoutParams(){
        String url = "http://127.0.0.1:32009/spring/soap/BloodInfoService?wsdl";
        JaxWsDynamicClientFactory clientFactory = buildClientFactory();
        Client client = clientFactory.createClient(url);
        QName name = new QName("http://www.stpass.com/aladdin","listAllBlood");
        Object[] objects = null;
        try {
            objects = client.invoke(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("invokeWithoutParams:{}", new Gson().toJson(objects));
    }

    // TODO: 在不引用外部静态包前提下,动态编译和加载类对象过于复杂
    public Object buildParam(List<String> reqList){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        InputStream stringArrInput = this.getClass().getClassLoader().getResourceAsStream("StringArray.java");
        log.debug("{}",stringArrInput);
        int result = compiler.run(stringArrInput,System.out,System.out);
        log.debug("result={}",result);
        Object stringArr = null;
        try {
            stringArr = Thread.currentThread().getContextClassLoader()
                    .loadClass("net.java.dev.jaxb.array.StringArray").newInstance();
            Method setItem = stringArr.getClass().getMethod("setItem", List.class);
            setItem.invoke(stringArr, reqList);
        }catch (Exception e){
            log.error("buildParam ERR:{}",e.toString());
        }
        return stringArr;
    }

    public void invokeWithComplexParams(){
        String url = "http://127.0.0.1:32009/spring/soap/ProductionService?wsdl";
        List<String> reqList = Arrays.asList("020242152073341","020242152075941");
        JaxWsDynamicClientFactory clientFactory = buildClientFactory();
        Object[] objects = null;
        try {
            Object stringArr = buildParam(reqList);
            Client client = clientFactory.createClient(url);
            QName name = new QName("http://www.stpass.com/aladdin","getDonMappingWithBloodGroupBatch");
            // 参数需要严格验证包名
            objects = client.invoke(name, stringArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("invokeWithComplexParams:{}", new Gson().toJson(objects));
    }

    @Test public void test(){
        //invokeWithoutParams();
        invokeWithComplexParams();
    }

}



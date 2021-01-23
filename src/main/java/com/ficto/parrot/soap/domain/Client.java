package com.ficto.parrot.soap.domain;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
    @Test
    public void test() throws MalformedURLException {
        URL wsdlDoc = new URL("http://127.0.0.1:9888/jws/ws?wsdl");
        QName serviceName = new QName("http://service.jws.fictio.com/", "WebSerivceImplService");
        Service service = Service.create(wsdlDoc, serviceName);
        WebSerivceImpl port = service.getPort(WebSerivceImpl.class);
        String resp = port.sayHi("小米");
        log.debug("{}",resp);
    }
}

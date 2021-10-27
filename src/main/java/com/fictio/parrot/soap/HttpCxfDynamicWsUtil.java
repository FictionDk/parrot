package com.fictio.parrot.soap;

import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class HttpCxfDynamicWsUtil {

    public void initDemo(){
        final String wsdlURL = "http://127.0.0.1:32009/spring/soap/ProductionService";
        String soapStart = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:alad=\"http://www.stpass.com/aladdin\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<alad:getDonMappingWithBloodGroupBatch>"
                + "<arg0>"
                + "<item>";
        String soapEnd = "</item>"
                + "</arg0>"
                + "</alad:getDonMappingWithBloodGroupBatch>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
        String item = "020242152073341";
        HTTPRequest request = WS.url(wsdlURL).authBasic("stpass","passinside").body(soapStart+item+soapEnd)
                .contentType("text/xml");
        try(final HTTPResponse response = request.post()){
            log.debug("{}",response.getStatus());
            log.debug("{}",response.getResponseAsString());
        }
    }

    public void xmlParseDemo(){
        final String wsdlURL = "http://127.0.0.1:32009/spring/soap/ProductionService";
        List<String> reqList = Arrays.asList("020242152073341","020242152075941");
        StringBuilder params = new StringBuilder();
        for(String req : reqList){
            params.append("<item>").append(req).append("</item>");
        }
        InputStream wsdlInput = this.getClass().getClassLoader().getResourceAsStream("product.getBlood.xml");
        assert wsdlInput != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(wsdlInput));
        StringBuilder sb = new StringBuilder();
        String s;
        try {
            while (((s=reader.readLine()) != null)) sb.append(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String soapParams = sb.toString().replace("REPLACE",params);
        log.debug("params={}",soapParams);
        String result;
        HTTPRequest request = WS.url(wsdlURL).authBasic("stpass","passinside").body(soapParams)
                .contentType("text/xml");
        try(final HTTPResponse response = request.post()){
            log.debug("{}",response.getStatus());
            result = response.getResponseAsString();
        }

        SAXReader xmlReader = new SAXReader();
        Document document = null;
        try {
            document = xmlReader.read(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        assert document != null;
        List<Blood> bloods = elementsToBlood(document.getRootElement().elements());
        log.debug("1. {}",bloods);
        bloods.clear();
        List<Node> resultEle = document.selectNodes("//item");  // XPath语法
        resultEle.forEach(e->bloods.add(elementToBlood((Element) e)));
        log.debug("2. {}",bloods);
    }

    @AllArgsConstructor
    @ToString
    private static class Blood {
        String sid;
        String group;
        String code;
        String expTime;
    }

    // 使用递归
    private static List<Blood> elementsToBlood(List<Element> elements) {
        List<Blood> bloods = new ArrayList<>();
        elements.forEach(element -> {
            // 转换当前节点
            if ("item".equals(element.getName())) Optional.of(elementToBlood(element)).ifPresent(bloods::add);
            // 递归转换子节点
            else bloods.addAll(elementsToBlood(element.elements()));
        });
        return bloods;
    }

    private static Blood elementToBlood(Element ele){
        return new Blood(ele.elementText("serialId"), ele.elementText("bloodGroup"),
                ele.elementText("product"),ele.elementText("expirationTime"));
    }

    @Test
    public void tests(){
        //initDemo();
        xmlParseDemo();
    }
}

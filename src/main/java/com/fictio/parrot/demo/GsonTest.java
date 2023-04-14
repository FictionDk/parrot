package com.fictio.parrot.demo;


import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GsonTest {
    @Data
    static class MsgData {
        private String entity;
        private String json;
        private String dept;
    }
    @SuppressWarnings("unchecked")
    @Test
    public void test() {

        Map<String, Object> content = new HashMap<>();
        content.put("deptId", 107);
        content.put("seqNumber", "10001025");

        MsgData msg = new MsgData();
        msg.setJson(new Gson().toJson(content));

        String dataJson = new Gson().toJson(msg);

        Map<String, Object> data = new Gson().fromJson(dataJson, Map.class);
        log.debug("{}",data.get("json").getClass());
    }

    @Test
    public void joTest() {
        MsgData msg = new MsgData();
        msg.setJson("100001025");
        msg.setDept("A107");
        String dataJson = new Gson().toJson(msg);
        log.debug("1. {}",dataJson);
        GsonBuilder gb = new GsonBuilder().serializeNulls();
        log.debug("2. {}",gb.create().toJson(msg));
        JsonObject jo = new Gson().fromJson(dataJson, JsonObject.class);
        log.debug("3. {}",jo);
        log.debug("{}",jo.get("dept").isJsonNull());
        log.debug("{}", jo.get("dept").getAsLong());
    }

}

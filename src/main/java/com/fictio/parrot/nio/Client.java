package com.fictio.parrot.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Test;

public class Client {

    @Test public void test() {
        Socket client = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            client = new Socket();
            client.connect(new InetSocketAddress("127.0.0.1",8666));
            writer = new PrintWriter(client.getOutputStream(),true);
            writer.println("QUERY TIME ORDER");
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println(reader.readLine()); // 注意如果收不到服务端的换行符,方法一直阻塞,不会执行结束方法
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                reader.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

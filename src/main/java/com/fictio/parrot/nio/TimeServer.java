package com.fictio.parrot.nio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

class BIOHandleMessage implements Runnable {
    Socket socket;
    public BIOHandleMessage(Socket client) {
        this.socket = client;
    }
    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try(InputStream input = this.socket.getInputStream()){
            reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();
            System.out.println("line="+line);
            if(!"QUERY TIME ORDER".equalsIgnoreCase(line)) return;
            try(OutputStream output = this.socket.getOutputStream()){
                writer = new BufferedWriter(new OutputStreamWriter(output));
                writer.write(String.valueOf(System.currentTimeMillis()));
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                this.socket.close();
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }
}

class NIOServer {
    private Selector selector;
    private int port;
    private final static int BUF_SIZE = 1024;
    public NIOServer(int port) {
        this.port = port;
    }
    public void initServer() throws IOException {
        this.selector = Selector.open();

        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(port));

        @SuppressWarnings("unused")
        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_ACCEPT);

        while(true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> ite = keys.iterator();
            while(ite.hasNext()) {
                SelectionKey key = ite.next();
                ite.remove();
                if(key.isAcceptable()) {
                    doAccess(key);
                }else if(key.isReadable()) {
                    doRead(key);
                }else if(key.isWritable() && key.isValid()) {
                    doWrite(key);
                }else if(key.isConnectable()) {
                    System.out.println("连接成功 "+key.toString());
                }
            }
        }
    }
    private void doWrite(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUF_SIZE);
        byteBuffer.flip();
        SocketChannel clientChannel = (SocketChannel) key.channel();
        while(byteBuffer.hasRemaining()) clientChannel.write(byteBuffer);
        byteBuffer.compact();
    }
    private void doRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUF_SIZE);
        long bytesRead = clientChannel.read(byteBuffer);
        while(bytesRead > 0) {
            byteBuffer.flip();
            byte[] data = byteBuffer.array();
            String info = new String(data).trim();
            System.out.println("收到消息: "+info);
            byteBuffer.clear();
            bytesRead = clientChannel.read(byteBuffer);
        }
        if(bytesRead==-1) clientChannel.close();
    }
    private void doAccess(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(key.selector(), SelectionKey.OP_READ);
    }
}

class NIOHandleMessage implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverChannel;

    /**
     * <p> 初始化多路复用器,绑定监听端口;
     */
    public NIOHandleMessage(int port) {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * <p> 线程中遍历selector,(无论有米有读写事件,每秒唤醒一次)
     * <p> 当存在就绪状态的Channel时,selector返回Channel的SelectionKey集合
     */
    @Override
    public void run() {
        while(true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> ite = selectedKeys.iterator();
                while(ite.hasNext()) {
                    SelectionKey key = ite.next();
                    ite.remove();
                    handleInput(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p> 处理客户端接入的请求消息,判断网络事件类型(isAcceptable/isReadable/...)
     * <p> ssc.accept() 正式建立TCP物理链路
     * <p> ByteBuffer.allocate(1024);SocketChannel.read(readBuffer);通过ByteBuffer开辟1M的缓冲区,读取请求码流
     * <p> 根据SocketChannel.read(readBuffer)返回的字节数,判断链路是否关闭(-1),关闭SocketChannel,释放资源
     * @param key
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()) {
            if(key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if(key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = -1;
                readBytes = sc.read(readBuffer);
                if(readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("收到消息: "+body);
                    String currentTime = String.valueOf(System.currentTimeMillis()+"\n");
                    doWrite(sc, currentTime);
                }else if(readBytes < 0) {
                    key.cancel();
                    sc.close();
                }else;
            }
        }
    }

    /**
     * <p> 将应答消息异步返回客户端
     * <p> 将字符串转码至字节数组,调用ByteBuffer.put(...)方法将内容复制到缓冲区内
     * <p> 对缓冲区执行flip()操作,调用SocketChannel.write(...)将字节数组发送出去
     * @param channel
     * @param resp
     * @throws IOException
     */
    private void doWrite(SocketChannel channel, String resp) throws IOException {
        if(resp != null && resp.trim().length() > 0) {
            byte[] bytes = resp.getBytes();
            ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
            writerBuffer.put(bytes);
            writerBuffer.flip();
            channel.write(writerBuffer);
        }
    }
}

@Slf4j
public class TimeServer {
    private static ExecutorService es = Executors.newCachedThreadPool();
    public static void main(String[] args) throws IOException {
        // bioDemo();
       //new NIOServer(8666).initServer();
        es.submit(new NIOHandleMessage(8666));
    }

    public void bioDemo() throws IOException {
        @SuppressWarnings("resource")
        ServerSocket server = new ServerSocket(8666);
        Socket client = null;
        while(true) {
            client = server.accept();
            log.debug("客户端[{}]连接成功",client.getRemoteSocketAddress());
            es.submit(new BIOHandleMessage(client));
        }
    }
}

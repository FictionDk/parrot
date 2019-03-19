package com.fictio.parrot.thinking.thread;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Sender implements Runnable {
	private PipedWriter outer = new PipedWriter();
	private Random random = new Random(50);
	public PipedWriter getOuter() {
		return outer;
	}
	public void init(Reciver reciver) throws IOException {
		outer.connect(reciver.getPipedReader());
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				for(char c = 'A'; c < 'z'; c++) {
					outer.append(c);
					//outer.write(c);
					Thread.yield();
					TimeUnit.MILLISECONDS.sleep(100+random.nextInt(500));
				}
			}
		} catch (Exception e) {
			log.error("Sender interrupted, {}",e.toString());
		} finally {
			try {
				outer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

@Slf4j
class Reciver implements Runnable {
	private PipedReader in;
	public Reciver(Sender sender) throws IOException {
		in = new PipedReader(sender.getOuter());
	}
	public PipedReader getPipedReader() {
		return in;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				int c =  in.read();
				if(c != -1) {
					log.info("Read: {}",(char)c);
				}
			}
		} catch (Exception e) {
			log.error("Reciver read exception,{}",e);
		} finally {
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}

/**
 * <p> 多任务间的管道流通信
 * 
 *
 */
public class PipedIO {

	@Test
	public void test() throws IOException {
		ExecutorService exec = Executors.newCachedThreadPool();
		Sender sender = new Sender();
		/**
		 * <p> 使用new PipedReader(sender.getOuter())方法
		 * <p> 将输入输出流建立连接
		 */
		Reciver reciver = new Reciver(sender);
		
		//如果提前将reader和writer建立初始化连接
		//则exec.execute(new Reciver(sender))会抛出IOException,连接已存在
		//sender.init(reciver);
		
		exec.execute(sender);
		exec.execute(reciver);
		
		//如果直接在没有初始化的情况下直接连接,会抛出an I/O error occurs异常
		//exec.execute(new Reciver(sender));
		
		OrnamentalGarden.sleep(4000);
		exec.shutdownNow();
	}
	
}

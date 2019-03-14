package com.fictio.parrot.thinking.thread;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

class IOBlocked implements Runnable {
	private InputStream in;
	public IOBlocked(InputStream is) {
		this.in = is;
	}
	public void run() {
		System.out.println("Waiting for read().");
		try {
			in.read();
		} catch (IOException e) {
			if(Thread.currentThread().isInterrupted()) 
				System.out.println("Interrupted from block I/O");
			else
				throw new RuntimeException(e);
		}
		System.out.println("Exiting IOBlocked.run()");
	}
}


class NIOBlocked implements Runnable {
	private final SocketChannel sc;
	public NIOBlocked(SocketChannel sc) {
		this.sc = sc;
	}
	public void run() {
		System.out.println("waiting for read()");
		try {
			sc.read(ByteBuffer.allocate(1));
		} catch (ClosedByInterruptException e) {
			System.out.println("ClosedByInterruptException");
		} catch (AsynchronousCloseException e) {
			System.out.println("AdynchronousCloseException");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Exiting NIOBlocking.run"+this);
	}
	public String call() throws Exception {
		System.out.println("waiting for read()");
		try {
			sc.read(ByteBuffer.allocate(1));
		} catch (ClosedByInterruptException e) {
			System.out.println("ClosedByInterruptException");
		} catch (AsynchronousCloseException e) {
			System.out.println("AdynchronousCloseException");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Exiting NIOBlocking.run"+this);
		return null;
	}
}

public class ResourceClose {

	@Test
	public void NioTest() throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		ServerSocket server = new ServerSocket(8080);
		
		InetSocketAddress isa = new InetSocketAddress("localhost", 8080);
		
		SocketChannel sc1 = SocketChannel.open(isa);
		SocketChannel sc2 = SocketChannel.open(isa);
		String re = "";
		Future<String> f = exec.submit(new NIOBlocked(sc1),re);
		exec.execute(new NIOBlocked(sc2));
		
		exec.shutdown();
		OrnamentalGarden.sleep(1000);
		f.cancel(true);
		OrnamentalGarden.sleep(1000);
		sc2.close();
		
		if(!server.isClosed()) server.close();
	}
	
	@Test
	public void test() throws IOException {
		ExecutorService exec = Executors.newCachedThreadPool();
		ServerSocket server = new ServerSocket(8080);
		
		@SuppressWarnings("resource")
		InputStream socketInput = new Socket("localhost",8080).getInputStream();
		
		exec.execute(new IOBlocked(socketInput));
		exec.execute(new IOBlocked(System.in));
		
		OrnamentalGarden.sleep(100);
		System.out.println("Shuting down all threads");
		exec.shutdownNow();
		
		OrnamentalGarden.sleep(1000);
		System.out.println("Closeing "+socketInput.getClass().getName());
		socketInput.close();
		
		OrnamentalGarden.sleep(1000);
		System.out.println("Closeing "+System.in.getClass().getName());
		System.in.close();
		
		OrnamentalGarden.sleep(400);
		
		if(!server.isClosed()) server.close();
	}

}

package com.fictio.parrot.thinking.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class LiftOffRunner implements Runnable {
	private BlockingQueue<LiftOff> rockets;
	public LiftOffRunner(BlockingQueue<LiftOff> queue) {
		this.rockets = queue;
	}
	public void add(LiftOff lo) {
		try {
			rockets.put(lo);
		} catch (InterruptedException e) {
			log.error("Interrupted during put()");
		}
	}
	public void run() {
		try {
			while(!Thread.interrupted()) {
				LiftOff rocket = rockets.take();
				rocket.run();
			}
		} catch (Exception e) {
			log.error("Waking from take()");
		}
		log.info("Exiting LiftOffRunner");
	}
}


@Slf4j
public class TestBlockingQueue {

	static void getKey() {
		try {
			new BufferedReader(
				new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}
	
	static void getKey(String message) {
		log.info("message:{}",message);
		getKey();
	}
	
	static void test(String message,BlockingQueue<LiftOff> queue) {
		log.info("MSG:{}",message);
		LiftOffRunner runner = new LiftOffRunner(queue);
		Thread t = new Thread(runner);
		t.start();
		for(int i = 0; i < 5; i++)
			runner.add(new LiftOff(5));
		getKey("Press 'enter' ("+message+")");
		t.interrupt();
		log.info("Finished "+message+" test");
	}
	
	@Test
	public void test() {
		test("LinkedBlockingQueue", new LinkedBlockingQueue<>());
		test("ArrayBlockingQueue",new ArrayBlockingQueue<>(3));
		test("SynchronousQueue",new SynchronousQueue<>());
	}
	
}

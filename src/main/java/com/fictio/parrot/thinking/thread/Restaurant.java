package com.fictio.parrot.thinking.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

class Meal {
	private final int orderNum;
	public Meal(int order) {
		this.orderNum = order;
	}
	public int getOrderNum() {
		return this.orderNum;
	}
	public String toString() {
		return "Meal_"+orderNum;
	}
}

abstract class Worker implements Runnable {
	Restaurant restaurant;
	String name;
	public Worker(String name,Restaurant restaurant) {
		this.name = name;
		this.restaurant = restaurant;
	}
	public String toString() {
		return Thread.currentThread().getName()+
				"_"+name+"_"+restaurant.toString();
	}
}

final class Waiter extends Worker {
	public Waiter(String name,Restaurant restaurant) {
		super(name,restaurant);
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) getMeal();
		} catch (Exception e) {
			System.out.println("Waiter get meal failed,"+e.toString());
		}
	}
	private void getMeal() throws Exception {
		synchronized (this) {
			while(restaurant.meal == null) wait();
			System.out.println("Waiter "+this.name+" get "+restaurant.meal);
			synchronized (restaurant.boy) {
				restaurant.tableMeal = new Meal(restaurant.meal.getOrderNum());
				restaurant.meal = null;
				restaurant.boy.notifyAll();
			}
			TimeUnit.MILLISECONDS.sleep(1000);
		}
	}
}

final class BusBoy extends Worker{
	public BusBoy(String name,Restaurant restaurant) {
		super(name,restaurant);
	}
	private void cleanMeal() throws InterruptedException {
		synchronized (this) {
			while(restaurant.tableMeal == null) wait();
			System.out.println("BusBoy "+this.name+" clean "+restaurant.tableMeal);
			synchronized (restaurant.chef) {
				restaurant.tableMeal = null;
				restaurant.chef.notifyAll();
			}
			TimeUnit.MILLISECONDS.sleep(500);
		}
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) cleanMeal();
		}catch(Exception e) {
			System.out.println("BusBoy clean table meal failed, "+e.toString());
		}
	}
}

final class Chef extends Worker {
	private int count = 0;
	public Chef(String name,Restaurant restaurant) {
		super(name, restaurant);
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) buildMeal();
		}catch(Exception e) {
			System.out.println("Chefer builder meal failed .."+e.toString());
		}
	}
	
	private void buildMeal() throws InterruptedException {
		synchronized (this) {
			while(restaurant.meal != null) {
				wait();
			}
			if(++count == 20) {
				System.out.println("Out of food, closing...");
				//restaurant.exec.shutdown();
				restaurant.exec.shutdownNow();
				return;
			}
			System.out.println("Order Up!");
			synchronized (restaurant.waiter) {
				restaurant.meal = new Meal(count);
				restaurant.waiter.notifyAll();
			}
			TimeUnit.MILLISECONDS.sleep(1000);
		}		
	}
}


/**
 * <p> 餐馆并发demo
 */
public class Restaurant {
	Meal tableMeal;
	Meal meal;
	String restaurantName;
	Chef chef = new Chef("Less", this);
	Waiter waiter = new Waiter("Joe", this);
	BusBoy boy = new BusBoy("Kim", this);
	ExecutorService exec = Executors.newCachedThreadPool();
	
	@Test
	public void test() {
		exec.execute(chef);
		exec.execute(waiter);
		exec.execute(boy);
		sleep(40000);
	}
	
	public void sleep(int mills) {
		try {
			TimeUnit.MILLISECONDS.sleep(mills);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

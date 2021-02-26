package com.fictio.parrot.logic.thread;

import org.junit.Test;

class AssemblePoint {
    private int n;
    public AssemblePoint(int n) {
        this.n = n;
    }
    public synchronized void await() throws InterruptedException {
        if(n > 0) {
            n--;
            if(n==0) notifyAll();
            else wait();
        }
    }
}

public class AssemblePointDemo {

    private class Tourist implements Runnable {
        AssemblePoint ap;
        public Tourist(AssemblePoint ap) {
            this.ap = ap;
        }
        @Override public void run() {
            try {
                Thread.sleep((int) (Math.random()*1000));
                ap.await();
                System.out.println(Thread.currentThread().getName() + " arrived!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test public void tests() {
        int num = 10;
        AssemblePoint ap = new AssemblePoint(num);
        Tourist t = new Tourist(ap);
        for(int i = 0; i < num; i++) new Thread(t, "T_"+i).start();
    }

}

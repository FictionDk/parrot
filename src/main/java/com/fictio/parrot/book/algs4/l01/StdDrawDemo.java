package com.fictio.parrot.book.algs4.l01;

import com.fictio.parrot.book.algs4.l00.StdDraw;
import com.fictio.parrot.book.algs4.l00.StdRandom;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class StdDrawDemo {
    public static void main(String[] args) {
        DrawA a = new DrawA();
        DrawB b = new DrawB();
        a.run();
        b.run();
    }

    static class DrawA implements Runnable {
        @Override
        public void run() {
            int n = 100;
            StdDraw.setXscale(0, n);
            StdDraw.setYscale(0, n*n);
            StdDraw.setPenRadius(.01);
            for(int i = 1; i <= n; i++){
                StdDraw.point(i, i);
                StdDraw.point(i, i*i);
                StdDraw.point(i, i*Math.log(i));
            }
        }
    }

    static class DrawB implements Runnable {
        @Override
        public void run() {
            int n = 50;
            double[] a = new double[n];
            for (int i = 0; i < n; i++) a[i] = StdRandom.uniform();
            Arrays.sort(a);
            for (int i = 0; i < n; i++){
                double x = 1.0 * i / n;
                double y = a[i] / 2.0;
                double rw = 0.5 / n;
                double rh = a[i] / 2.0;
                StdDraw.filledRectangle(x, y, rw, rh);
            }
        }
    }
}

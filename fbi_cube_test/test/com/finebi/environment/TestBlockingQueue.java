package com.finebi.environment;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Lucifer on 2017-3-17.
 *
 * @author Lucifer
 * @since 4.0
 */
public class TestBlockingQueue {
    public static LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(100);

    public static void main(String[] args) {
        new PutThread().start();
        new TakeThread().start();
        new PollThread().start();
        while(true) {
            try {
                Thread.sleep(2000l);
                System.exit(0);
            } catch (Exception e) {

            }
        }
    }
}

class PutThread extends Thread {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                TestBlockingQueue.queue.put(i);
                System.out.println("PutThread:" + i);
            }
            return;
        } catch (InterruptedException e) {

        }
    }
}

class PollThread extends Thread {
    @Override
    public void run() {
        int count = 0;
        while (true) {
            Integer data = TestBlockingQueue.queue.poll();
            if (data == null) {
                count++;
                System.out.println("PollThread:null");
            } else {
                System.out.println("PollThread:" + data);
            }
            if (count == 20) {
                break;
            }
        }
    }
}

class TakeThread extends Thread {
    @Override
    public void run() {
        try {
            while(true) {
                Integer data = TestBlockingQueue.queue.take();
                System.out.println("TakeThread:" + data);
            }
        } catch (InterruptedException e) {

        }
    }
}

package com.finebi.environment;

import com.finebi.cube.common.log.BILoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Lucifer on 2017-3-17.
 *
 * @author Lucifer
 * @since 4.0
 */
public class TestBlockingQueue {
    public static LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(100);
    public static int times = 100;
    public static int count = 20;

    public static void main(String[] args) {
        new PutThread().start();
        new TakeThread().start();
        new PollThread().start();
        while (true) {
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
            for (int i = 0; i < TestBlockingQueue.times; i++) {
                TestBlockingQueue.queue.put(i);
                BILoggerFactory.getLogger().info("PutThread:" + i);
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
                BILoggerFactory.getLogger().info("PollThread:null");
            } else {
                BILoggerFactory.getLogger().info("PollThread:" + data);
            }
            if (count == TestBlockingQueue.count) {
                break;
            }
        }
    }
}

class TakeThread extends Thread {
    @Override
    public void run() {
        try {
            while (true) {
                Integer data = TestBlockingQueue.queue.take();
                BILoggerFactory.getLogger().info("TakeThread:" + data);
            }
        } catch (InterruptedException e) {

        }
    }
}

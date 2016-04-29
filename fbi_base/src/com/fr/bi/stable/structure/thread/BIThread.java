package com.fr.bi.stable.structure.thread;

/**
 * Created by GUY on 2015/3/16.
 */
public interface BIThread {

    void start();

    void notifyThread();

    void interrupt();
}
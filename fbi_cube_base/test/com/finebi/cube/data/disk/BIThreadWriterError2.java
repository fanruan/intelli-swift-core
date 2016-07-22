package com.finebi.cube.data.disk;

/**
 * Created by kary on 2016/7/21.
 *  * 调用不同句柄对同一个文件读写时，依然有一定概率导致jvm崩溃
 */
public class BIThreadWriterError2 extends Thread{
    public BIThreadWriterError2() {
    }

    public void run() {
        BIDiskWriterReaderTest diskWriterReaderTest=new BIDiskWriterReaderTest();
        diskWriterReaderTest.testSimpleWriteReader();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new BIThreadWriterError2().start();
        }

    }
}

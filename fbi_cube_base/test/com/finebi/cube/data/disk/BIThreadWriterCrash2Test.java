package com.finebi.cube.data.disk;

/**
 * Created by kary on 2016/7/21.
 *  * 调用不同句柄对同一个文件读写时，有一定概率导致jvm崩溃
 */
public class BIThreadWriterCrash2Test extends Thread{
    public BIThreadWriterCrash2Test() {
    }

    public void run() {
        BIDiskWriterReaderTest diskWriterReaderTest=new BIDiskWriterReaderTest();
        diskWriterReaderTest.testSimpleWriteReader();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new BIThreadWriterCrash2Test().start();
        }

    }
}

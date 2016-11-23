package com.finebi.cube.data.disk;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/**
 * Created by wang on 2016/11/17.
 */
public class TestMap {
    public static void doClean(final MappedByteBuffer buffer) {
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    Method clean = buffer.getClass().getMethod("cleaner", new Class[0]);
                    clean.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) clean.invoke(buffer, new Object[0]);
                    if (cleaner != null) {
                        cleaner.clean();
                        cleaner.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
    public static void main(String[] args)  {
        ArrayList<MappedByteBuffer> lbf = new ArrayList<MappedByteBuffer>();
        ArrayList<DoubleBuffer> llbf = new ArrayList<DoubleBuffer>();
        String basePath ="/app/finebi/webapps/WebReport/WEB-INF/resources/cubes/-999/Advanced/b768262e/";

        ArrayList<String> pathl = new ArrayList<String>();
        pathl.add("075f9a6c");
        pathl.add("1eb9857f");
        pathl.add("3a5e3ac3");
        pathl.add("0f85e078");
        pathl.add("007909da");
        pathl.add("d79065ea");
        pathl.add("02acaf56");
        pathl.add("74f468d5");
        pathl.add("1d496a5e");
        pathl.add("66ed01dc");
        pathl.add("73c5b324");
        pathl.add("0dc5711c");
        pathl.add("28fdb199");
        pathl.add("fd404fac");
        for (int i = 0; i < Integer.parseInt(args[2]); i++) {
            try {
                for (int j = 0; j < pathl.size(); j++) {

                    FileChannel channel = new RandomAccessFile(new File(basePath +pathl.get(j) +"/detail.fbi"), "r").getChannel();
                    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                    DoubleBuffer longbf = buffer.asDoubleBuffer();
                    for (int k = 0; k < channel.size() / 8; k++) {
                        double d = longbf.get(k);
                    }
                    lbf.add(buffer);
                    llbf.add(longbf);

                    FileChannel channelW = new RandomAccessFile(new File(basePath +pathl.get(j) +"/detail.fbi"), "rw").getChannel();
                    MappedByteBuffer bufferW = channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());
                    DoubleBuffer longbfW = buffer.asDoubleBuffer();

                    lbf.add(bufferW);
                    llbf.add(longbfW);
                }
                System.out.println(i + "=========" + args[1]);
            }catch (Exception e){
                e.printStackTrace();
                for (int j = 0;j<lbf.size();j++) {
                    doClean(lbf.get(j));
                }
                for (int j = 0; j < llbf.size(); j++) {
                    llbf.get(j).clear();
                }

            }
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

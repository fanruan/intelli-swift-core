package com.fr.bi.cluster.utils;

import com.fr.bi.cluster.ClusterConstant;
import com.fr.bi.stable.utils.code.BILogger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketTimeoutException;

public class BIClusterUtils {

    /**
     * 最大重试次数
     */
    private static final int MAX_TEST_COUNT = 1 << 4;

    private static <T> T getValueFromHttpMaster(HttpExecuter<T> ex) {
        return getValueFromHttpMaster(0, ex);
    }

    private static <T> T getValueFromHttpMaster(int count, HttpExecuter<T> ex) {
        try {
            if (count > MAX_TEST_COUNT) {
//                System.out.println("严重错误:主机无法连接");
                //TODO 需要增加对offline机器进行处理的线程
                return null;
            }
            if (count != 0) {
//                System.out.println("正在master服务器进行第" + count + "次重试");
            }
            return ex.exe();
        } catch (SocketTimeoutException e) {
//            System.out.println("master 服务器连接超时, 5秒后重试...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                BILogger.getLogger().error(e1.getMessage());
            }
            return getValueFromHttpMaster(++count, ex);
        } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public static void writeNull(ObjectOutputStream res) {
        writeObject(ClusterConstant.TRANS.NULLOBJECT, res);
    }

    /**
     * 这里不关闭流
     *
     * @param object
     * @param oos
     */
    public static void writeObject(Serializable object, ObjectOutputStream oos) {
        if (object == null) {
            object = ClusterConstant.TRANS.NULLOBJECT;
        }
        if (oos == null) {
            return;
        }
        try {
            oos.writeObject(object);
            oos.flush();
        } catch (IOException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * TODO 使用这个接口有一个问题需要解决，那就是 命令重复发送的问题，若发生命令重复发送，需要有状态可以控制防止命令重复执行
     * 比如一些重要的更新，生成操作等
     *
     * @param <T>
     * @author Daniel
     */
    public static interface HttpExecuter<T> {

        public T exe() throws SocketTimeoutException, Exception;

    }


}
package com.fr.swift.util;

import com.fr.swift.util.function.Consumer;

import java.io.File;

/**
 * @author anchore
 * @date 2018/1/4
 */
public class FileUtil {
    /**
     * CommonUtils.deleteFile删文件有问题。。。
     *
     * @param f 文件或目录
     */
    public static void delete(final File f) {
        walk(f, new Consumer<File>() {
            @Override
            public void accept(File file) {
                file.delete();
            }
        });
    }

    public static void delete(String path) {
        delete(new File(path));
    }

    public static void walk(File f, Consumer<File> consumer) {
        if (f == null || !f.exists()) {
            return;
        }

        if (f.isDirectory()) {
            File[] children = f.listFiles();

            if (children != null) {
                for (File child : children) {
                    walk(child, consumer);
                }
            }
        }

        consumer.accept(f);
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }
}

package com.finebi.base.utils.data.io;

import com.finebi.log.BILoggerFactory;
import com.fr.stable.StableUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by andrew_asa on 2017/12/21.
 */
public class FileUtils {

    /**
     * 新建文件，旧文件删除
     *
     * @param f
     */
    public static void createFile(File f) {

        delete(f);
        try {
            createDirs(f.getParentFile());
            f.createNewFile();
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    public static boolean delete(File f) {

        return StableUtils.deleteFile(f);
    }

    public static void createDirs(File f) {

        StableUtils.mkdirs(f);
    }

    /**
     * 一行一行读文件
     *
     * @return list集合
     */
    public static String readFile(String path) throws IOException {

        File file = new File(path);
        StringBuffer sb = new StringBuffer();
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            reader.close();
            fr.close();
        }
    }

    public static void writeFile(String path, String content) throws IOException {

        File tmp = new File(path);
        createFile(tmp);
        FileWriter fileWriter = new FileWriter(tmp, false);
        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
        bufferWriter.write(content);
        bufferWriter.close();
        fileWriter.close();
    }
}

package com.fr.bi.stable.utils;

import com.fr.stable.StableUtils;

import java.io.*;

/**
 * Created by kary on 2018/1/3.
 */
public class BIFileUtils {
    public static void writeFile(String path, String content) throws IOException {
        File tmp = new File(path);
        createFile(tmp);
        FileWriter fileWriter = new FileWriter(tmp, false);
        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
        bufferWriter.write(content);
        bufferWriter.close();
        fileWriter.close();
    }

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

    public static void createFile(File f) throws IOException {
        delete(f);
        createDirs(f.getParentFile());
        f.createNewFile();
    }

    public static void createDirs(File f) {
        StableUtils.mkdirs(f);
    }

    public static boolean delete(File f) {
        return StableUtils.deleteFile(f);
    }

}

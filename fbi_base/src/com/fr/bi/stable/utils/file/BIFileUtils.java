
package com.fr.bi.stable.utils.file;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.io.io.GroupReader;
import com.fr.bi.stable.utils.mem.BIMemoryUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StableUtils;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connery on 2015/12/4.
 */
public class BIFileUtils {

    public static <T> T createFile(Object ob, String field, Class<T> c, String path) {
        try {
            Field f = null;
            Class<?> clz = ob.getClass();
            while (f == null && clz != null) {
                try {
                    f = clz.getDeclaredField(field);
                } catch (Exception e) {
                    clz = clz.getSuperclass();
                }
            }
            f.setAccessible(true);
            T t = (T) f.get(ob);
            if (t != null) {
                return t;
            }
            synchronized (ob) {
                t = (T) f.get(ob);
                if (t == null) {
                    Constructor<T> constructor = c.getConstructor(path.getClass());
                    constructor.setAccessible(true);
                    t = constructor.newInstance(path);
                    f.set(ob, t);
                }
                return t;
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    private static long MAX_PERSIZE = 1L << 24;

    public static boolean delete(File f) {
        return StableUtils.deleteFile(f);
    }

    /**
     * 查找出路径下的所有文件路径
     *
     * @param folder 文件夹
     * @return
     */
    public static List findAllFiles(File folder) {
        List realFiles = new ArrayList();
        File[] files = folder.listFiles();
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    realFiles.addAll(findAllFiles(files[i]));
                } else {
                    realFiles.add(files[i].getAbsolutePath());
                }
            }
        }
        return realFiles;
    }

    public static List deleteFiles(File f) {
        List removedFailedFiles = new ArrayList();
        File[] files = f.listFiles();
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteFiles(files[i]);
                } else {
                    if (!BIFileUtils.delete(files[i])) {
                        removedFailedFiles.add(files[i].getAbsolutePath());
                    }
                }
            }
            if (!f.delete()) {
                removedFailedFiles.add(f.getAbsolutePath());
            }
        }
        return removedFailedFiles;
    }

    public static void createDirs(File f) {
        StableUtils.mkdirs(f);
    }

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

    /**
     * 检查文件夹是否存在或可创建
     *
     * @param f
     * @return
     */
    public static boolean checkDir(File f) {
        createDirs(f);
        return f.exists();
    }

    /**
     * 检查文件夹是否可创建或为空
     *
     * @param f
     * @return
     */
    public static boolean ensureDir(File f) {
        if (f.exists()) {
            return false;
        }
        return checkDir(f);
    }

    /**
     * 一行一行读文件
     *
     * @param file 文件
     * @return list集合
     */
    public static List<String> readFileByLines(File file) {
        GroupReader reader = GroupReader.readFromFile(file);
        if (reader != null) {
            List<String> res = reader.getValueList();
            return res == null ? new ArrayList<String>() : res;
        }
        return new ArrayList<String>();
    }

    /**
     * 移动到
     *
     * @param source 源
     * @param dest   目的
     * @throws java.io.IOException
     */
    public static void copyFile(File source, File dest) throws IOException {
        FileChannel in = null, out = null;
        try {
            if (!source.exists()) {
                BILoggerFactory.getLogger(BIFileUtils.class).warn("the source file is not exist: " + source.getAbsolutePath());
                return;
            }
            if (!dest.exists()) {
                dest.createNewFile();
            }
            in = new RandomAccessFile(source, "r").getChannel();
            out = new RandomAccessFile(dest, "rw").getChannel();
            long currentSize = 0;
            long max_persize = MAX_PERSIZE;
            long total_size = in.size();
            while (true) {
                if (currentSize == total_size) {
                    break;
                }
                long left_size = total_size - currentSize;
                long size = Math.min(left_size, max_persize);
                MappedByteBuffer reader = null, writer = null;
                try {
                    reader = in.map(FileChannel.MapMode.READ_ONLY, currentSize, size);
                    writer = out.map(FileChannel.MapMode.READ_WRITE, currentSize, size);
                    for (int p = 0; p < size; p++) {
                        writer.put(p, reader.get(p));
                    }
                    currentSize += size;
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    BIMemoryUtils.un_map(writer);
                    BIMemoryUtils.un_map(reader);
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 移动文件
     *
     * @param from from来自
     * @param to   到
     * @throws Exception
     */
    public static void moveFile(String from, String to) throws Exception {
        try {
            File dir = new File(from);
            // 文件一览
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            // 目标
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                moveDir.mkdirs();
            }
            // 文件移动
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    moveFile(files[i].getPath(), to + File.separator + files[i].getName());
                } else {
                    File moveFile = new File(moveDir.getPath() + File.separator
                            + files[i].getName());
                    // 目标文件夹下存在的话，删除
                    if (moveFile.exists()) {
                        moveFile.delete();
                    }
                    boolean success = files[i].renameTo(moveFile);
                    //失败就copy一份过去
                    if (!success) {
                        FRContext.getLogger().info("Warning!FILE:" + files[i].getAbsolutePath() + " OR FILE:" + moveFile.getAbsolutePath() + "is open by other thread");
                        BIFileUtils.copyFile(files[i], moveFile);
                    }
                }
            }
            if (dir.exists()) {
                dir.delete();
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * @param path    文件路径
     * @param suffix  后缀名, 为空则表示所有文件
     * @param isdepth 是否遍历子目录
     * @return list
     */
    public static List<String> getListFiles(String path, String suffix, boolean isdepth) {
        List<String> lstFileNames = new ArrayList<String>();
        File file = new File(path);
        return listFile(lstFileNames, file, suffix, isdepth);
    }

    private static List<String> listFile(List<String> lstFileNames, File f, String suffix, boolean isdepth) {
        // 若是目录, 采用递归的方法遍历子目录
        if (f.isDirectory()) {
            File[] t = f.listFiles();

            for (int i = 0; t != null && i < t.length; i++) {
                if (isdepth || t[i].isFile()) {
                    listFile(lstFileNames, t[i], suffix, isdepth);
                }
            }
        } else {
            String filePath = f.getAbsolutePath();
            if (!ComparatorUtils.equals(suffix, "")) {
                int begIndex = filePath.lastIndexOf("."); // 最后一个.(即后缀名前面的.)的索引
                String tempsuffix = "";

                if (begIndex != -1) {
                    tempsuffix = filePath.substring(begIndex + 1, filePath.length());
                    if (ComparatorUtils.equals(tempsuffix, suffix)) {
                        lstFileNames.add(filePath);
                    }
                }
            } else {
                lstFileNames.add(filePath);
            }
        }
        return lstFileNames;
    }


    public static void copyFile(String fileName, File old_f, File new_f) {
        File tmp = new File(old_f, fileName);
        int k = 0;
        while (tmp.exists()) {
            try {
                copyFile(tmp, new File(new_f, tmp.getName()));
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            k++;
            tmp = new File(old_f, fileName + "_" + k);
        }
    }

    public static void writeFile(String path, String content) {
        try {
            File tmp = new File(path);
            createFile(tmp);
            FileWriter fileWriter = new FileWriter(tmp, false);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write(content);
            bufferWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    /**
     * 一行一行读文件
     *
     * @return list集合
     */
    public static String readFile(String path) {
        File file = new File(path);
        StringBuffer sb = new StringBuffer();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            try {
                while ((line = reader.readLine()) != null)
                    sb.append(line);
                return sb.toString();
            } finally {
                reader.close();
                fr.close();
            }

        } catch (IOException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    /**
     * 完整拷贝整个文件件
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {

            copyFile(src, dest);
        }
    }

    public static boolean renameFolder(File src, File dest) throws IOException {
        if (!src.isDirectory() || !src.exists()) {
            return false;
        }
        return src.renameTo(dest);
    }
}

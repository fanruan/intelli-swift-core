package com.fr.swift.executor.task.utils;

import com.fr.swift.log.SwiftLoggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Hoky
 * @date 2020/11/3
 */
public class MigrationZipUtils {
    private static final int BUFFER_SIZE = 2 * 1024;
    /**
     * 是否保留原来的目录结构
     * true:  保留目录结构;
     * false: 所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    private static final boolean KEEP_DIR_STRUCTURE = true;


    /**
     * 压缩成ZIP
     *
     * @param srcDir       压缩 文件/文件夹 路径
     * @param outPathFile  压缩 文件/文件夹 输出路径+文件名 D:/xx.zip
     * @param isDelSrcFile 只需要压缩文件夹
     */
    public static void toZip(String srcDir, String outPathFile, boolean isDelSrcFile) throws Exception {
        long start = System.currentTimeMillis();
        FileOutputStream out = null;
        ZipOutputStream zos = null;
        try {
            out = new FileOutputStream(outPathFile);
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            if (!sourceFile.exists()) {
                throw new Exception("zip file not exist!");
            }
            compress(sourceFile, zos, sourceFile.getName());
//            if (isDelSrcFile) {
//                delDir(srcDir);
//            }
            SwiftLoggers.getLogger().info("zip " + sourceFile.getName() + " cost:  " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("zip error from ZipUtils");
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name)
            throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (!sourceFile.isDirectory()) {
            zos.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (KEEP_DIR_STRUCTURE) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (KEEP_DIR_STRUCTURE) {
                        compress(file, zos, name + "/" + file.getName());
                    } else {
                        compress(file, zos, file.getName());
                    }
                }
            }
        }
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipPath 解压源文件路径
     * @param descDir 解压目标文件路径
     */
    public static void unCompress(String zipPath, String descDir) throws IOException {
        long start = System.currentTimeMillis();
        try {
            File zipFile = new File(zipPath);
            if (!zipFile.exists()) {
                throw new IOException("zip file not exist.");
            }
            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");
                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                // 输出文件路径信息
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            SwiftLoggers.getLogger().info("file:{}. zip path:{}. finished. cost time:{}ms. ", zipPath, descDir, System.currentTimeMillis() - start);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            throw new IOException(e);
        }
    }

    public static void delDir(String dirPath) throws IOException {
        long start = System.currentTimeMillis();
        try {
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                return;
            }
            if (dirFile.isFile()) {
                dirFile.delete();
                return;
            }
            File[] files = dirFile.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                delDir(files[i].toString());
            }
            dirFile.delete();
            SwiftLoggers.getLogger().info("delete file:{}. cost:{}ms. ", dirPath, System.currentTimeMillis() - start);
        } catch (Exception e) {
            SwiftLoggers.getLogger().info("delete file:{}. exception:{}. cost:{}ms. ", dirPath, e, System.currentTimeMillis() - start);
            throw new IOException("delete file exception.");
        }
    }
}

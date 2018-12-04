package com.fr.swift.repository.utils;

import com.fr.swift.log.SwiftLoggers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author yee
 * @date 2018/7/2
 */
public class ZipUtils {
    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out)
            throws RuntimeException, IOException {

        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName());
            long end = System.currentTimeMillis();
            SwiftLoggers.getLogger().info("Zip {} finished. Cost {} ms", srcDir, (end - start));
        } catch (Exception e) {
            throw new RuntimeException("zip error", e);
        } finally {
            SwiftRepositoryUtils.close(zos);
        }
    }

    public static void unZip(String parent, InputStream inputStream) throws Exception {
        long start = System.currentTimeMillis();
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
            File fout = new File(parent, entry.getName());
            if (!fout.exists()) {
                fout.getParentFile().mkdirs();
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fout));
            SwiftRepositoryUtils.copyBinaryTo(zis, bos);
            SwiftRepositoryUtils.close(bos);
        }

        SwiftRepositoryUtils.close(zis);
        long end = System.currentTimeMillis();
        SwiftLoggers.getLogger().info("Unzip {} finished. Cost {} ms", parent, (end - start));
    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name) throws Exception {
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            FileInputStream in = new FileInputStream(sourceFile);
            SwiftRepositoryUtils.copyBinaryTo(in, zos);
            // Complete the entry
            zos.closeEntry();
            SwiftRepositoryUtils.close(in);
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 空文件夹的处理
                zos.putNextEntry(new ZipEntry(name + "/"));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    compress(file, zos, name + "/" + file.getName());
                }
            }
        }
    }
}

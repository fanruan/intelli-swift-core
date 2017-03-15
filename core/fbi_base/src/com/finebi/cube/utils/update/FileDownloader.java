package com.finebi.cube.utils.update;

import com.fr.stable.ArrayUtils;
import com.fr.stable.StableUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author richie
 * @date 2015-04-01
 * @since 8.0
 * 文件下载器
 */
public class FileDownloader {
    private String saveDir;
    public static final int BYTE = 153600;

    public FileDownloader(String saveDir) {
        this.saveDir = saveDir;
    }


    public void download(DownloadItem[] files) throws Exception {
        if (ArrayUtils.isNotEmpty(files)) {
            for (DownloadItem item : files) {
                download(item);
            }
        }
    }


    public void download(DownloadItem item) throws Exception {
        URL url = new URL(item.getUrl());
        URLConnection connection = url.openConnection();
        int total = connection.getContentLength();
        item.setTotalLength(total);
        InputStream reader = connection.getInputStream();
        File tempFile = new File(StableUtils.pathJoin(saveDir, item.getName()));
        StableUtils.makesureFileExist(tempFile);
        FileOutputStream writer = new FileOutputStream(tempFile);
        byte[] buffer = new byte[BYTE];
        int bytesRead;
        int totalBytesRead = 0;
        while ((bytesRead = reader.read(buffer)) > 0) {
            writer.write(buffer, 0, bytesRead);
            buffer = new byte[BYTE];
            totalBytesRead += bytesRead;
            item.setDownloadLength(totalBytesRead);
        }
        writer.flush();
        writer.close();
    }

}
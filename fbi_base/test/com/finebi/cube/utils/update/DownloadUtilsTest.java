package com.finebi.cube.utils.update;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import com.fr.bi.stable.utils.file.BIFileUtils;
import junit.framework.TestCase;

import java.io.File;

/**
 * @author richie
 * @date 2015-04-01
 * @since 8.0
 */
public class DownloadUtilsTest extends TestCase {
    /**
     * demo主程序
     *
     * @param args 参数
     */
    public void testDownload() {
        String folderName = "DownloadUtilsTest";
        String name_download = "DownloadFile";
        String path = System.getProperty("user.dir") + File.separator + folderName;
        String path_download = path + File.separator + name_download;
        String target_Path = path + File.separator + "test";

        int length = BIRandomUitils.getRandomInteger() % 10000;
        String content = "abc" + BIRandomUitils.getRandomCharacterString(length);
        BIFileUtils.delete(new File(target_Path));
        BIFileUtils.writeFile(target_Path, content);
        FileDownloader downloader = new FileDownloader(path);
        BIFileUtils.delete(new File(path_download));
        DownloadItem[] items = new DownloadItem[]{
                new DownloadItem(name_download, "file:///" + target_Path.replace("\\", "/"))};
        try {
            downloader.download(items);
            String readContent = BIFileUtils.readFile(path_download);
            assertEquals(content, readContent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
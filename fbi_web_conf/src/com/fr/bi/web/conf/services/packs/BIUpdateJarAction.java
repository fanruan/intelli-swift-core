package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.utils.update.DownloadItem;
import com.finebi.cube.utils.update.FileDownloader;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class BIUpdateJarAction extends
        AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "update_jar";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String base = BIConfigurePathUtils.getProjectLibPath();
        target = BIFileUtils.readFile(base + File.separator + "online_update");
        downloadJar(base);
    }

    public String target = "ftp://env.finedevelop.com/BI4.0/md5sums";

    private void downloadJar(String base) {
        try {
            FileDownloader downloader = new FileDownloader(base);
            DownloadItem[] items = new DownloadItem[]{
                    new DownloadItem("BI.jar", target)};
            downloader.download(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

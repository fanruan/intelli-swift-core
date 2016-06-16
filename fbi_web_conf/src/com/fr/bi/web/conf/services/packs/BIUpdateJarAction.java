package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.utils.update.DownloadItem;
import com.finebi.cube.utils.update.FileDownloader;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

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
        String target = BIFileUtils.readFile(base + File.separator + "online_update");
        JSONObject jsonObject = new JSONObject(target);
        JSONArray array = jsonObject.getJSONArray("jars");
        String url = jsonObject.getString("url");
        for (int i = 0; i < array.length(); i++) {
            String name = array.getString(i);
            downloadJar(name, url + name, base);

        }
    }

    private void downloadJar(String name, String url, String base) {
        try {
            FileDownloader downloader = new FileDownloader(base);
            DownloadItem[] items = new DownloadItem[]{
                    new DownloadItem(name, url)};
            downloader.download(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

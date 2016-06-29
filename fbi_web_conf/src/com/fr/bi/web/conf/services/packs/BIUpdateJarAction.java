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
       updateJarOnline();
    }
    protected  updateJarOnline(){
        String base = BIConfigurePathUtils.getProjectLibPath();
        String confPath = base + File.separator + "online_update";
        String target;
        if (new File(confPath).exists()) {
            target = BIFileUtils.readFile(confPath);
        } else {
            target = getDefault();
        }
        JSONObject jsonObject = new JSONObject(target);
        JSONArray array = jsonObject.getJSONArray("jars");
        String url = jsonObject.getString("url");
        for (int i = 0; i < array.length(); i++) {
            String name = array.getString(i);
            downloadJar(name, url + name, base);

        }
    }

    private String getDefault() {
        return "{\n" +
                "    \"url\": \"http://o8snq3y3i.bkt.clouddn.com/\",\n" +
                "    \"jars\": [\n" +
                "        \"fr-bi-server-4.0.jar\",\n" +
                "        \"fr-chart-8.0.jar\",\n" +
                "        \"fr-core-8.0.jar\",\n" +
                "        \"fr-performance-8.0.jar\",\n" +
                "        \"fr-platform-8.0.jar\",\n" +
                "        \"fr-report-8.0.jar\",\n" +
                "        \"fr-third-8.0.jar\"\n" +
                "    ]\n" +
                "}";
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

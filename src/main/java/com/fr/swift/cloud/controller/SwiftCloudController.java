package com.fr.swift.cloud.controller;

import com.fr.swift.cloud.SwiftCloudUtils;
import com.fr.swift.repository.utils.ZipUtils;
import com.fr.swift.util.Strings;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2019-02-25
 */
public class SwiftCloudController {
    /**
     * TODO: 2019/02/25 用户名密码先写为空，先下载目录放在当前目录县
     */
    private static final String USERNAME = Strings.EMPTY;
    private static final String PASSWORD = Strings.EMPTY;
    private static final String DOWNLOAD_ROOT_PATH = System.getProperty("user.dir");

    public Map<String, Object> triggerAnalyse(UploadInfo info) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, String> res = SwiftCloudUtils.getUserInfo(USERNAME, PASSWORD);
            if (res != null) {
                String appKey = res.get("app_key");
                String appSecret = res.get("app_secret");

                // 通过客户的用户ID、客户的应用ID和客户的数据包日期获取数据包的下载链接
                String downloadLink = SwiftCloudUtils.getDownloadLink(appKey, appSecret, info.getClientUserId(), info.getClientAppId(), info.getTreasDate());
                if (Strings.isNotEmpty(downloadLink)) {
                    InputStream inputStream = new URL(downloadLink).openStream();
                    String downloadPath = DOWNLOAD_ROOT_PATH + "/" + info.getClientUserId() + "/" + info.getTreasDate();
                    ZipUtils.unZip(downloadPath, inputStream);
                    // TODO 2019/02/25 接导入
                }
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
}

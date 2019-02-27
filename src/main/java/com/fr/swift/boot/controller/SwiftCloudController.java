package com.fr.swift.boot.controller;

import com.fr.swift.cloud.SwiftCloudConstants;
import com.fr.swift.cloud.SwiftCloudUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.utils.ZipUtils;
import com.fr.swift.util.Strings;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author yee
 * @date 2019-02-25
 */
@RestController
@RequestMapping("/swift")
public class SwiftCloudController {
    /**
     * TODO: 2019/02/25 用户名密码先写为空，先下载目录放在当前目录县
     */
    private static final String USERNAME = Strings.EMPTY;
    private static final String PASSWORD = Strings.EMPTY;
    private static final String DOWNLOAD_ROOT_PATH = System.getProperty("user.dir");

    private Map<String, String> authMap = new ConcurrentHashMap<String, String>();
    private ExecutorService service = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory(SwiftCloudController.class));

    @ResponseBody
    @RequestMapping(value = "/triggerAnalyse", method = RequestMethod.POST)
    public Map<String, Object> triggerAnalyse(@RequestParam("client_user_id") String clientUserId,
                                              @RequestParam("client_app_id") String clientAppId,
                                              @RequestParam("treas_date") String treasDate) {
        logStartTrigger(clientUserId, clientAppId, treasDate);
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (authMap.isEmpty()) {
                SwiftLoggers.getLogger().info("====== Start get Auth code ======");
                Map<String, String> res = SwiftCloudUtils.getUserInfo(USERNAME, PASSWORD);
                if (!res.isEmpty()) {
                    authMap.putAll(res);
                    SwiftLoggers.getLogger().info("====== Get Auth code success ======");
                } else {
                    result.put("error", "Get app_key and app_secret error");
                    SwiftLoggers.getLogger().info("====== Get app_key and app_secret error skip task ======");
                    return result;
                }
            }
            String appKey = authMap.get("app_key");
            String appSecret = authMap.get("app_secret");
            if (Strings.isEmpty(appKey) || Strings.isEmpty(appSecret)) {
                throw new RuntimeException("Auth error! Can not find app_key or app_secret");
            }
            service.submit(new AnalyseTask(appKey, appSecret, clientUserId, clientAppId, treasDate));
            result.put("status", SwiftCloudConstants.SUCCESS);
            result.put("msg", "analyse task has been submitted to the queue");
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
            result.put("error", e.getMessage());
        }
        logEndTrigger(clientUserId, clientAppId, treasDate);
        return result;
    }

    private void logEndTrigger(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("       End Trigger Analyse");
        logClientInfo(clientUserId, clientAppId, treasDate);
    }


    private void logStartTrigger(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("       Start Trigger Analyse");
        logClientInfo(clientUserId, clientAppId, treasDate);
    }

    private void logStartGetDownload(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("     Start get download address");
        logClientInfo(clientUserId, clientAppId, treasDate);
    }

    private void logStartAnalyse(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("           Start Analyse");
        logClientInfo(clientUserId, clientAppId, treasDate);
    }

    private void logStartUpload(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("           Start Upload");
        logClientInfo(clientUserId, clientAppId, treasDate);
    }

    private void logClientInfo(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info(" ClientUserId:\t{} ", clientUserId);
        SwiftLoggers.getLogger().info(" ClientAppId:\t{} ", clientAppId);
        SwiftLoggers.getLogger().info(" TreasDate:\t{} ", treasDate);
        SwiftLoggers.getLogger().info("======================================");
    }

    private class AnalyseTask implements Runnable {

        private String appKey;
        private String appSecret;
        private String clientUserId;
        private String clientAppId;
        private String treasDate;

        public AnalyseTask(String appKey, String appSecret, String clientUserId, String clientAppId, String treasDate) {
            this.appKey = appKey;
            this.appSecret = appSecret;
            this.clientUserId = clientUserId;
            this.clientAppId = clientAppId;
            this.treasDate = treasDate;
        }

        @Override
        public void run() {
            try {
                // 通过客户的用户ID、客户的应用ID和客户的数据包日期获取数据包的下载链接
                logStartGetDownload(clientUserId, clientAppId, treasDate);
                String downloadLink = SwiftCloudUtils.getDownloadLink(appKey, appSecret, clientUserId, clientAppId, treasDate);

                if (Strings.isNotEmpty(downloadLink)) {
                    SwiftLoggers.getLogger().info("get download link success. link is {}", downloadLink);
                    InputStream inputStream = new URL(downloadLink).openStream();
                    String downloadPath = DOWNLOAD_ROOT_PATH + "/" + clientUserId + "/" + treasDate;
                    ZipUtils.unZip(downloadPath, inputStream);
                    logStartAnalyse(clientUserId, clientAppId, treasDate);
                    // TODO 2019/02/25 接导入 + 生成报告

                    // 云端的path
                    String reportPath = "";
                    String filePath = "";
                    File report = new File(filePath);
                    logStartUpload(clientUserId, clientAppId, treasDate);
                    boolean upload = SwiftCloudUtils.upload(report, appKey, appSecret, clientUserId, clientAppId, treasDate, reportPath);
                    if (upload) {
                        SwiftLoggers.getLogger().info("Upload report {} success", reportPath);
                    }
                } else {
                    throw new RuntimeException("Download link is empty");
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn(e);
            }
        }
    }


}

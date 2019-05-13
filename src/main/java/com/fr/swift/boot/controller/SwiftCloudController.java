package com.fr.swift.boot.controller;

import com.fr.swift.cloud.SwiftCloudConstants;
import com.fr.swift.cloud.SwiftCloudUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.task.AnalyseTask;
import com.fr.swift.util.Strings;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author yee
 * @date 2019-02-25
 */
@RestController
public class SwiftCloudController {
    /**
     * TODO: 2019/02/25 用户名密码先临时写定，先下载目录放在当前目录县
     */
    private static final String USERNAME = "冰轮蓝蓝";
    private static final String PASSWORD = "fr110059";

    private Map<String, String> authMap = new ConcurrentHashMap<String, String>();
    private ExecutorService service = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory(SwiftCloudController.class));

    public static void logClientInfo(String clientUserId, String clientAppId, String treasDate) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info(" ClientUserId:\t{} ", clientUserId);
        SwiftLoggers.getLogger().info(" ClientAppId:\t{} ", clientAppId);
        SwiftLoggers.getLogger().info(" TreasDate:\t{} ", treasDate);
        SwiftLoggers.getLogger().info("======================================");
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

    // TODO: 2019/5/10 by lucifer to be deleted
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "/cloud/analyse", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Map<String, Object> triggerAnalyse(@RequestBody UploadInfo uploadInfo) {
        String clientUserId = uploadInfo.getClientUserId();
        String clientAppId = uploadInfo.getClientAppId();
        String treasDate = uploadInfo.getTreasDate();
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
}
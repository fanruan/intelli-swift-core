package com.fr.swift.cloud.util;

import com.fr.swift.log.SwiftLoggers;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
public class CloudLogUtils {

    public static void logStartJob(String clientUserId, String clientAppId, String treasDate, String version, String job) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info("     Start " + job);
        logClientInfo(clientUserId, clientAppId, treasDate, version);
    }

    private static void logClientInfo(String clientUserId, String clientAppId, String treasDate, String version) {
        SwiftLoggers.getLogger().info("======================================");
        SwiftLoggers.getLogger().info(" ClientUserId:\t{} ", clientUserId);
        SwiftLoggers.getLogger().info(" ClientAppId:\t{} ", clientAppId);
        SwiftLoggers.getLogger().info(" TreasDate:\t{} ", treasDate);
        SwiftLoggers.getLogger().info(" Version:\t{} ", version);
        SwiftLoggers.getLogger().info("======================================");
    }
}

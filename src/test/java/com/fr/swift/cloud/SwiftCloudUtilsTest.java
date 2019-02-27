package com.fr.swift.cloud;

import com.fr.swift.util.Strings;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-02-25
 */
public class SwiftCloudUtilsTest {

    // 帆软市场的用户名
    private static final String USERNAME = "冰轮蓝蓝";
    // 帆软市场的密码
    private static final String PASSWORD = "fr110059";

    @Test
    @Ignore
    /**
     * download还调不通
     */
    public void getDownloadLink() {
        Map<String, String> res = SwiftCloudUtils.getUserInfo(USERNAME, PASSWORD);
        if (res != null) {
            String appKey = res.get("app_key");
            String appSecret = res.get("app_secret");

            // 通过客户的用户ID、客户的应用ID和客户的数据包日期获取数据包的下载链接
            String downloadLink = SwiftCloudUtils.getDownloadLink(appKey, appSecret, "140045", "fa7fa29a-4581-464d-8088-641663ace623", "201902");
            // 打印结果
            assertTrue(Strings.isNotEmpty(downloadLink));
        }
    }

    @Test
    @Ignore
    /**
     * upload还调不通
     */
    public void upload() {
        // 用户认证
        Map<String, String> res = SwiftCloudUtils.getUserInfo(USERNAME, PASSWORD);
        if (res != null) {
            String appKey = res.get("app_key");
            String appSecret = res.get("app_secret");
            // 通过报告上传的地址获取报告上传的Token
            String reportPath = "analyze/report_140045_fa7fa29a-4581-464d-8088-641663ace623_201902.txt";
            // 本地报告文件
            File reportFile = new File("/Users/yee/Downloads/report_140045_fa7fa29a-4581-464d-8088-641663ace623_201902.txt");

            // 通过报告本地的路径、报告上传的地址和报告上传的Token上传报告
            boolean upload = SwiftCloudUtils.upload(reportFile, appKey, appSecret, "140045", "fa7fa29a-4581-464d-8088-641663ace623", "201902", reportPath);
            // 打印结果
            assertTrue(upload);
        }
    }
}
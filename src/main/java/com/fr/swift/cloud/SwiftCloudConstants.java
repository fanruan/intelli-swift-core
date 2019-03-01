package com.fr.swift.cloud;


import com.fr.swift.util.IoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yee
 * @date 2019-02-25
 */
public class SwiftCloudConstants {
    /**
     * 下载的zip存储的位置
     */
    public static final String ZIP_FILE_PATH = String.format("%s/analyseSourceData", System.getProperty("user.dir"));
    private static String ROOT_URL;


    private static final String ANALYZE_URL = "/api/v1/analyze";
    static final String ANALYZE_AUTH_URL = ROOT_URL + ANALYZE_URL;

    public static final String SUCCESS = "success";
    static final String HMAC_MD5 = "HmacMD5";
    static final String VERSION = "1.0";
    static final String SIGN_METHOD = "hmac";
    /**
     * Download
     */
    static final String DOWNLOAD_URL = ANALYZE_AUTH_URL + "/download/link";
    /**
     * Upload
     */
    static final String UPLOAD_URL = ANALYZE_AUTH_URL + "/upload/report";

    /**
     * root url可以从外部读取
     * PS 主要为了方便debug
     */
    static {
        File config = new File("cloud.properties");
        InputStream inputStream = null;
        try {
            if (config.exists()) {
                inputStream = new FileInputStream(config);
            } else {
                inputStream = SwiftCloudConstants.class.getResourceAsStream("/cloud.properties");
            }
            if (null != inputStream) {
                Properties properties = new Properties();
                properties.load(inputStream);
                ROOT_URL = properties.getProperty("url.root", "https://market.fanruan.com");
            } else {
                ROOT_URL = "https://market.fanruan.com";
            }
        } catch (Exception ignore) {
            ROOT_URL = "https://market.fanruan.com";
        } finally {
            IoUtil.close(inputStream);
        }
    }
}

package com.fr.swift.cloud;


/**
 * @author yee
 * @date 2019-02-25
 */
public class SwiftCloudConstants {
    private static final String ROOT_URL = "http://192.168.5.83:3008";
    private static final String ANALYZE_URL = "/api/v1/analyze";
    static final String ANALYZE_AUTH_URL = ROOT_URL + ANALYZE_URL;

    static final String SUCCESS = "success";
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
    static final String UPLOAD_TOKEN_URL = ANALYZE_AUTH_URL + "/upload/token";
    static final String UPLOAD_URL = "http://upload.qiniu.com";
    static final String UPLOAD_SUBMIT_URL = ANALYZE_AUTH_URL + "/upload/report/submit";
}

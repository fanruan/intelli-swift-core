package com.fr.swift.cloud;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Strings;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019-02-25
 */
public class SwiftCloudUtils {
    public static Map<String, String> getUserInfo(String username, String password) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("username", username);
        params.put("password", password);

        Response response = null;
        try {
            response = getResponse(SwiftCloudConstants.ANALYZE_AUTH_URL, JsonBuilder.writeJsonString(params));
            Map<String, String> result = new HashMap<String, String>();
            if (null != response && response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String jsonString = responseBody.string();

                Map responseMap = JsonBuilder.readValue(jsonString, Map.class);

                if (SwiftCloudConstants.SUCCESS.equals(responseMap.get("status"))) {
                    return (Map<String, String>) responseMap.get("data");
                }
            }
            return result;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        } finally {
            IoUtil.close(response);
        }
        return Collections.emptyMap();
    }

    /**
     * 获取认证签名
     *
     * @param map       map
     * @param appSecret appSecret
     */
    public static String getSign(HashMap<String, Object> map, String appSecret) {
        String[] sortedArray = map.keySet().toArray(new String[0]);
        Arrays.sort(sortedArray);
        StringBuilder stringBuilder = new StringBuilder();
        for (String element : sortedArray) {
            stringBuilder.append(element).append(map.get(element));
        }
        return byte2hex(encryptHMAC(stringBuilder.toString(), appSecret));
    }

    private static byte[] encryptHMAC(String plainText, String appSecret) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(Charset.forName("UTF-8")), SwiftCloudConstants.HMAC_MD5);
            Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
            mac.init(secretKeySpec);
            return mac.doFinal(plainText.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }
        return new byte[0];
    }

    private static String byte2hex(byte[] encryptHMAC) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte element : encryptHMAC) {
            String hexString = Integer.toHexString(element & 255);
            if (hexString.length() == 1) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hexString.toUpperCase());
        }
        return stringBuilder.toString();
    }


    public static String getDownloadLink(String appKey, String appSecret, String clientUserId, String clientAppId, String treasDate) {
        HashMap<String, Object> params = getSignMap(appKey);
        // 数据包信息
        params.put("client_user_id", clientUserId);
        params.put("client_app_id", clientAppId);
        params.put("treas_date", treasDate);
        // 签名
        params.put("sign", getSign(params, appSecret));
        Response response = null;
        try {

            response = getResponse(SwiftCloudConstants.DOWNLOAD_URL, JsonBuilder.writeJsonString(params));

            if (null != response && response.isSuccessful()) {
                ResponseBody body = response.body();
                Map responseMap = JsonBuilder.readValue(body.string(), Map.class);
                if (SwiftCloudConstants.SUCCESS.equals(responseMap.get("status"))) {
                    return responseMap.get("data").toString();
                } else {
                    SwiftLoggers.getLogger().warn(responseMap.get("error").toString());
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        } finally {
            IoUtil.close(response);
        }
        return Strings.EMPTY;
    }

    private static HashMap<String, Object> getSignMap(String appKey) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("app_key", appKey);
        params.put("v", SwiftCloudConstants.VERSION);
        params.put("sign_method", SwiftCloudConstants.SIGN_METHOD);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return params;
    }

    private static Response getResponse(String url, String requestJson) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestJson);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return null;
    }


    /**
     * 报告上传
     *
     * @param report       待上传的本地文件
     * @param appKey       用户appKey 通过auth获得
     * @param appSecret    用户appSecret 通过auth获取
     * @param clientUserId 客户的用户ID
     * @param clientAppId  客户的应用ID
     * @param treasDate    客户的数据包日期
     * @param reportPath   云端的报告地址
     * @return 上传是否成功
     */
    public static boolean upload(File report, String appKey, String appSecret, String clientUserId, String clientAppId, String treasDate, String reportPath) {
        HashMap<String, Object> params = getSignMap(appKey);
        String timeStamp = String.valueOf(System.currentTimeMillis());
        params.put("timestamp", timeStamp);

        params.put("client_user_id", clientUserId);
        params.put("client_app_id", clientAppId);
        params.put("treas_date", treasDate);
        params.put("report_path", reportPath);
        // 签名
        params.put("sign", getSign(params, appSecret));
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                builder.addFormDataPart(param.getKey(), param.getValue().toString());
            }
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), report);
            RequestBody requestBody = builder
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", report.getName(), fileBody)
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url(SwiftCloudConstants.UPLOAD_URL)
                    .put(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            boolean success = response.isSuccessful();
            response.close();
            return success;
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return false;
    }
}
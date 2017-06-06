package com.fr.bi.stable.report.update.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kary on 2017/1/23.
 * 此处的升级内容：
 * 驼峰以及自定义key值修改
 * 图片uri修正
 * 散点气泡图type升级成点图的type

 */
public class ProfilesUpdateOperation implements ReportUpdateOperation {
    private static final String DEFAULT_FILE_NAME = "keys.json";
    private JSONObject keys;
    private static Pattern linePattern = Pattern.compile("(?!^)_(\\w)");

    public ProfilesUpdateOperation() {
        try {
            if (null == keys) {
                keys = readKeyJson();
                formatValues();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

    }

    @Override
    public JSONObject update(JSONObject reportSetting) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(reportSetting.toString())) {
            reportSetting = recursionMapUpdate(reportSetting.toString());
            return reportSetting;
        } else {
            return reportSetting;
        }
    }

    //map 结构的递归
    JSONObject recursionMapUpdate(String obj) throws JSONException {
        JSONObject json = new JSONObject(obj);
        JSONObject res = new JSONObject();
        Set<String> keySet = json.toMap().keySet();
        for (String s : keySet) {
            boolean flag = BIJsonUtils.isKeyValueSet(json.get(s).toString());
            if (flag) {
                if (ComparatorUtils.equals(s, "widgets")) {
                    json = correctPreviousSrcError(json);
                    json=correctScatterType(json);
                }
                res.put(updateKey(s), recursionMapUpdate(json.getString(s)));
            } else {
                res.put(updateKey(s), recursionListUpdate(json.get(s)));
            }
        }
        return res;
    }
/*
* 散点气泡图type升级
* type 26，28->67
* */
    private JSONObject correctScatterType(JSONObject json) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
            Iterator keys = json.getJSONObject("widgets").keys();
            while (keys.hasNext()) {
                String dimId = keys.next().toString();
                JSONObject dimJson = json.getJSONObject("widgets").getJSONObject(dimId);
                if (dimJson.has("type")) {
                    if (dimJson.getInt("type")== BIReportConstant.WIDGET.BUBBLE||dimJson.getInt("type")==BIReportConstant.WIDGET.SCATTER)
                    dimJson.put("type", BIReportConstant.WIDGET.DOT);
                }
            }
        }
        return json;
    }


    /*
    * 处理之前stable版本保存图片时把整个url全保存进去了，没有地方拦截了，先在此处修正
    * /WebReport/ReportServer?op=fr_bi&cmd=get_uploaded_image&image_id=47a49db9-6a37-46ab-96a3-d615c46ccecc_表样.jpg" -> 47a49db9-6a37-46ab-96a3-d615c46ccecc_表样.jpg"
    * */
    private JSONObject correctPreviousSrcError(JSONObject json) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(json.getString("widgets"))) {
            Iterator keys = json.getJSONObject("widgets").keys();
            while (keys.hasNext()) {
                String dimId = keys.next().toString();
                JSONObject dimJson = json.getJSONObject("widgets").getJSONObject(dimId);
                if (dimJson.has("src")) {
                    dimJson.put("src", correctImgSrc(dimJson.getString("src")));
                }
            }
        }
        return json;
    }

    //所有不符合get格式的都当异常处理掉
    private String correctImgSrc(String src) {
        try {
            String content = src.split("//?")[src.split("//?").length - 1];
            String imageInfo = content.split("//&")[content.split("//&").length - 1];
            return imageInfo.substring(imageInfo.lastIndexOf("=") + 1, imageInfo.length());
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return src;
    }

    //list结构的递归
    Object recursionListUpdate(Object object) throws JSONException {
        String str = object.toString();
        if (BIJsonUtils.isArray(str)) {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                array.put(i, recursionListUpdate(array.getString(i)));
            }
            return array;
        } else {
            if (BIJsonUtils.isKeyValueSet(str)) {
                return recursionMapUpdate(str);
            } else {
                return object;
            }
        }
    }

    /*
    * 如果对应关系能在keys.json中找到，使用keys.json
    * 如果获取不了，默认转驼峰
    * */
    protected String updateKey(String str) {
        String res;
        if (keys.has(str)) {
            res = mactchKeysJson(str);
        } else {
            res = lineToCamels(str);
        }
        return res;
    }

    private String mactchKeysJson(String str) {
        try {
            String updatedKeys = null != keys ? keys.optString(str, str) : str;
            if (!ComparatorUtils.equals(updatedKeys, str)) {
                BILoggerFactory.getLogger(this.getClass()).debug(BIStringUtils.append("compatibility warning! the parameter whose name is ", str, " should be transfered"));
            }
            return updatedKeys;
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return str;
    }

    private String lineToCamels(String str) {
        if (str.contains("__")) {
            return str;
        }
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        if (!ComparatorUtils.equals(str, sb.toString())) {
            BILoggerFactory.getLogger(this.getClass()).debug(BIStringUtils.append("compatibility warning! the parameter whose name is ", str, " should be transferd"));
        }
        return sb.toString();
    }

    private void formatValues() throws JSONException {
        JSONObject finalJson = new JSONObject();
        Iterator iterator = keys.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String[] originalKeys = patterValues(keys.getString(key)).split("/");
            for (String oriKey : originalKeys) {
                finalJson.put(oriKey, key);
            }
        }
        keys = finalJson;
    }

    private String patterValues(String originalKey) {
        Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Matcher matcher = pattern.matcher(originalKey);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return originalKey;
    }

    private JSONObject readKeyJson() throws JSONException, ClassNotFoundException, IOException {
        StringBuffer keysStr = new StringBuffer();
        InputStream is = this.getClass().getResourceAsStream(DEFAULT_FILE_NAME);
        if (is == null) {
            BILoggerFactory.getLogger(this.getClass()).error("keys.json not existed in this path" + this.getClass().getResource("").toString());
            return new JSONObject();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = br.readLine()) != null) {
            keysStr.append(s);
        }
        return new JSONObject(keysStr.toString());
    }
}

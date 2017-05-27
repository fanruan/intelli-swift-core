package com.fr.bi.stable.report.update.operation;

import com.finebi.cube.common.log.BILoggerFactory;
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
 * change underScore to Camel
 * 首字母下划线不会被过滤
 */
public class ReportCamelOperation implements ReportUpdateOperation {
    private static final String DEFAULT_FILE_NAME = "keys.json";
    private JSONObject keys;
    private static Pattern linePattern = Pattern.compile("(?!^)_(\\w)");

    public ReportCamelOperation() {
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
                res.put(updateKey(s), recursionMapUpdate(json.getString(s)));
            } else {
                res.put(updateKey(s), recursionListUpdate(json.get(s)));
            }
        }
        return res;
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

    protected String updateKey(String str) {
        String convertToCamel = lineToCamels(str);
        return mactchKeysJson(convertToCamel);
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

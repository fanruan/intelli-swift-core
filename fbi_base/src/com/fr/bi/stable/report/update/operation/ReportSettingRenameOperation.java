package com.fr.bi.stable.report.update.operation;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kary on 2017/1/23.
 * change underScore to Camel
 */
public class ReportSettingRenameOperation implements ReportSettingsUpdateOperation {
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    @Override
    public JSONObject update(JSONObject reportSetting) throws JSONException {
        if (isKeyValueSet(reportSetting.toString())) {
//            JSONObject mapObject = recursionMapUpdate(reportSetting.toString());
            JSONObject mapObject = firstLevelKeyUpdate(reportSetting.toString());
            return mapObject;
        } else {
            return reportSetting;
        }
    }

    private enum JSON_TYPE {
        JSON_TYPE_OBJECT,
        JSON_TYPE_ARRAY,
        JSON_TYPE_ERROR
    }

    //仅修改将最外层的key值
    JSONObject firstLevelKeyUpdate(String str) throws JSONException {
        JSONObject json = new JSONObject(str);
        JSONObject res = new JSONObject();
        Set<String> keySet = json.toMap().keySet();
        for (String s : keySet) {
            res.put(lineToCamel(s), json.get(s));
        }
        return res;
    }

    //map 结构的递归
    JSONObject recursionMapUpdate(String str) throws JSONException {
        JSONObject json = new JSONObject(str);
        JSONObject res = new JSONObject();
        Set<String> keySet = json.toMap().keySet();
        for (String s : keySet) {
            boolean flag = isKeyValueSet(json.get(s).toString());
            if (flag) {
                res.put(lineToCamel(s), recursionMapUpdate(json.getString(s)));
            } else {
                res.put(lineToCamel(s), recursionListUpdate(json.getString(s)));
            }
        }
        return res;
    }

    //list结构的递归
    Object recursionListUpdate(Object object) throws JSONException {
        String str = object.toString();
        if (isArray(str)) {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                array.put(i, recursionListUpdate(array.getString(i)));
            }
            return array;
        } else {
            if (isKeyValueSet(str)) {
                return recursionMapUpdate(str);
            } else {
                return str;
            }
        }
    }

    private JSON_TYPE getJSONType(String str) {
        if (StringUtils.isEmpty(str)) {
            return JSON_TYPE.JSON_TYPE_ERROR;
        }

        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];

        if (firstChar == '{') {
            return JSON_TYPE.JSON_TYPE_OBJECT;
        } else if (firstChar == '[') {
            return JSON_TYPE.JSON_TYPE_ARRAY;
        } else {
            return JSON_TYPE.JSON_TYPE_ERROR;
        }
    }

    private boolean isKeyValueSet(String str) {
        JSON_TYPE type = getJSONType(str);
        return type == JSON_TYPE.JSON_TYPE_OBJECT;
    }

    private boolean isArray(String str) {
        JSON_TYPE type = getJSONType(str);
        return type == JSON_TYPE.JSON_TYPE_ARRAY;
    }

    protected String lineToCamel(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        if (sb.length() > 0) {
            sb.setCharAt(0, sb.substring(0).toLowerCase().toCharArray()[0]);
        }
        return sb.toString().replaceAll("_", "");
    }
}

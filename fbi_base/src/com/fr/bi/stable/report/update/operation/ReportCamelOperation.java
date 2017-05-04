package com.fr.bi.stable.report.update.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kary on 2017/1/23.
 * change underScore to Camel
 * 首字母下划线不会被过滤
 */
public class ReportCamelOperation implements ReportUpdateOperation {
    private static Pattern linePattern = Pattern.compile("(?!^)_(\\w)");

    @Override
    public JSONObject update(JSONObject reportSetting) throws JSONException {
        if (BIJsonUtils.isKeyValueSet(reportSetting.toString())) {
//            reportSetting = firstLevelKeyUpdate(reportSetting.toString());
            reportSetting = recursionMapUpdate(reportSetting.toString());
            return reportSetting;
        } else {
            return reportSetting;
        }
    }

    //仅修改将最外层的key值
    JSONObject firstLevelKeyUpdate(String str) throws JSONException {
        JSONObject json = new JSONObject(str);
        JSONObject res = new JSONObject();
        Set<String> keySet = json.toMap().keySet();
        for (String s : keySet) {
            res.put(updateKey(s), json.get(s));
        }
        return res;
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
        return lineToCamels(str);
    }

    /* 之前的错误处理把"_src"处理为"src"，现在先在这边转过来，之后这段代码会删掉*/
    private String lineToCamels(String str) {
        if (str.contains("__")){
            return str;
        }
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        if (!ComparatorUtils.equals(str,sb.toString())){
            BILoggerFactory.getLogger(this.getClass()).info(BIStringUtils.append("compatibility warning! the parameter whose name is ",str," should be transferd"));
        }
        return sb.toString();
    }
}

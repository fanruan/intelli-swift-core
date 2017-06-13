package com.fr.bi.report.update.operation;

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
        reportSetting=new JSONObject("{\"globalStyle\":{},\"layoutRatio\":{\"x\":1,\"y\":1},\"layoutType\":0,\"widgets\":{\"d11c0029cb879c66\":{\"settings\":{\"theme_color\":\"#4d9375\",\"table_style\":1,\"column_size\":[282,239,331,256,359],\"freeze_first_column\":false,\"show_name\":true,\"show_number\":true},\"view\":{\"10000\":[\"9fea0e688695538a\",\"41fb2ef42f782e12\",\"c6575bed84c7e522\",\"97d64e9b8441a396\",\"246c198993b82897\",\"de550b9d651a6cd0\",\"e16a033bcde7bc47\"]},\"filter_value\":{},\"sort_sequence\":[\"c6575bed84c7e522\"],\"name\":\"明细表\",\"init_time\":1466130070254,\"bounds\":{\"top\":0,\"left\":0,\"width\":1567,\"height\":766},\"page\":0,\"type\":4,\"dimensions\":{\"97d64e9b8441a396\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"购买数量\",\"_src\":{\"field_id\":\"a94b4f32ff962cac\",\"id\":\"a94b4f32ff962cac\"},\"sort\":{\"target_id\":\"97d64e9b8441a396\",\"type\":0},\"used\":true,\"type\":2,\"group\":{\"type\":2}},\"9fea0e688695538a\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"合同类型\",\"_src\":{\"field_id\":\"846bf845825c8a62\",\"id\":\"846bf845825c8a62\"},\"sort\":{\"target_id\":\"9fea0e688695538a\",\"type\":0},\"used\":true,\"type\":1,\"group\":{\"type\":2}},\"c6575bed84c7e522\":{\"filter_value\":{\"filter_value\":[],\"filter_type\":39,\"_src\":{\"field_id\":\"e200afa534593c0f\"}},\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"产品配送地址\",\"_src\":{\"field_id\":\"e200afa534593c0f\",\"id\":\"e200afa534593c0f\"},\"sort\":{\"sort_target\":\"c6575bed84c7e522\",\"type\":1},\"used\":true,\"type\":1,\"group\":{\"type\":2}},\"41fb2ef42f782e12\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"合同付款类型\",\"_src\":{\"field_id\":\"feedfdda95a40f54\",\"id\":\"feedfdda95a40f54\"},\"sort\":{\"target_id\":\"41fb2ef42f782e12\",\"type\":0},\"used\":true,\"type\":1,\"group\":{\"type\":2}},\"246c198993b82897\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"购买的产品\",\"_src\":{\"field_id\":\"73172ca0de3cd1a0\",\"id\":\"73172ca0de3cd1a0\"},\"sort\":{\"target_id\":\"246c198993b82897\",\"type\":0},\"used\":true,\"type\":2,\"group\":{\"type\":2}},\"de550b9d651a6cd0\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"日期(注册时间)\",\"_src\":{\"field_id\":\"29834b75ce254823\",\"id\":\"29834b75ce25482310\"},\"sort\":{\"target_id\":\"de550b9d651a6cd0\",\"type\":0},\"used\":false,\"type\":3,\"group\":{\"type\":10}},\"e16a033bcde7bc47\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"日期(合同签约时间)\",\"_src\":{\"field_id\":\"29440f5b6e2ccb29\",\"id\":\"29440f5b6e2ccb2910\"},\"sort\":{\"target_id\":\"e16a033bcde7bc47\",\"type\":0},\"used\":false,\"type\":3,\"group\":{\"type\":10}}}}},\"version\":\"4.0\",\"dimensions\":{\"97d64e9b8441a396\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"购买数量\",\"_src\":{\"field_id\":\"a94b4f32ff962cac\",\"id\":\"a94b4f32ff962cac\"},\"sort\":{\"target_id\":\"97d64e9b8441a396\",\"type\":0},\"used\":true,\"type\":2,\"group\":{\"type\":2}},\"9fea0e688695538a\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"合同类型\",\"_src\":{\"field_id\":\"846bf845825c8a62\",\"id\":\"846bf845825c8a62\"},\"sort\":{\"target_id\":\"9fea0e688695538a\",\"type\":0},\"used\":true,\"type\":1,\"group\":{\"type\":2}},\"c6575bed84c7e522\":{\"filter_value\":{\"filter_value\":[],\"filter_type\":39,\"_src\":{\"field_id\":\"e200afa534593c0f\"}},\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"产品配送地址\",\"_src\":{\"field_id\":\"e200afa534593c0f\",\"id\":\"e200afa534593c0f\"},\"sort\":{\"target_id\":\"c6575bed84c7e522\",\"type\":0},\"used\":true,\"type\":1,\"group\":{\"type\":2}},\"41fb2ef42f782e12\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"合同付款类型\",\"_src\":{\"field_id\":\"feedfdda95a40f54\",\"id\":\"feedfdda95a40f54\"},\"sort\":{\"target_id\":\"41fb2ef42f782e12\",\"type\":0},\"used\":true,\"type\":1,\"group\":{\"type\":2}},\"246c198993b82897\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"购买的产品\",\"_src\":{\"field_id\":\"73172ca0de3cd1a0\",\"id\":\"73172ca0de3cd1a0\"},\"sort\":{\"target_id\":\"246c198993b82897\",\"type\":0},\"used\":true,\"type\":2,\"group\":{\"type\":2}},\"de550b9d651a6cd0\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"日期(注册时间)\",\"_src\":{\"field_id\":\"29834b75ce254823\",\"id\":\"29834b75ce25482310\"},\"sort\":{\"target_id\":\"de550b9d651a6cd0\",\"type\":0},\"used\":true,\"type\":3,\"group\":{\"type\":10}},\"e16a033bcde7bc47\":{\"dimension_map\":{\"69a45b2ed6606a39\":{\"target_relation\":[]}},\"name\":\"日期(合同签约时间)\",\"_src\":{\"field_id\":\"29440f5b6e2ccb29\",\"id\":\"29440f5b6e2ccb2910\"},\"sort\":{\"target_id\":\"e16a033bcde7bc47\",\"type\":0},\"used\":true,\"type\":3,\"group\":{\"type\":10}}}}");
        if (BIJsonUtils.isKeyValueSet(reportSetting.toString())) {
            reportSetting = recursionMapUpdate(reportSetting.toString());
            return reportSetting;
        } else {
            return reportSetting;
        }
    }

    //map 结构的递归
    public JSONObject recursionMapUpdate(String obj) throws JSONException {
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
                    if (dimJson.getInt("type")== BIReportConstant.WIDGET.BUBBLE||dimJson.getInt("type")==BIReportConstant.WIDGET.SCATTER) {
                        dimJson.put("type", BIReportConstant.WIDGET.DOT);
                    }
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
    public Object recursionListUpdate(Object object) throws JSONException {
        String str = object.toString();
        if (BIJsonUtils.isArray(str)) {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                array.put(i, recursionListUpdate(array.get(i)));
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
    public String updateKey(String str) {
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

package com.finebi.base.utils.data.json;


import com.finebi.base.utils.data.list.ListUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by andrew_asa on 2017/10/11.
 */
public class JsonUtils {

    /**
     * 空数组
     */
    public static final String EMPTY_ARRAY = "[]";

    /**
     * 空map
     */
    public static final String EMPTY_MAP = "{}";

    /**
     * 确保不是空list字符串
     *
     * @param str
     * @return
     */
    public static String makeSureNotNullArrayStr(String str) {

        if (StringUtils.isEmpty(str)) {
            return EMPTY_ARRAY;
        }
        return str;
    }

    /**
     * 确保不是空map字符串
     *
     * @param str
     * @return
     */
    public static String makeSureNotNullMapStr(String str) {

        if (StringUtils.isEmpty(str)) {
            return EMPTY_MAP;
        }
        return str;
    }

    /**
     * list 转 JSONArray
     *
     * @param list
     * @return
     */
    public static JSONArray listToJsonArray(List<Object> list) {

        JSONArray ret = new JSONArray();
        if (ListUtils.isNotEmptyList(list)) {
            for (Object o : list) {
                ret.put(o);
            }
        }
        return ret;
    }

    /**
     * 字符串转json array
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static JSONArray strToJsonArray(String str) throws JSONException {

        return new JSONArray(makeSureNotNullArrayStr(str));
    }

    /**
     * 字符串转 json object list
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static List<JSONObject> strToJsonObjectList(String str) throws JSONException {

        List<JSONObject> ret = new ArrayList<JSONObject>();
        JSONArray array = strToJsonArray(str);
        for (int i = 0; i < array.length(); i++) {
            ret.add(array.getJSONObject(i));
        }
        return ret;
    }

    /**
     * 字符串转 json array map
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static Map<String, JSONArray> strToJsonArrayMap(String str) throws JSONException {

        Map<String, JSONArray> ret = new HashMap<String, JSONArray>();
        JSONObject jo = new JSONObject(makeSureNotNullMapStr(str));
        Iterator<String> it = jo.keys();
        while (it.hasNext()) {
            String key = it.next();
            JSONArray value = jo.getJSONArray(key);
            ret.put(key, value);
        }
        return ret;
    }

    /**
     * json array 转 list 数组
     *
     * @param array
     * @return
     */
    public static List<String> jsonArrayToStringList(JSONArray array) throws JSONException {

        List<String> ret = new ArrayList<String>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                ret.add(array.getString(i));
            }
        }
        return ret;
    }

}

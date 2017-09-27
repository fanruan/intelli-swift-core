package com.fr.bi.manager;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class created on 2017/4/12.
 *
 * @author Each.Zhang
 */
public class PerformanceParamTools {

    private final static String PERFORMANCE = "performance.";
    private final static String POINT = ".";

    /**
     * 获取传递的字段是否和对象的某个属性相同
     * @param obj
     * @param resultField
     * @return
     */
    private static boolean isObjectField (Object obj, String resultField) {
        if (resultField == null) {
            return false;
        }
        final Class<?> tempClazz = obj.getClass();
        Field[] declaredFields = tempClazz.getDeclaredFields();
        for (Field field : declaredFields) {
            String getField = field.getName();
            if (resultField.equals(getField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将获取到的String类型参数进行转换
     * 转换成Map<String, String>格式的数据，然后供下一步进行操作
     * @param params
     * @return Map<String, String> returnMap
     */
    public static Map<String, String> convertParams(String params) {
        if (params.isEmpty() || params == ""){
            return null;
        }
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            JSONObject resultObject = new JSONObject(params);
            Map<String, Object> tempMap = resultObject.toMap();
            Iterator<String> it = tempMap.keySet().iterator();
            while (it.hasNext()) {
                String paramKey = it.next();
                String paramValue = tempMap.get(paramKey).toString();
                resultMap.put(paramKey, paramValue);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return resultMap;
    }

    /**
     * 判断传递的参数是否属于对象参数
     * @param obj
     * @param resultMap
     * @return
     */
    public static boolean isBelongConfig (Object obj, Map<String, String> resultMap) {
        Iterator<String> it = resultMap.keySet().iterator();
        boolean isParam = true;
        while (it.hasNext()) {
            String paramKey = it.next();
            isParam = isObjectField(obj, paramKey);
            if (isParam == false) {
                return isParam;
            }
        }
        return isParam;
    }

    /**
     * 将参数的key进行转化
     * @param tempMap
     * @return
     */
    public static Map<String, String> convertParamKey (Map<String, String> tempMap) {
        Map<String, String> returnMap = new HashMap<String, String>();
        Iterator<String> it = tempMap.keySet().iterator();
        while (it.hasNext()) {
            String resultParamKey = it.next();
            String resultParamValue = tempMap.get(resultParamKey);
            if (resultParamKey.equals("emptyWhenNotSelect")) {
                returnMap.put(PERFORMANCE + "returnEmptyIndex", resultParamValue);
            } else if (resultParamKey.equals("diskSort")) {
                returnMap.put(PERFORMANCE + "useDiskSort", resultParamValue);
            } else {
                returnMap.put(PERFORMANCE + resultParamKey, resultParamValue);
            }
        }
        return returnMap;
    }

    /**
     * 将获取到配置文件的参数，进行转化
     * @param tempMap
     * @return
     */
    public static Map<String, String> convert2File (Map<String, String> tempMap) {
        Map<String, String> returnMap = new HashMap<String, String>();
        Iterator<String> it = tempMap.keySet().iterator();
        while (it.hasNext()) {
            String resultParamKey = it.next();
            String resultParamValue = tempMap.get(resultParamKey);
            if (resultParamKey.equals(PERFORMANCE + "returnEmptyIndex")) {
                returnMap.put("emptyWhenNotSelect", resultParamValue);
            } else if (resultParamKey.equals(PERFORMANCE + "useDiskSort")) {
                returnMap.put("diskSort", resultParamValue);
            } else {
                returnMap.put(resultParamKey.substring(resultParamKey.indexOf(POINT) + 1), resultParamValue);
            }
        }
        return returnMap;
    }

    /**
     * 合并Map
     * @return
     */
    public static Map<String, String> mergeMaps(Map<String, String> paramMap1, Map<String, String> paramMap2) {
        Map<String, String> resultMap = new HashMap<String, String>();
        Iterator<String> paramIt = paramMap1.keySet().iterator();
        while (paramIt.hasNext()) {
            String key = paramIt.next();
            String value = paramMap2.get(key);
            if (value == null) {
                value = paramMap1.get(key);
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }
}

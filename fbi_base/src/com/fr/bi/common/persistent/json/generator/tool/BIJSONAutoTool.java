package com.fr.bi.common.persistent.json.generator.tool;

import com.fr.bi.common.persistent.json.analyser.BIBeanJSONAnalyserWrapper;
import com.fr.bi.common.persistent.json.generator.BIBeanJSONWriterWrapper;
import com.fr.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Connery on 2016/1/21.
 */
public class BIJSONAutoTool {

    public static Object generateJSON(Object target, String headTag, boolean useTag) throws JSONException {
        return new BIBeanJSONWriterWrapper(target, headTag, useTag).generateJSON(new HashMap<String, List<BIBeanJSONWriterWrapper>>());
    }

    public static Object generateObject(String jsonContent, Class targetClass) throws JSONException
            , NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return new BIBeanJSONAnalyserWrapper(targetClass).analysisJSON(jsonContent);
    }
}
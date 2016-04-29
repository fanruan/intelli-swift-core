package com.fr.bi.common.persistent.json.analyser;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

import java.util.Collection;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONIteratorObjectAnalyser extends JSONObjectAnalyser {
    public JSONIteratorObjectAnalyser(BIBeanJSONAnalyserWrapper analyserWrapper) {
        super(analyserWrapper);
    }

    @Override
    public Object analysisJSONContent(Object jsonContent) throws JSONException {
        Collection bean = (Collection) analyserWrapper.getBean();
        Class element = analyserWrapper.getGenericType();
        JSONArray array;
        if (jsonContent instanceof JSONArray) {
            array = (JSONArray) jsonContent;
        } else if (jsonContent instanceof String) {
            array = new JSONArray(jsonContent);
        } else {
            throw new JSONException("Iterator json Content type is inaccuracy ");
        }
        for (int i = 0; i < array.length(); i++) {
            try {
                Object obj = array.get(i);
                if (BITypeUtils.isBasicValue(element)) {
                    bean.add(BITypeUtils.stringConvert2BasicType(element, (String) obj));
                } else {
                    Object elementObj = BIConstructorUtils.constructObject(element);
                    BIBeanJSONAnalyserWrapper wrapper = new BIBeanJSONAnalyserWrapper(elementObj);
                    Object elementResult = wrapper.analysisJSON(obj);
                    bean.add(elementResult);
                }
            } catch (Exception ignore) {
                BILogger.getLogger().error(ignore.getMessage(), ignore);
                continue;
            }
        }
        return bean;
    }
}
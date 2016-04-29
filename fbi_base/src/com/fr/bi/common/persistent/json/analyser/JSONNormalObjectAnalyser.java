package com.fr.bi.common.persistent.json.analyser;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONNormalObjectAnalyser extends JSONObjectAnalyser {

    public JSONNormalObjectAnalyser(BIBeanJSONAnalyserWrapper analyserWrapper) {
        super(analyserWrapper);
    }

    @Override

    public Object analysisJSONContent(Object jsonContent) throws JSONException {
        JSONObject content = (JSONObject) jsonContent;
        Iterator it = content.keys();
        while (it.hasNext()) {
            try {
                String fieldName = (String) it.next();
                Field field = analyserWrapper.getField(fieldName);
                String value = content.getString(fieldName);
                Class fieldType = field.getType();
                if (BITypeUtils.isBasicValue(fieldType)) {
                    Object objValue = BITypeUtils.stringConvert2BasicType(fieldType, value);
                    analyserWrapper.setOriginalValue(fieldName, objValue);
                } else {
                    String fieldJSONContent = content.getString(field.getName());
                    if (fieldJSONContent == null || "null".equals(fieldJSONContent)) {
                        continue;
                    }
                    BIBeanJSONAnalyserWrapper wrapper = constructFieldObj(field, fieldJSONContent);

                    Object fieldValue = wrapper.analysisJSON(fieldJSONContent);
                    analyserWrapper.setOriginalValue(fieldName, fieldValue);
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return analyserWrapper.getBean();
    }


}
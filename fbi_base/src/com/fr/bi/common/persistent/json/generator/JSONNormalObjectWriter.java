package com.fr.bi.common.persistent.json.generator;

import com.fr.bi.common.persistent.json.generator.anno.BIJSONElement;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONNormalObjectWriter extends JSONObjectWriter {
    public JSONNormalObjectWriter(BIBeanJSONWriterWrapper writerWrapper, Map<String, List<BIBeanJSONWriterWrapper>> disposedBeans) {
        super(writerWrapper, disposedBeans);
    }

    @Override
    protected Object dealWithContent() throws JSONException {
        JSONObject content = new JSONObject();
        content = generateSampleContent(content);
        if (!writerWrapper.getSampleModel()) {
            content = generateComplexContent(content);
        }
        return content;
    }

    private JSONObject generateSampleContent(JSONObject result) {
        Iterator<Field> fields = writerWrapper.seekBasicTypeField().iterator();
        while (fields.hasNext()) {
            Field field = fields.next();
            try {
                Object value = writerWrapper.getOriginalValue(field);
                if (value == null) {
                    result.put(field.getName(), "null");
                } else {
                    result.put(field.getName(), value);
                }
            } catch (Exception ignore) {
                continue;
            }
        }
        return result;
    }

    private JSONObject generateComplexContent(JSONObject result) {
        Iterator<Field> fields = writerWrapper.seekSpecificTaggedAllField(BIJSONElement.class).iterator();
        while (fields.hasNext()) {
            Field field = fields.next();
            try {
                Object value = writerWrapper.getOriginalValue(field);
                if (value == null) {
                    result.put(field.getName(), "null");
                } else {
                    if (BITypeUtils.isBasicValue(value.getClass())) {

                    } else if (!writerWrapper.getSampleModel()) {
                        result.put(field.getName(), new BIBeanJSONWriterWrapper(value, field.getName(), true).generateJSON(disposedBeans));
                    }
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return result;
    }
}
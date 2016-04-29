package com.fr.bi.common.persistent.json.generator;

import com.fr.bi.common.persistent.json.generator.tool.JSONMapKeyExtractor;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONMapObjectWriter extends JSONObjectWriter {
    public JSONMapObjectWriter(BIBeanJSONWriterWrapper writerWrapper, Map<String, List<BIBeanJSONWriterWrapper>> disposedBeans) {
        super(writerWrapper, disposedBeans);
    }


    @Override
    protected Object dealWithContent() throws JSONException {
        JSONObject jsonMap = new JSONObject();
        Map map = (Map) writerWrapper.getBean();
        Iterator<Map.Entry> it = map.entrySet().iterator();
        while (it.hasNext()) {
            try {
                Map.Entry entry = it.next();
                Object value = entry.getValue();
                String keyString = new JSONMapKeyExtractor(entry.getKey()).extractKeyFieldValue();
                jsonMap.put(keyString, new BIBeanJSONWriterWrapper(value, "", false).generateJSON(disposedBeans));
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return jsonMap;
    }
}
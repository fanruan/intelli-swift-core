package com.fr.bi.common.persistent.json.generator;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONIteratorObjectWriter extends JSONObjectWriter {
    public JSONIteratorObjectWriter(BIBeanJSONWriterWrapper writerWrapper, Map<String, List<BIBeanJSONWriterWrapper>> disposedBeans) {
        super(writerWrapper, disposedBeans);
    }


    @Override
    protected Object dealWithContent() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        Iterator iterator = ((Iterable) writerWrapper.getBean()).iterator();
        while (iterator.hasNext()) {
            try {
                Object value = iterator.next();
                jsonArray.put(new BIBeanJSONWriterWrapper(value, "", false).generateJSON(disposedBeans));
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return jsonArray;
    }
}
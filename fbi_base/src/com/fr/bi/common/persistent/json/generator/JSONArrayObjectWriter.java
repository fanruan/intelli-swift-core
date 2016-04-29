package com.fr.bi.common.persistent.json.generator;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;

import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONArrayObjectWriter extends JSONObjectWriter {

    public JSONArrayObjectWriter(BIBeanJSONWriterWrapper writerWrapper, Map<String, List<BIBeanJSONWriterWrapper>> disposedBeans) {
        super(writerWrapper, disposedBeans);
    }

    @Override
    protected Object dealWithContent() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        Object[] array = (Object[]) writerWrapper.getBean();
        for (int i = 0; i < array.length; i++) {
            jsonArray.put(new BIBeanJSONWriterWrapper(array[i], "", false).generateJSON(disposedBeans));
        }
        return jsonArray;
    }
}
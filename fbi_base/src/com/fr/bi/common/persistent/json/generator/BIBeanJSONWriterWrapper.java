package com.fr.bi.common.persistent.json.generator;

import com.fr.bi.common.persistent.BIBeanWriterWrapper;
import com.fr.json.JSONException;

import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2016/1/19.
 */
public class BIBeanJSONWriterWrapper extends BIBeanWriterWrapper {
    private String jsonKey;
    private boolean isSampleModel;
    private boolean useJsonKey;


    public BIBeanJSONWriterWrapper(Object bean, String jsonKey, boolean useJsonKey) {
        super(bean, bean.getClass());
        this.jsonKey = jsonKey;
        this.useJsonKey = useJsonKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public boolean isUseJsonKey() {
        return useJsonKey;
    }

    public void setUseJsonKey(boolean useJsonKey) {
        this.useJsonKey = useJsonKey;
    }

    public Boolean getSampleModel() {
        return isSampleModel;
    }

    public void setSampleModel(Boolean sampleModel) {
        isSampleModel = sampleModel;
    }

    public Object generateJSON(Map<String, List<BIBeanJSONWriterWrapper>> disposedBeans) throws JSONException {
        if (isIterableType()) {
            return new JSONIteratorObjectWriter(this, disposedBeans).generateJSONObject();
        } else if (isMapType()) {
            return new JSONMapObjectWriter(this, disposedBeans).generateJSONObject();
        } else if (isArrayType()) {
            return new JSONArrayObjectWriter(this, disposedBeans).generateJSONObject();
        } else {
            return new JSONNormalObjectWriter(this, disposedBeans).generateJSONObject();
        }
    }
}
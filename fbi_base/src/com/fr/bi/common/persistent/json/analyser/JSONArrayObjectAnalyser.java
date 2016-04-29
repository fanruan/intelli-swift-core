package com.fr.bi.common.persistent.json.analyser;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONArrayObjectAnalyser extends JSONObjectAnalyser {


    @Override
    public Object analysisJSONContent(Object jsonContent) throws JSONException {
        Object[] bean = (Object[]) analyserWrapper.getBean();
        JSONArray array = new JSONArray();

        return null;
    }

    public JSONArrayObjectAnalyser(BIBeanJSONAnalyserWrapper analyserWrapper) {
        super(analyserWrapper);
    }
}
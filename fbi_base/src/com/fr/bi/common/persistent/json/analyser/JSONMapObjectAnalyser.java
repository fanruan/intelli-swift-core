package com.fr.bi.common.persistent.json.analyser;

import com.fr.json.JSONException;

/**
 * Created by Connery on 2016/1/19.
 */
public class JSONMapObjectAnalyser  extends JSONObjectAnalyser {
    public JSONMapObjectAnalyser(BIBeanJSONAnalyserWrapper analyserWrapper) {
        super(analyserWrapper);
    }

    @Override
    public Object analysisJSONContent(Object jsonContent) throws JSONException {
        return null;
    }
}
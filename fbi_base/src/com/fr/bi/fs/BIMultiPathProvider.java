package com.fr.bi.fs;

import com.fr.json.JSONObject;

/**
 * Created by wang on 2017/4/20.
 */
public interface BIMultiPathProvider {
    String XML_TAG = "BIMultiPathProvider";
    JSONObject getMultiPath(long userId) throws Exception;
}

package com.fr.bi.conf.provider;

import com.fr.bi.conf.base.cube.data.BILoginInfoInTableField;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/19.
 */
public interface BICubeConfManagerProvider {
    String XML_TAG = "BICubeConfManagerProvider";

    String getCubePath();

    void saveCubePath(String path);

    BILoginInfoInTableField getLoginInfoInTableField();

    void saveLoginInfoInTableField(BILoginInfoInTableField tableField);

    JSONObject createJSON(long userId) throws Exception;

    @Deprecated
    void persistData(long userId);
}

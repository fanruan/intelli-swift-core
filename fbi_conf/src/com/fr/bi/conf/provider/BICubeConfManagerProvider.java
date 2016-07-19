package com.fr.bi.conf.provider;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/19.
 */
public interface BICubeConfManagerProvider {
    String XML_TAG = "BICubeConfManagerProvider";

    String getCubePath();

    void saveCubePath(String path);

    String getLoginField();

    void saveLoginField(String loginField);

    Object getLoginFieldValue(BusinessField field, long userId);

    void updatePackageLastModify();

    long getPackageLastModify();

    void updateMultiPathLastCubeStatus(BIReportConstant.MULTI_PATH_STATUS status);

    BIReportConstant.MULTI_PATH_STATUS getMultiPathCubeStatus();

    JSONObject createJSON(long userId) throws Exception;

    @Deprecated
    void persistData(long userId);
}

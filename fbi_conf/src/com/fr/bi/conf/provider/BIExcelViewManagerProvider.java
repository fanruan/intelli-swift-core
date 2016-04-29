package com.fr.bi.conf.provider;

import com.fr.bi.conf.manager.excelview.SingleUserBIExcelViewManager;
import com.fr.bi.conf.manager.excelview.source.ExcelViewSource;
import com.fr.json.JSONObject;

import java.util.Map;

/**
 * Created by Young's on 2016/4/20.
 */
public interface BIExcelViewManagerProvider {
    String XML_TAG = "BIExcelViewManager";

    SingleUserBIExcelViewManager getExcelViewManager(long userId);

    void saveExcelView(String tableId, ExcelViewSource source, long userId);

    Map<String, ExcelViewSource> getExcelViews(long userId);

    JSONObject createJSON(long userId) throws Exception;

    void clear(long userId);

    /**
     * 持久化数据
     * TODO 应该按照规则自动调用
     *
     * @param userId 用户ID
     */
    @Deprecated
    void persistData(long userId);
}

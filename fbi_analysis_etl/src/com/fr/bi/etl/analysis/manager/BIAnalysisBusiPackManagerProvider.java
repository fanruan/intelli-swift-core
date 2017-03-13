package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public interface BIAnalysisBusiPackManagerProvider extends BISystemPackageConfigurationProvider {

    String XML_TAG = "BIAnalysisBusiPackManager";

    void addTable(AnalysisBusiTable table);

    void removeTable(String tableId, long userId);

    AnalysisBusiTable getTable(String tableId, long userId) throws BITableAbsentException;

    JSONObject saveAnalysisETLTable(long userId, String tableId, String newId, String tableName, String describe, String tableJSON) throws Exception;

}
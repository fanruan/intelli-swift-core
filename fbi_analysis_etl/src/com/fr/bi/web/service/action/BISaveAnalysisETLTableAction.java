package com.fr.bi.web.service.action;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.utils.BILogHelper;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class BISaveAnalysisETLTableAction extends AbstractAnalysisETLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String newId = WebUtils.getHTTPRequestParameter(req, "new_id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String describe = WebUtils.getHTTPRequestParameter(req, "describe");
        AnalysisBusiTable table;
        CubeTableSource source;
        CubeTableSource oriSource = null;
        if (StringUtils.isEmpty(newId)) {
            //编辑||新建
            table = new AnalysisBusiTable(tableId, userId);
            table.setDescribe(describe);
            String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
            JSONObject jo = new JSONObject(tableJSON);
            JSONArray items = jo.getJSONArray(Constants.ITEMS);
            BIAnalysisETLManagerCenter.getAliasManagerProvider().setAliasName(tableId, tableName, userId);
            source = AnalysisETLSourceFactory.createTableSource(items, userId);
            table.setSource(source);
            if (BIAnalysisETLManagerCenter.getDataSourceManager().containBusinessTable(table.getID())) {
                oriSource = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(table);
            }
        } else {
            //复制
            table = new AnalysisBusiTable(newId, userId);
            BIAnalysisETLManagerCenter.getAliasManagerProvider().setAliasName(newId, tableName, userId);
            AnalysisBusiTable oldTable = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            source = oldTable.getTableSource();
            oriSource = oldTable.getTableSource();
            table.setSource(source);
            table.setDescribe(oldTable.getDescribe());
        }
        addTable(table);
        refreshTables(getUsedTables(table, oriSource));
        BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().refresh();
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        checkIndex(userId, source);
        WebUtils.printAsJSON(res, getResult(userId, tableName, table));
        new Thread() {
            public void run() {
                BIAnalysisETLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
                BIAnalysisETLManagerCenter.getDataSourceManager().persistData(userId);
            }
        }.start();
    }

    private void checkIndex(long userId, CubeTableSource source) {
        try {
            BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().checkTableIndex((AnalysisCubeTableSource) source, new BIUser(userId));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(BIStringUtils.append("analysisSource update failed", source.getSourceID(), e.getMessage()),e);
        }
    }

    private void addTable(AnalysisBusiTable table) {
        try {
            BIAnalysisETLManagerCenter.getDataSourceManager().addTableSource(table, table.getTableSource());
            BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Add AnalysisETL table*******");
            BIAnalysisETLManagerCenter.getBusiPackManager().addTable(table);
            BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("The added table is: " + logTable(table));
            BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Add AnalysisETL table*******");
        } catch (BIKeyDuplicateException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

    }

    private Set<AnalysisBusiTable> getUsedTables(AnalysisBusiTable table, CubeTableSource oriSource) {
        Set<AnalysisBusiTable> usedTables = new HashSet<AnalysisBusiTable>();
        if (null != oriSource) {
            for (BusinessTable businessTable : BIAnalysisETLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
                if (ComparatorUtils.equals(businessTable.getTableSource().getSourceID(), oriSource.getSourceID())) {
                    usedTables.add((AnalysisBusiTable) businessTable);
                }
            }
        }
        usedTables.add(table);
        return usedTables;
    }

    private void refreshTables(Set<AnalysisBusiTable> usedTables) {
        Map<BusinessTable, CubeTableSource> refreshTables = new HashMap<BusinessTable, CubeTableSource>();
        try {
            for (BusinessTable table : usedTables) {
                AnalysisCubeTableSource s = null;
                if (null != table) {
                    s = (AnalysisCubeTableSource) BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(table);
                    s.refreshWidget();
                }
                refreshTables.put(table, s);
            }
        } catch (BIKeyAbsentException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        for (BusinessTable table : refreshTables.keySet()) {
            try {
                BIAnalysisETLManagerCenter.getDataSourceManager().addTableSource(table, refreshTables.get(table));
            } catch (BIKeyDuplicateException e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }
    }

    private JSONObject getResult(long userId, String tableName, AnalysisBusiTable table) throws Exception {
        JSONObject result = new JSONObject();
        JSONObject packages = BIAnalysisETLManagerCenter.getBusiPackManager().createPackageJSON(userId);
        JSONObject translations = new JSONObject();
        translations.put(table.getID().getIdentity(), tableName);
        JSONObject tableJSONWithFieldsInfo = table.createJSONWithFieldsInfo(userId);
        JSONObject tableFields = tableJSONWithFieldsInfo.getJSONObject("tableFields");
        JSONObject tables = new JSONObject();
        tables.put(table.getID().getIdentity(), tableFields);
        JSONObject fields = tableJSONWithFieldsInfo.getJSONObject("fieldsInfo");
        result.put("packages", packages);
        result.put("translations", translations);
        result.put("tables", tables);
        result.put("fields", fields);
        return result;
    }

    @Override
    public String getCMD() {
        return "save_table";
    }

    private String logTable(BusinessTable table) {
        try {
            return BILogHelper.logAnalysisETLTable(table) +
                    "\n" + "*********Fields of AnalysisETL table*******" +
                    BILogHelper.logAnalysisETLTableField(table, "") +
                    "\n" + "*********Fields of AnalysisETL table*******";
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIBusinessPackage.class).error(e.getMessage(), e);
            return "";
        }
    }
}

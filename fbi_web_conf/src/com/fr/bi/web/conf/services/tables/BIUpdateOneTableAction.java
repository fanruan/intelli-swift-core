package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.finebi.cube.conf.utils.BIConfUtils;
import com.fr.bi.cal.BICubeManager;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.conf.manager.excelview.source.ExcelViewSource;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.services.session.BIConfSessionUtils;
import com.fr.bi.web.conf.utils.BIWriteConfigResourcesUtils;
import com.fr.data.NetworkHelper;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * 更新一张表信息（可能是新添加的ETL）
 * Created by Young's on 2016/12/21.
 */
public class BIUpdateOneTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String sessionId = NetworkHelper.getHTTPRequestSessionIDParameter(req);
        String translationsStr = WebUtils.getHTTPRequestParameter(req, "translations");
        String tableStr = WebUtils.getHTTPRequestParameter(req, "table");
        String updateSettingStr = WebUtils.getHTTPRequestParameter(req, "updateSettings");
        String excelViewStr = WebUtils.getHTTPRequestParameter(req, "excelView");
        String packageId = WebUtils.getHTTPRequestParameter(req, "packageId");
        boolean isNew = WebUtils.getHTTPRequestBoolParameter(req, "isNew");

        JSONObject tableJO = new JSONObject(tableStr);
        BusinessTable table = updateTable(userId, tableJO, packageId, isNew);

        JSONObject translationsJO = new JSONObject(translationsStr);
        updateTranslation(userId, translationsJO, table);

        JSONObject updateSettingJO = new JSONObject(updateSettingStr);
        updateUpdateSettings(userId, updateSettingJO);

        JSONObject excelViewJO = new JSONObject(excelViewStr);
        updateExcelView(userId, table.getID().getIdentityValue(), excelViewJO);

        //lock
        BIConfSessionUtils.getSession(sessionId).releaseTableLock(table.getID().getIdentityValue());

        BIWriteConfigResourcesUtils.writeResourceAsync(userId);
    }

    private BusinessTable updateTable(long userId, JSONObject tableJO, String packageId, boolean isNew) throws Exception {
        String tableId = tableJO.getString("id");
        BusinessTable table;
        if (isNew && packageId != null) {
            table = new BIBusinessTable(new BITableID(tableId), tableJO.optString("table_name", "ETL"));
            BICubeConfigureCenter.getPackageManager().addTable(userId, new BIPackageID(packageId), (BIBusinessTable) table);
        } else {
            table = BusinessTableHelper.getBusinessTable(new BITableID(tableId));
        }
        if (tableJO.has("fields")) {
            List<BusinessField> fields = BIConfUtils.parseField(tableJO.getJSONArray("fields"), table);
            table.setFields(fields);
        }
        CubeTableSource tableSource = TableSourceFactory.createTableSource(tableJO, userId);
        BICubeConfigureCenter.getDataSourceManager().addTableSource(table, tableSource);
        table.setSource(tableSource);
        return table;
    }

    private void updateTranslation(long userId, JSONObject translations, BusinessTable table) throws Exception {
        String tableId = table.getID().getIdentityValue();
        List<BusinessField> fields = table.getFields();
        BICubeConfigureCenter.getAliasManager().removeAliasName(tableId, userId);
        for (BusinessField field : fields) {
            BICubeConfigureCenter.getAliasManager().removeAliasName(field.getFieldID().getIdentityValue(), userId);
        }
        Iterator<String> keys = translations.keys();
        while (keys.hasNext()) {
            String id = keys.next();
            BICubeConfigureCenter.getAliasManager().setAliasName(id, translations.getString(id), userId);
        }
    }

    private void updateExcelView(long userId, String tableId, JSONObject excelViewJO) throws Exception {
        ExcelViewSource source = new ExcelViewSource();
        source.parseJSON(excelViewJO);
        BIConfigureManagerCenter.getExcelViewManager().saveExcelView(tableId, source, userId);
    }

    private void updateUpdateSettings(long userId, JSONObject updateSettingsJO) throws Exception {
        UpdateSettingSource originalGlobalSetting = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettingManager(userId).getUpdateSettings().get(DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE);
        Iterator<String> sourceTableIds = updateSettingsJO.keys();
        while (sourceTableIds.hasNext()) {
            String sourceTableId = sourceTableIds.next();
            UpdateSettingSource source = new UpdateSettingSource();
            if (ComparatorUtils.equals(sourceTableId, DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE)) {
                source = null != originalGlobalSetting ? originalGlobalSetting : source;
            } else {
                source.parseJSON(updateSettingsJO.getJSONObject(sourceTableId));
            }
            BIConfigureManagerCenter.getUpdateFrequencyManager().saveUpdateSetting(sourceTableId, source, userId);
        }
        BICubeManager biCubeManager = StableFactory.getMarkedObject(BICubeManagerProvider.XML_TAG, BICubeManager.class);
        biCubeManager.resetCubeGenerationHour(userId);
    }

    @Override
    public String getCMD() {
        return "update_one_table";
    }
}

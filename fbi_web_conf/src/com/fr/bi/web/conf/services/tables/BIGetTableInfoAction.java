package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.manager.excelview.source.ExcelViewSource;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.services.packs.BIGetTablesOfOnePackageAction;
import com.fr.bi.web.conf.services.session.BIConfSession;
import com.fr.bi.web.conf.services.session.BIConfSessionUtils;
import com.fr.bi.web.conf.utils.BIWebConfUtils;
import com.fr.data.NetworkHelper;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 获取业务包表详细信息
 * 点击进入表，其他用户不可同时操作当前表
 * Created by Young's on 2016/12/15.
 */
public class BIGetTableInfoAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_table_info";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String sessionId = NetworkHelper.getHTTPRequestSessionIDParameter(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BIConfSession session = (BIConfSession) SessionDealWith.getSessionIDInfor(sessionId);
        boolean isEditing = session.setEdit(true, tableId);
        JSONObject jo = new JSONObject();
        if (!isEditing) {
            WebUtils.printAsJSON(res, new JSONObject().put("lockedBy", BIConfSessionUtils.getCurrentEditingUserByTableId(userId, tableId)));
            return;
        }

        BusinessTable table = BusinessTableHelper.getBusinessTable(new BITableID(tableId));
        //fields
        CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
        JSONObject tableJO = source.createJSON();
        tableJO.put("fields", getFields(table));
        jo.put("table", tableJO);

        //update setting
        List<Set<CubeTableSource>> usedSources = source.createGenerateTablesList();
        JSONObject updateSettingJO = new JSONObject();
        if (usedSources != null) {
            for (Set<CubeTableSource> set : usedSources) {
                for (CubeTableSource tableSource : set) {
                    String sourceId = tableSource.getSourceID();
                    UpdateSettingSource updateSetting = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSetting(sourceId, userId);
                    if (updateSetting != null) {
                        updateSettingJO.put(sourceId, updateSetting.createJSON());
                    }
                }
            }
        }
        jo.put("updateSettings", updateSettingJO);

        //excel view
        JSONObject excelViewJO = new JSONObject();
        ExcelViewSource excelView = BIConfigureManagerCenter.getExcelViewManager().getExcelViewManager(userId).getExcelViewByTableId(tableId);
        if (excelView != null) {
            excelViewJO = excelView.createJSON();
        }
        jo.put("excelView", excelViewJO);

        jo.put(BIJSONConstant.JSON_KEYS.RELATIONS, getAllRelationsOfTable(table));
        jo.put(BIJSONConstant.JSON_KEYS.TRANSLATIONS, getAllTransOfTable(table));

        WebUtils.printAsJSON(res, jo);
    }

    private JSONObject getAllTransOfTable(BusinessTable table) throws Exception {
        JSONObject translations = new JSONObject();
        long userId = UserControl.getInstance().getSuperManagerID();
        String tableId = table.getID().getIdentityValue();
        translations.put(tableId, BICubeConfigureCenter.getAliasManager().getAliasName(tableId, userId));
        Iterator<BusinessField> it = table.getFields().iterator();
        while (it.hasNext()) {
            BusinessField field = it.next();
            String fieldId = field.getFieldID().getIdentityValue();
            translations.put(fieldId, BICubeConfigureCenter.getAliasManager().getAliasName(fieldId, userId));
        }
        return translations;
    }

    private JSONArray getFields(BusinessTable businessTable) {
        JSONArray ja = new JSONArray();
        List<JSONObject> stringList = new ArrayList<JSONObject>();
        List<JSONObject> numberList = new ArrayList<JSONObject>();
        List<JSONObject> dateList = new ArrayList<JSONObject>();
        Iterator<BusinessField> it = businessTable.getFields().iterator();
        while (it.hasNext()) {
            try {
                stringList.add(it.next().createJSON());
            } catch (Exception e) {
                BILoggerFactory.getLogger(BIGetTablesOfOnePackageAction.class).error(e.getMessage(), e);
            }
        }
        ja.put(stringList).put(numberList).put(dateList);
        return ja;
    }

    private JSONObject getAllRelationsOfTable(BusinessTable businessTable) throws Exception {
        long userId = UserControl.getInstance().getSuperManagerID();
        Set<BITableRelation> foreignKeys = BICubeConfigureCenter.getTableRelationManager().getForeignRelation(userId, businessTable).getContainer();
        Set<BITableRelation> primaryKeys = BICubeConfigureCenter.getTableRelationManager().getPrimaryRelation(userId, businessTable).getContainer();

        JSONObject jo = new JSONObject();
        JSONArray connJA = new JSONArray();
        for (BITableRelation relation : primaryKeys) {
            if (isValidRelationByFieldExist(relation)) {
                connJA.put(BIWebConfUtils.createRelationJSONWithName(relation));
            }
        }
        for (BITableRelation relation : foreignKeys) {
            if (isValidRelationByFieldExist(relation)) {
                connJA.put(BIWebConfUtils.createRelationJSONWithName(relation));
            }
        }
        jo.put(BIJSONConstant.JSON_KEYS.PRIMARY_KEY_MAP, getPrimKeyMap(primaryKeys));
        jo.put(BIJSONConstant.JSON_KEYS.FOREIGN_KEY_MAP, getForKeyMap(foreignKeys));
        jo.put(BIJSONConstant.JSON_KEYS.CONNECTION_SET, connJA);
        return jo;
    }

    private boolean isValidRelationByFieldExist(BITableRelation relation) {
        return BIWebConfUtils.isFieldExist(relation.getPrimaryKey()) &&
                BIWebConfUtils.isFieldExist(relation.getForeignKey());
    }

    private JSONObject getPrimKeyMap(Set<BITableRelation> relations) throws Exception {
        JSONObject jo = new JSONObject();
        Map<String, JSONArray> tableRelationMap = new HashMap<String, JSONArray>();
        for (BITableRelation relation : relations) {
            if (isValidRelationByFieldExist(relation)) {
                JSONArray ja = new JSONArray();
                String primaryId = relation.getPrimaryField().getFieldID().getIdentityValue();
                if (tableRelationMap.containsKey(primaryId)) {
                    ja = tableRelationMap.get(primaryId);
                }
                ja.put(BIWebConfUtils.createRelationJSONWithName(relation));
                tableRelationMap.put(primaryId, ja);
            }
        }
        Set<String> tableRelationKeySet = tableRelationMap.keySet();
        for (String primaryFieldId : tableRelationKeySet) {
            if (tableRelationMap.get(primaryFieldId) != null) {
                jo.put(primaryFieldId, tableRelationMap.get(primaryFieldId));
            }
        }
        return jo;
    }

    private JSONObject getForKeyMap(Set<BITableRelation> relations) throws Exception {
        JSONObject jo = new JSONObject();
        Map<String, JSONArray> tableRelationMap = new HashMap<String, JSONArray>();
        for (BITableRelation relation : relations) {
            if (isValidRelationByFieldExist(relation)) {
                JSONArray ja = new JSONArray();
                String foreignId = relation.getForeignField().getFieldID().getIdentity();
                if (tableRelationMap.containsKey(foreignId)) {
                    ja = tableRelationMap.get(foreignId);
                }
                ja.put(BIWebConfUtils.createRelationJSONWithName(relation));
                tableRelationMap.put(foreignId, ja);
            }
        }
        Set<String> tableRelationKeySet = tableRelationMap.keySet();
        for (String foreignFieldId : tableRelationKeySet) {
            if (tableRelationMap.get(foreignFieldId) != null) {
                jo.put(foreignFieldId, tableRelationMap.get(foreignFieldId));
            }
        }
        return jo;
    }
}

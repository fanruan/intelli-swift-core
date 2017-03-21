package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIWebConfUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 关联详细信息
 * Created by Young's on 2017/3/15.
 */
public class BIGetRelationsDetailAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = UserControl.getInstance().getSuperManagerID();
        Set<BITableRelation> connectionSet = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        Map<BusinessTable, IRelationContainer> primKeyMap = BICubeConfigureCenter.getTableRelationManager().getAllTable2PrimaryRelation(userId);
        Map<BusinessTable, IRelationContainer> foreignKeyMap = BICubeConfigureCenter.getTableRelationManager().getAllTable2ForeignRelation(userId);
        Iterator<Map.Entry<BusinessTable, IRelationContainer>> primary = primKeyMap.entrySet().iterator();
        Iterator<Map.Entry<BusinessTable, IRelationContainer>> foreign = foreignKeyMap.entrySet().iterator();
        JSONArray setJO = new JSONArray();
        for (BITableRelation relation : connectionSet) {
            if (BIWebConfUtils.isFieldExist(relation.getPrimaryField()) && BIWebConfUtils.isFieldExist(relation.getForeignField())) {
                setJO.put(BIWebConfUtils.createRelationJSONWithName(relation));
            }
        }
        JSONObject jo = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.CONNECTION_SET, setJO);
        jo.put(BIJSONConstant.JSON_KEYS.PRIMARY_KEY_MAP, getPrimKeyMap(primary));
        jo.put(BIJSONConstant.JSON_KEYS.FOREIGN_KEY_MAP, getForKeyMap(foreign));
        WebUtils.printAsJSON(res, jo);
    }

    private JSONObject getPrimKeyMap(Iterator<Map.Entry<BusinessTable, IRelationContainer>> it) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<BusinessTable, IRelationContainer> entry = it.next();
            String primaryId;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            Map<String, JSONArray> tableRelationMap = new HashMap<String, JSONArray>();
            for (BITableRelation relation : relations) {
                if (BIWebConfUtils.isFieldExist(relation.getPrimaryField()) && BIWebConfUtils.isFieldExist(relation.getForeignField())) {
                    JSONArray ja = new JSONArray();
                    primaryId = relation.getPrimaryField().getFieldID().getIdentityValue();
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
        }
        return jo;
    }

    private JSONObject getForKeyMap(Iterator<Map.Entry<BusinessTable, IRelationContainer>> it) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<BusinessTable, IRelationContainer> entry = it.next();
            String foreignId;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            Map<String, JSONArray> tableRelationMap = new HashMap<String, JSONArray>();
            for (BITableRelation relation : relations) {
                if (BIWebConfUtils.isFieldExist(relation.getPrimaryField()) && BIWebConfUtils.isFieldExist(relation.getForeignField())) {
                    JSONArray ja = new JSONArray();
                    foreignId = relation.getForeignField().getFieldID().getIdentity();
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

        }
        return jo;
    }

    @Override
    public String getCMD() {
        return "get_relations_detail";
    }
}

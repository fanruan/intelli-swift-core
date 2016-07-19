package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Young's on 2016/4/11.
 */
public class BIGetInfoEnterConfAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject relations = createJSONWithTableName(userId);
        JSONObject translations = BICubeConfigureCenter.getAliasManager().getTransManager(userId).createJSON();
        JSONObject jo = new JSONObject();
        jo.put("relations", relations);
        jo.put("translations", translations);
        jo.put("fields", getAllFields(userId));
        jo.put("update_settings", BIConfigureManagerCenter.getUpdateFrequencyManager().createJSON(userId));
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_translations_relations_fields_4_conf";
    }

    private JSONObject getAllFields(long userId) {
        JSONObject fields = new JSONObject();
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(userId);
        try {
            for (IBusinessPackageGetterService p : packs) {
                for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                    Iterator<BusinessField> iterator = t.getFields().iterator();
                    while (iterator.hasNext()) {
                        BusinessField field = iterator.next();
                        fields.put(BIModuleUtils.getBusinessFieldById(field.getFieldID()).getFieldID().getIdentity(), field.createJSON());
                    }
                }
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage());
        }
        return fields;
    }

    private JSONObject createJSONWithTableName(long userId) throws Exception {
        Set<BITableRelation> connectionSet = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        Map<BusinessTable, IRelationContainer> primKeyMap = BICubeConfigureCenter.getTableRelationManager().getAllTable2PrimaryRelation(userId);
        Map<BusinessTable, IRelationContainer> foreignKeyMap = BICubeConfigureCenter.getTableRelationManager().getAllTable2ForeignRelation(userId);
        Iterator<Map.Entry<BusinessTable, IRelationContainer>> primIter = primKeyMap.entrySet().iterator();
        Iterator<Map.Entry<BusinessTable, IRelationContainer>> foreignIter = foreignKeyMap.entrySet().iterator();
        JSONArray setJO = new JSONArray();
        for (BITableRelation relation : connectionSet) {
            setJO.put(relation.createJSON());
        }
        JSONObject jo = new JSONObject();
        jo.put("connectionSet", setJO);
        jo.put("primKeyMap", getPrimKeyMap(primIter));
        jo.put("foreignKeyMap", getForKeyMap(foreignIter));
        return jo;
    }

    private JSONObject getPrimKeyMap(Iterator<Map.Entry<BusinessTable, IRelationContainer>> it) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<BusinessTable, IRelationContainer> entry = it.next();
            JSONArray ja = new JSONArray();
            String primaryId = null;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            for (BITableRelation relation : relations) {
                primaryId = relation.getPrimaryField().getFieldID().getIdentityValue();
                ja.put(relation.createJSON());
                if (primaryId != null) {
                    jo.put(primaryId, ja);
                }
            }
        }
        return jo;
    }

    private JSONObject getForKeyMap(Iterator<Map.Entry<BusinessTable, IRelationContainer>> it) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<BusinessTable, IRelationContainer> entry = it.next();
            String foreignId = null;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            for (BITableRelation relation : relations) {
                JSONArray ja = new JSONArray();
                foreignId = relation.getForeignField().getFieldID().getIdentity();
                ja.put(relation.createJSON());
                if (foreignId != null) {
                    jo.put(foreignId, ja);
                }
            }

        }
        return jo;
    }
}

package com.fr.bi.web.conf.services;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.base.relation.relation.IRelationContainer;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.relation.BISimpleRelation;
import com.fr.bi.stable.relation.BITableRelation;
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
        JSONObject translations = BIConfigureManagerCenter.getAliasManager().getTransManager(userId).createJSON();
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

    private JSONObject getAllFields(long userId) throws Exception{
        JSONObject fields = new JSONObject();
        Set<BIBusinessPackage> packs = BIConfigureManagerCenter.getPackageManager().getAllPackages(userId);
        for (BIBusinessPackage p : packs) {
            for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                Iterator<BIBasicField> iterator = t.getFieldsIterator();
                while (iterator.hasNext()) {
                    BIBasicField field = iterator.next();
                    fields.put(field.getTableID().getIdentity() + field.getFieldName(), field.createJSON());
                }
            }
        }
        return fields;
    }

    private JSONObject createJSONWithTableName(long userId) throws Exception {
        Set<BITableRelation> connectionSet = BIConfigureManagerCenter.getTableRelationManager().getAllTableRelation(userId);
        Map<Table, IRelationContainer> primKeyMap = BIConfigureManagerCenter.getTableRelationManager().getAllTable2PrimaryRelation(userId);
        Map<Table, IRelationContainer> foreignKeyMap = BIConfigureManagerCenter.getTableRelationManager().getAllTable2ForeignRelation(userId);
        Iterator<Map.Entry<Table, IRelationContainer>> primIter = primKeyMap.entrySet().iterator();
        Iterator<Map.Entry<Table, IRelationContainer>> foreignIter = foreignKeyMap.entrySet().iterator();
        JSONArray setJO = new JSONArray();
        for (BITableRelation relation : connectionSet) {
            BISimpleRelation simpleRelation = relation.getSimpleRelation();
            setJO.put(simpleRelation.createJSON());
        }
        JSONObject jo = new JSONObject();
        jo.put("connectionSet", setJO);
        jo.put("primKeyMap", getPrimKeyMap(primIter));
        jo.put("foreignKeyMap", getForKeyMap(foreignIter));
        return jo;
    }

    private JSONObject getPrimKeyMap(Iterator<Map.Entry<Table, IRelationContainer>> it) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<Table, IRelationContainer> entry = it.next();
            JSONArray ja = new JSONArray();
            String primaryId = null;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            for (BITableRelation relation : relations) {
                BISimpleRelation simpleRelation = relation.getSimpleRelation();
                primaryId = simpleRelation.getPrimaryId();
                ja.put(simpleRelation.createJSON());
            }
            if(primaryId != null) {
                jo.put(primaryId, ja);
            }
        }
        return jo;
    }

    private JSONObject getForKeyMap(Iterator<Map.Entry<Table, IRelationContainer>> it) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<Table, IRelationContainer> entry = it.next();
            JSONArray ja = new JSONArray();
            String foreignId = null;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            for (BITableRelation relation : relations) {
                BISimpleRelation simpleRelation = relation.getSimpleRelation();
                foreignId = simpleRelation.getForeignId();
                ja.put(simpleRelation.createJSON());
            }
            if(foreignId != null) {
                jo.put(foreignId, ja);
            }
        }
        return jo;
    }
}

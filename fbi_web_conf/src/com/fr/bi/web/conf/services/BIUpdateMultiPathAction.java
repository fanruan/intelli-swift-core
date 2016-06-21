package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by roy on 16/2/25.
 */
public class BIUpdateMultiPathAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String disabledRelationsStr = WebUtils.getHTTPRequestParameter(req, "disabledRelations");
        String availableRelationsStr = WebUtils.getHTTPRequestParameter(req, "availableRelations");
        long userId = ServiceUtils.getCurrentUserID(req);
        updateDisablePath(disabledRelationsStr, availableRelationsStr, userId);
    }

    @Override
    public String getCMD() {
        return "update_multi_path";
    }

    private void updateDisablePath(String disabledRelationStr, String availableRelationStr, long userId) throws Exception {
        JSONArray disabledJa = new JSONArray(disabledRelationStr);
        JSONArray availableJa = new JSONArray(availableRelationStr);


        for (int i = 0; i < disabledJa.length(); i++) {
            JSONArray pathJa = disabledJa.getJSONArray(i);
            BITableRelationPath disablePath = createPath(pathJa);
            BusinessTable foreignTable = getFirstForeignTable(pathJa);
            BusinessTable primaryTable = getLastPrimaryTable(pathJa);
            Set<BITableRelationPath> allPaths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, foreignTable, primaryTable);
            Iterator it = allPaths.iterator();
            while (it.hasNext()) {
                BITableRelationPath path = (BITableRelationPath) it.next();
                if (ComparatorUtils.equals(path, disablePath)) {
                    if (!BICubeConfigureCenter.getTableRelationManager().isPathDisable(userId, path)) {
                        BICubeConfigureCenter.getTableRelationManager().addDisableRelations(userId, path);
                    }
                }
            }
        }

        for (int i = 0; i < availableJa.length(); i++) {
            JSONArray pathJa = availableJa.getJSONArray(i);
            BITableRelationPath availablePath = createPath(pathJa);
            BusinessTable foreignTable = getFirstForeignTable(pathJa);
            BusinessTable primaryTable = getLastPrimaryTable(pathJa);
            Set<BITableRelationPath> allPaths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, foreignTable, primaryTable);
            Iterator it = allPaths.iterator();
            while (it.hasNext()) {
                BITableRelationPath path = (BITableRelationPath) it.next();
                if (ComparatorUtils.equals(path, availablePath)) {
                    if (BICubeConfigureCenter.getTableRelationManager().isPathDisable(userId, path)) {
                        BICubeConfigureCenter.getTableRelationManager().removeDisableRelations(userId, path);
                    }
                }
            }
        }


    }

    private BusinessTable getFirstForeignTable(JSONArray pathJa) throws Exception {
        JSONObject firstRelationJo = pathJa.getJSONObject(pathJa.length() - 1);
//        BITableRelation re = new BITableRelation();
//        re.parseJSON(firstRelationJo);
        BITableRelation re = BITableRelationHelper.getRelation(firstRelationJo);
        return re.getForeignTable();
    }

    private BusinessTable getLastPrimaryTable(JSONArray pathJa) throws Exception {
        JSONObject firstRelationJo = pathJa.getJSONObject(0);
//        BITableRelation re = new BITableRelation();
//        re.parseJSON(firstRelationJo);
        BITableRelation re = BITableRelationHelper.getRelation(firstRelationJo);
        return re.getPrimaryTable();
    }

    private BITableRelationPath createPath(JSONArray pathJa) throws Exception {
        BITableRelationPath newPath = new BITableRelationPath();
        for (int i = 0; i < pathJa.length(); i++) {
//            BITableRelation re = new BITableRelation();
//            re.parseJSON(pathJa.getJSONObject(i));
            BITableRelation re = BITableRelationHelper.getRelation(pathJa.getJSONObject(i));
            newPath.addRelationAtTail(re);
        }
        return newPath;
    }

}

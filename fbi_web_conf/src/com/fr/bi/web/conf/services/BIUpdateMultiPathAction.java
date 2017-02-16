package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIReportConstant;
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
        String relation = WebUtils.getHTTPRequestParameter(req, "relation");
        long userId = ServiceUtils.getCurrentUserID(req);
        updateRelation(relation, userId);
    }

    @Override
    public String getCMD() {
        return "update_multi_path";
    }

    private void updateRelation(String relation, long userId) throws Exception {
        JSONArray relationJa = new JSONArray(relation);
        BITableRelationPath pathJa = createPath(relationJa);
        BusinessTable foreignTable = getFirstForeignTable(relationJa);
        BusinessTable primaryTable = getLastPrimaryTable(relationJa);
        Set<BITableRelationPath> allPaths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, foreignTable, primaryTable);
        Iterator it = allPaths.iterator();
        while (it.hasNext()) {
            BITableRelationPath path = (BITableRelationPath) it.next();
            if (ComparatorUtils.equals(path, pathJa)) {
                if (BICubeConfigureCenter.getTableRelationManager().isPathDisable(userId, path)) {
                    BICubeConfigureCenter.getTableRelationManager().removeDisableRelations(userId, path);
                } else {
                    BICubeConfigureCenter.getTableRelationManager().addDisableRelations(userId, path);
                }
            }
        }

    }

    private BusinessTable getFirstForeignTable(JSONArray pathJa) throws Exception {
        JSONObject firstRelationJo = pathJa.getJSONObject(pathJa.length() - 1);
        BITableRelation re = BITableRelationHelper.getRelation(firstRelationJo);
        return re.getForeignTable();
    }

    private BusinessTable getLastPrimaryTable(JSONArray pathJa) throws Exception {
        JSONObject firstRelationJo = pathJa.getJSONObject(0);
        BITableRelation re = BITableRelationHelper.getRelation(firstRelationJo);
        return re.getPrimaryTable();
    }

    private BITableRelationPath createPath(JSONArray pathJa) throws Exception {
        BITableRelationPath newPath = new BITableRelationPath();
        for (int i = 0; i < pathJa.length(); i++) {
            BITableRelation re = BITableRelationHelper.getRelation(pathJa.getJSONObject(i));
            newPath.addRelationAtTail(re);
        }
        return newPath;
    }

}

/**
 *
 */
package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.exception.BITableUnreachableException;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author Guy
 */
public class BIGetMultiPathAction extends AbstractBIConfigureAction {

    /* (non-Javadoc)
     * @see com.fr.web.core.AcceptCMD#getCMD()
     */
    @Override
    public String getCMD() {
        return "get_multi_path";
    }

    /**
     * ����
     *
     * @param req ����
     * @param res ����
     * @throws Exception
     */
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject multiPathJo = getMultiPath(userId);
        JSONObject jo = new JSONObject();
        JSONObject translations = BICubeConfigureCenter.getAliasManager().getTransManager(userId).createJSON();
        jo.put("cubeEnd", BIConfigureManagerCenter.getLogManager().getConfigVersion(userId));
        jo.put("translations", translations);
        jo.put("relations", multiPathJo.optJSONArray("relations"));
        jo.put("disabledRelations", multiPathJo.optJSONArray("disabledRelations"));
        jo.put("availableRelations", multiPathJo.optJSONArray("availableRelations"));
        jo.put("noneRelations", multiPathJo.optJSONArray("noneRelations"));
        WebUtils.printAsJSON(res, jo);
    }


    private Set<BIBusinessTable> getRelatedTables(long userId) {
        Set<BIBusinessTable> relatedTables = new HashSet<BIBusinessTable>();
        Iterator<Map.Entry<BusinessTable, IRelationContainer>> primaryIt = BICubeConfigureCenter.getTableRelationManager().getAllTable2PrimaryRelation(userId).entrySet().iterator();
        while (primaryIt.hasNext()) {
            Map.Entry entry = primaryIt.next();
            relatedTables.add((BIBusinessTable) entry.getKey());
        }

        Iterator<Map.Entry<BusinessTable, IRelationContainer>> foreignIt = BICubeConfigureCenter.getTableRelationManager().getAllTable2ForeignRelation(userId).entrySet().iterator();
        while (foreignIt.hasNext()) {
            Map.Entry entry = foreignIt.next();
            relatedTables.add((BIBusinessTable) entry.getKey());
        }
        return relatedTables;
    }

    private Set<BITableRelationPath> getAllPath(long userId, BusinessTable foreignTable, BusinessTable primaryTable) throws BITableUnreachableException, BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, foreignTable, primaryTable);
    }

    private Set<BITableRelationPath> getAllAvailablePath(long userId, BusinessTable foreignTable, BusinessTable primaryTable) throws BITableUnreachableException,
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return BICubeConfigureCenter.getTableRelationManager().getAllAvailablePath(userId, foreignTable, primaryTable);
    }


    private Set<BITableRelationPath> getDisabledPath(long userId, BusinessTable foreignTable, BusinessTable primaryTable) throws BITableUnreachableException, BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return BICubeConfigureCenter.getTableRelationManager().getAllUnavailablePath(userId, foreignTable, primaryTable);
    }

    private JSONObject getMultiPath(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray multiJa = new JSONArray();
        Set<BITableRelationPath> availableMultiSet = new HashSet<BITableRelationPath>();
        Set<BITableRelationPath> disabledMultiSet = new HashSet<BITableRelationPath>();
        Set<BITableRelationPath> noneMultiSet = new HashSet<BITableRelationPath>();
        Set<BIBusinessTable> relatedTables = getRelatedTables(userId);
        Set<BITableRelationPath> allDisabledPaths = getAllDisabledPath(userId);
        Iterator it = relatedTables.iterator();


        while (it.hasNext()) {
            BusinessTable foreignTable = (BusinessTable) it.next();
            Iterator primaryTableIt = relatedTables.iterator();
            while (primaryTableIt.hasNext()) {
                BusinessTable primaryTable = (BusinessTable) primaryTableIt.next();
                Set<BITableRelationPath> allPath = getAllPath(userId, foreignTable, primaryTable);
                Set<BITableRelationPath> multiPathItem = new HashSet<BITableRelationPath>();
                if (allPath.size() > 1) {
                    Set<BITableRelationPath> displayPath = new HashSet<BITableRelationPath>();
                    for (BITableRelationPath path : allPath) {
                        if (checkHasDisablePath(allDisabledPaths, path)) {
                            displayPath.add(path);
                        }
                    }
                    multiPathItem.addAll(displayPath);
                    Set<BITableRelationPath> allAvailablePath = getAllAvailablePath(userId, foreignTable, primaryTable);
                    availableMultiSet.addAll(allAvailablePath);
                }
                Set<BITableRelationPath> disablePathSet = getDisabledPath(userId, foreignTable, primaryTable);
                Iterator noneIt = disablePathSet.iterator();
                while (noneIt.hasNext()) {
                    BITableRelationPath nonePath = (BITableRelationPath) noneIt.next();
                    if (!allPath.contains(nonePath)) {
                        noneMultiSet.add(nonePath);
                    }
                }
                multiPathItem.addAll(disablePathSet);
                disabledMultiSet.addAll(disablePathSet);
                if (!multiPathItem.isEmpty()) {
                    multiJa.put(path2relations(multiPathItem));
                }

            }
        }
        jo.put("relations", multiJa);
        jo.put("disabledRelations", path2relations(disabledMultiSet));
        jo.put("availableRelations", path2relations(availableMultiSet));
        jo.put("noneRelations", path2relations(noneMultiSet));
        return jo;
    }

    private boolean checkHasDisablePath(Set<BITableRelationPath> disabledPathSet, BITableRelationPath checkPath) {
        List<BITableRelation> checkPathRelations = checkPath.getAllRelations();
        Iterator<BITableRelationPath> it = disabledPathSet.iterator();
        while (it.hasNext()) {
            BITableRelationPath disabledPath = it.next();
            if (checkPathRelations.containsAll(disabledPath.getAllRelations()) && checkPath != disabledPath) {
                return false;
            }
        }
        return true;
    }


    private JSONArray path2relations(Set<BITableRelationPath> multiPathSet) throws Exception {
        JSONArray multiPathJa = new JSONArray();
        Iterator it = multiPathSet.iterator();
        while (it.hasNext()) {
            JSONArray multiRelationJa = new JSONArray();
            BITableRelationPath path = (BITableRelationPath) it.next();
            List relationList = path.getAllRelations();
            for (int i = 0; i < relationList.size(); i++) {
                BITableRelation relation = ((BITableRelation) relationList.get(i));
                multiRelationJa.put(relation.createJSON());
            }
            multiPathJa.put(multiRelationJa);
        }
        return multiPathJa;
    }

    private Set<BITableRelationPath> getAllDisabledPath(long userId) throws BITableUnreachableException, BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        Set<BITableRelationPath> allDisabledPathSet = new HashSet<BITableRelationPath>();
        Set<BIBusinessTable> allTables = this.getRelatedTables(userId);
        Iterator<BIBusinessTable> primaryIt = allTables.iterator();
        while (primaryIt.hasNext()) {
            BusinessTable primaryTable = primaryIt.next();
            Iterator<BIBusinessTable> foreignIt = allTables.iterator();
            while (foreignIt.hasNext()) {
                BusinessTable foreignTable = foreignIt.next();
                allDisabledPathSet.addAll(getDisabledPath(userId, foreignTable, primaryTable));
            }
        }
        return allDisabledPathSet;
    }


}

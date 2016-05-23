package com.finebi.cube.conf.relation;

import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.BIConfigureManagerCenter;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.finebi.cube.conf.relation.path.BITableContainer;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.IBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.exception.*;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Connery on 2016/1/12.
 */
public class BISystemTableRelationManager extends BISystemDataManager<BIUserTableRelationManager> implements BITableRelationConfigurationProvider {
    private static String OBJ_TAG = "UserTableRelationManager";


    @Override
    public BIUserTableRelationManager constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(BIUserTableRelationManager.class, userId);
    }

    @Override
    public String managerTag() {
        return OBJ_TAG;
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }

    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }

    @Override
    public Set<BITableRelation> getAllTableRelation(long userId) {
        return getUserGroupConfigManager(userId).getAllTableRelation();

    }

    @Override
    public void envChanged() {

    }

    @Override
    public boolean isPathDisable(long userId, BITableRelationPath disablePath) {
        BINonValueUtils.checkNull(disablePath);
        return getUserGroupConfigManager(userId).isPathDisable(disablePath);
    }

    @Override
    public void addDisableRelations(long userId, BITableRelationPath disablePath) throws BITablePathDuplicationException {
        BINonValueUtils.checkNull(disablePath);
        getUserGroupConfigManager(userId).addDisableRelations(disablePath);
    }

    @Override
    public void removeDisableRelations(long userId, BITableRelationPath disablePath) throws BITablePathAbsentException {
        BINonValueUtils.checkNull(disablePath);
        getUserGroupConfigManager(userId).removeDisableRelations(disablePath);
    }

    @Override
    public boolean containTableRelation(long userId, BITableRelation tableRelation) {
        BINonValueUtils.checkNull(tableRelation);
        return getUserGroupConfigManager(userId).containTableRelation(tableRelation);
    }

    @Override
    public boolean containTableRelationship(long userId, BITableRelation tableRelation) {
        return getUserGroupConfigManager(userId).containTableRelation(tableRelation);
    }

    @Override
    public boolean containTablePrimaryRelation(long userId, IBusinessTable table) {
        return getUserGroupConfigManager(userId).containTablePrimaryRelation(table);
    }

    @Override
    public boolean containTableForeignRelation(long userId, IBusinessTable table) {
        return getUserGroupConfigManager(userId).containTableForeignRelation(table);
    }


    @Override
    public void registerTableRelation(long userId, BITableRelation tableRelation) throws BIRelationDuplicateException {
        BINonValueUtils.checkNull(tableRelation);
        getUserGroupConfigManager(userId).registerTableRelation(tableRelation);
    }

    @Override
    public void registerTableRelationSet(long userId, Set<BITableRelation> tableRelations) {
        BIUserTableRelationManager manager = getUserGroupConfigManager(userId);
        Iterator<BITableRelation> it = tableRelations.iterator();
        while (it.hasNext()) {
            try {
                manager.registerTableRelation(it.next());
            } catch (BIRelationDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
    }

    @Override
    public void removeTableRelation(long userId, BITableRelation tableRelation) throws BIRelationAbsentException, BITableAbsentException {
        getUserGroupConfigManager(userId).removeTableRelation(tableRelation);
    }

    @Override
    public boolean isChanged(long userId) {
        return getUserGroupConfigManager(userId).isChanged();
    }

    @Override
    public void finishGenerateCubes(long userId, Set<BITableRelation> relationSet) {
        getUserGroupConfigManager(userId).finishGenerateCubes(relationSet);
    }

    @Override
    public Map<IBusinessTable, IRelationContainer> getAllTable2PrimaryRelation(long userId) {
        return getUserGroupConfigManager(userId).getAllTable2PrimaryRelation();
    }

    @Override
    public Map<IBusinessTable, IRelationContainer> getAllTable2ForeignRelation(long userId) {
        return getUserGroupConfigManager(userId).getAllTable2ForeignRelation();

    }

    @Override
    public JSONObject createBasicRelationsJSON(long userId) {
        return null;
    }

    @Override
    public void clear(long user) {
        getUserGroupConfigManager(user).clear();
    }

    @Override
    public Set<BITableRelationPath> getAllPath(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable) throws
            BITableUnreachableException, BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return getUserGroupConfigManager(userId).getAllPath(juniorTable, primaryTable);
    }

    @Override
    public Set<BITableRelationPath> getAllAvailablePath(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable) throws BITableUnreachableException,
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return getUserGroupConfigManager(userId).getAllAvailablePath(juniorTable, primaryTable);
    }

    @Override
    public Set<BITableRelationPath> getAllTablePath(long userId) throws BITableRelationConfusionException, BITablePathConfusionException {
        Set<IBusinessTable> allTables = getAllTables(userId);
        Iterator<IBusinessTable> superTableIt = allTables.iterator();
        Set<BITableRelationPath> resultPaths = new HashSet<BITableRelationPath>();
        while (superTableIt.hasNext()) {
            IBusinessTable superTable = superTableIt.next();
            Iterator<IBusinessTable> juniorTableIt = allTables.iterator();
            while (juniorTableIt.hasNext()) {
                IBusinessTable juniorTable = juniorTableIt.next();
                if (!ComparatorUtils.equals(superTable, juniorTable)) {
                    try {
                        resultPaths.addAll(getAllPath(userId, juniorTable, superTable));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
        return resultPaths;
    }

    protected Set<IBusinessTable> getAllTables(long userId) {
        return BIConfigureManagerCenter.getPackageManager().getAllTables(userId);
    }

    @Override
    public BITableRelationPath getFirstPath(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable) throws BITableUnreachableException {
        return null;
    }

    @Override
    public BITableRelationPath getFirstAvailablePath(long userId, IBusinessTable primaryTable, IBusinessTable juniorTable) throws BITableUnreachableException {
        return null;
    }

    @Override
    public BITableContainer getCommonSonTables(long userId, Set<IBusinessTable> tables) {
        return null;
    }

    @Override
    public JSONObject createRelationsPathJSON(long userId) throws JSONException {
        JSONObject jo = new JSONObject();
        Set<IBusinessTable> primaryTables = new HashSet<IBusinessTable>();
        Set<IBusinessTable> foreignTables = new HashSet<IBusinessTable>();
        for (BITableRelation relation : getAllTableRelation(userId)) {
            primaryTables.add(relation.getPrimaryTable());
            foreignTables.add(relation.getForeignTable());
        }
        for (IBusinessTable p : primaryTables) {
            JSONObject relation = new JSONObject();
            jo.put(p.getID().getIdentity(), relation);
            for (IBusinessTable f : foreignTables) {
                try {
                    Set<BITableRelationPath> path = getAllAvailablePath(userId, f, p);
                    if (path != null || !path.isEmpty()) {
                        relation.put(f.getID().getIdentity(), createPathJSON(path));
                    }
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
        }
        return jo;
    }

    private JSONArray createPathJSON(Set<BITableRelationPath> pathSet) throws JSONException {
        JSONArray ja = new JSONArray();
        for (BITableRelationPath p : pathSet) {
            JSONArray path = new JSONArray();
            ja.put(path);
            for (BITableRelation r : p.getAllRelations()) {
                JSONObject jo = new JSONObject();
                JSONObject primaryJo = new JSONObject();
                JSONObject foreignJo = new JSONObject();
                primaryJo.put("field_id", r.getPrimaryField().getTableBelongTo().getID().getIdentityValue() + r.getPrimaryField().getFieldName());
                foreignJo.put("field_id", r.getForeignField().getTableBelongTo().getID().getIdentityValue() + r.getForeignField().getFieldName());
                jo.put("primaryKey", primaryJo);
                jo.put("foreignKey", foreignJo);
                path.put(jo);
            }
        }
        return ja;
    }

    @Override
    public boolean isReachable(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable) {
        return false;
    }

    @Override
    public IRelationContainer getPrimaryRelation(long userId, IBusinessTable table) throws BITableAbsentException {
        return getUserGroupConfigManager(userId).getPrimaryRelation(table);
    }

    @Override
    public IRelationContainer getForeignRelation(long userId, IBusinessTable table) throws BITableAbsentException {
        return getUserGroupConfigManager(userId).getForeignRelation(table);
    }


}
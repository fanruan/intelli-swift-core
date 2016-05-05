package com.fr.bi.conf.base.relation;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.base.relation.path.BITableContainer;
import com.fr.bi.conf.base.relation.path.BITableRelationshipManager;
import com.fr.bi.conf.base.relation.path.BITableRelationshipService;
import com.fr.bi.conf.base.relation.relation.IRelationContainer;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.*;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Connery on 2016/1/12.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIUserTableRelationManager.class)
public class BIUserTableRelationManager implements Release {

    /**
     *
     */
    private static final long serialVersionUID = -4599513067350765988L;
    private static final String XML_TAG = "ConnectionManager";
    protected BITableRelationAnalysisService oldAnalyserHandler;
    protected BITableRelationAnalysisService currentAnalyserHandler;
    protected BIDisablePathsManager disablePathsManager;
    protected BITableRelationshipService tableRelationshipService;
    protected BIUser biUser;

    public BITableRelationshipService getTableRelationshipService() {
        return tableRelationshipService;
    }

    protected BIUserTableRelationManager(long userId) {
        biUser = new BIUser(userId);
        oldAnalyserHandler = BIFactoryHelper.getObject(BITableRelationAnalysisService.class);
        currentAnalyserHandler = BIFactoryHelper.getObject(BITableRelationAnalysisService.class);
        disablePathsManager = new BIDisablePathsManager();
        tableRelationshipService = new BITableRelationshipManager(currentAnalyserHandler);
    }

    public Set<BITableRelation> getAllTableRelation() {
        return currentAnalyserHandler.getRelationContainer().getContainer();
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void releaseResource() {
        oldAnalyserHandler.clear();
        currentAnalyserHandler.clear();
        tableRelationshipService.clear();
    }

    public Boolean isChanged() {
        return currentAnalyserHandler.isChanged(oldAnalyserHandler.getRelationContainer());
    }


    public BITableContainer getCommonSeniorTables(BITablePair tablePair) throws BITableAbsentException {
        return tableRelationshipService.getCommonSeniorTables(tablePair);
    }


    public boolean isPathDisable(BITableRelationPath disablePath) {
        return disablePathsManager.isPathDisable(disablePath);
    }


    public void addDisableRelations(BITableRelationPath disablePath) throws BITablePathDuplicationException {
        disablePathsManager.addDisabledPath(disablePath);
    }



    public void removeDisableRelations(BITableRelationPath disablePath) throws BITablePathAbsentException {
        disablePathsManager.removeDisablePath(disablePath);
    }


    public boolean containTableRelation(BITableRelation tableRelation) {
        return currentAnalyserHandler.contain(tableRelation);
    }

    public boolean containTableRelationInTableRelationShip(BITableRelation tableRelation) {
        return tableRelationshipService.contain(tableRelation);
    }

    public boolean containTableForeignRelation(BITable table) {
        return currentAnalyserHandler.containTableForeignRelation(table);
    }

    public boolean containTablePrimaryRelation(BITable table) {
        return currentAnalyserHandler.containTablePrimaryRelation(table);
    }


    public void registerTableRelation(BITableRelation tableRelation) throws BIRelationDuplicateException {
        currentAnalyserHandler.addRelation(tableRelation);
        tableRelationshipService.addBITableRelation(tableRelation);
    }


    public void removeTableRelation(BITableRelation tableRelation) throws BIRelationAbsentException, BITableAbsentException {
        currentAnalyserHandler.removeRelation(tableRelation);
        tableRelationshipService.removeTableRelation(tableRelation);
    }


    public boolean isChanged(long userId) {
        return false;
    }


    public void finishGenerateCubes(Set<BITableRelation> connectionSet) {
        synchronized (oldAnalyserHandler) {
            oldAnalyserHandler.clear();
            for (BITableRelation relation : connectionSet) {
                try {
                    oldAnalyserHandler.addRelation(relation);
                } catch (BIRelationDuplicateException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
        }
    }


    public JSONObject createBasicRelationsJSON() {
        return null;
    }


    public void clear(long user) {

    }


    public Set<BITableRelationPath> getAllPath(Table juniorTable, Table primaryTable)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return tableRelationshipService.getAllPath(new BITablePair(primaryTable, juniorTable));
    }


    public Set<BITableRelationPath> getAllAvailablePath(Table juniorTable, Table primaryTable) throws
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        Set<BITableRelationPath> set = new HashSet<BITableRelationPath>();
        for (BITableRelationPath path : getAllPath(juniorTable, primaryTable)) {
            if (!isPathDisable(path)) {
                set.add(path);
            }
        }
        return set;
    }


    public BITableRelationPath getFirstPath(Table juniorTable, Table primaryTable) throws BITableUnreachableException {
        return null;
    }


    public BITableRelationPath getFirstAvailablePath(Table primaryTable, Table juniorTable) throws BITableUnreachableException {
        return null;
    }


    public JSONObject createRelationsPathJSON() {
        return null;
    }

    public boolean isReachable(Table juniorTable, Table primaryTable) {
        return false;
    }

    public Map<Table, IRelationContainer> getAllTable2PrimaryRelation() {
        return currentAnalyserHandler.getAllTable2PrimaryRelation();
    }

    public Map<Table, IRelationContainer> getAllTable2ForeignRelation() {
        return currentAnalyserHandler.getAllTable2ForeignRelation();
    }

    public IRelationContainer getPrimaryRelation(Table table) throws BITableAbsentException {
        return currentAnalyserHandler.getPrimaryRelation(table);
    }

    IRelationContainer getForeignRelation(Table table) throws BITableAbsentException {
        return currentAnalyserHandler.getForeignRelation(table);
    }

}
package com.finebi.cube.conf.relation;

import com.finebi.cube.conf.relation.path.BITableContainer;
import com.finebi.cube.conf.relation.path.BITableRelationshipManager;
import com.finebi.cube.conf.relation.path.BITableRelationshipService;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITablePair;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.exception.*;
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
    
    protected BITableRelationAnalysisService oldAnalyserHandler;
    protected BITableRelationAnalysisService currentAnalyserHandler;
    protected BIDisablePathsManager disablePathsManager;
    protected BITableRelationshipService tableRelationshipService;
    protected BITableRelationshipService analysisTableRelationShipService;
    protected Boolean analysisStatus = false;
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
        analysisTableRelationShipService = new BITableRelationshipManager(currentAnalyserHandler);
    }


    public Set<BITableRelation> getAllTableRelation() {
        return currentAnalyserHandler.getRelationContainer().getContainer();
    }

    public Set<BITableRelation> getAllOldTableRelation() {
        return oldAnalyserHandler.getRelationContainer().getContainer();
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void clear() {
        // TODO: 16/7/5 分析关联只显示可用关联需求需要保留之前生成过cube的关联
//        oldAnalyserHandler.clear();
        currentAnalyserHandler.clear();
        tableRelationshipService.clear();
    }

    public Boolean isChanged() {
        return currentAnalyserHandler.isChanged(oldAnalyserHandler.getRelationContainer());
    }


    public BITableContainer getCommonSeniorTables(BITablePair tablePair) throws BITableAbsentException {
        return tableRelationshipService.getCommonSeniorTables(tablePair);
    }

    public BITableContainer getAnalysisCommonSeniorTables(BITablePair tablePair) throws BITableAbsentException {
        return analysisTableRelationShipService.getCommonSeniorTables(tablePair);
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

    public boolean containTableForeignRelation(BusinessTable table) {
        return currentAnalyserHandler.containTableForeignRelation(table);
    }

    public boolean containTablePrimaryRelation(BusinessTable table) {
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
            analysisTableRelationShipService.clear();
            for (BITableRelation relation : currentAnalyserHandler.getRelationContainer().getContainer()) {
                try {
                    oldAnalyserHandler.addRelation(relation);
                } catch (BIRelationDuplicateException e) {
                    BILogger.getLogger().error(e.getMessage());
                }
            }
//            for (BITableRelation relation : connectionSet) {
//                try {
//                    oldAnalyserHandler.addRelation(relation);
//                } catch (BIRelationDuplicateException e) {
//                    BILogger.getLogger().error(e.getMessage());
//                }
//            }
            analysisTableRelationShipService = new BITableRelationshipManager(oldAnalyserHandler);
            for (BITableRelation relation : connectionSet) {
                analysisTableRelationShipService.addBITableRelation(relation);
            }

        }
    }


    public JSONObject createBasicRelationsJSON() {
        return null;
    }


    public void clear(long user) {

    }


    public Set<BITableRelationPath> getAllPath(BusinessTable juniorTable, BusinessTable primaryTable)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return tableRelationshipService.getAllPath(new BITablePair(primaryTable, juniorTable));
    }

    public Set<BITableRelationPath> getAnalysisAllPath(BusinessTable juniorTable, BusinessTable primaryTable)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return analysisTableRelationShipService.getAllPath(new BITablePair(primaryTable, juniorTable));
    }

    public Set<BITableRelationPath> getAnalysisAllAvailablePath(BusinessTable juniorTable, BusinessTable primaryTable) throws
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        Set<BITableRelationPath> set = new HashSet<BITableRelationPath>();
        for (BITableRelationPath path : getAnalysisAllPath(juniorTable, primaryTable)) {
            if (!isPathDisable(path)) {
                set.add(path);
            }
        }
        return set;
    }


    public Set<BITableRelationPath> getAllAvailablePath(BusinessTable juniorTable, BusinessTable primaryTable) throws
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        Set<BITableRelationPath> set = new HashSet<BITableRelationPath>();
        for (BITableRelationPath path : getAllPath(juniorTable, primaryTable)) {
            if (!isPathDisable(path)) {
                set.add(path);
            }
        }
        return set;
    }

    public Set<BITableRelationPath> getAllUnavailablePath(BusinessTable juniorTable, BusinessTable primaryTable) throws
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        Set<BITableRelationPath> set = new HashSet<BITableRelationPath>();
        for (BITableRelationPath path : getAllPath(juniorTable, primaryTable)) {
            if (isPathDisable(path)) {
                set.add(path);
            }
        }
        return set;
    }

    public Set<BITableRelationPath> getAnalysisAllUnavailablePath(BusinessTable juniorTable, BusinessTable primaryTable) throws
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        Set<BITableRelationPath> set = new HashSet<BITableRelationPath>();
        for (BITableRelationPath path : getAnalysisAllPath(juniorTable, primaryTable)) {
            if (isPathDisable(path)) {
                set.add(path);
            }
        }
        return set;
    }

    public BITableRelationPath getFirstPath(BusinessTable juniorTable, BusinessTable primaryTable) throws BITableUnreachableException {
        return null;
    }


    public BITableRelationPath getFirstAvailablePath(BusinessTable primaryTable, BusinessTable juniorTable) throws BITableUnreachableException {
        return null;
    }


    public JSONObject createRelationsPathJSON() {
        return null;
    }

    public boolean isReachable(BusinessTable juniorTable, BusinessTable primaryTable) {
        return false;
    }

    public Map<BusinessTable, IRelationContainer> getAllTable2PrimaryRelation() {
        return currentAnalyserHandler.getAllTable2PrimaryRelation();
    }

    public Map<BusinessTable, IRelationContainer> getAllTable2ForeignRelation() {
        return currentAnalyserHandler.getAllTable2ForeignRelation();
    }

    public IRelationContainer getPrimaryRelation(BusinessTable table) throws BITableAbsentException {
        return currentAnalyserHandler.getPrimaryRelation(table);
    }

    IRelationContainer getForeignRelation(BusinessTable table) throws BITableAbsentException {
        return currentAnalyserHandler.getForeignRelation(table);
    }

    public boolean isRelationGenerated(BITableRelation tableRelation) throws BIRelationAbsentException, BITableAbsentException {
        return oldAnalyserHandler.contain(tableRelation);
    }
}

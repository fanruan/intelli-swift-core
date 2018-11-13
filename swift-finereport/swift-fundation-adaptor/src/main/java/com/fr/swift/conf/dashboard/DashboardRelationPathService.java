package com.fr.swift.conf.dashboard;

import com.finebi.base.constant.BaseConstant;
import com.finebi.base.constant.FineEngineType;
import com.finebi.common.base.config.utils.FieldUtils;
import com.finebi.common.internalimp.config.driver.CommonDataSourceDriverFactory;
import com.finebi.common.internalimp.config.relation.utils.RelationUtils;
import com.finebi.common.internalimp.config.relation.visitor.AllPathVisitor;
import com.finebi.common.service.engine.relation.DirectRelationPathManagerUtils;
import com.finebi.common.structure.config.driver.CommonDataSourceDriver;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.fieldinfo.FieldInfo;
import com.finebi.common.structure.config.relation.PathInfo;
import com.finebi.common.structure.config.relation.Relation;
import com.finebi.common.structure.config.relation.RelationConfiguration;
import com.finebi.common.structure.config.relation.RelationPath;
import com.finebi.common.structure.config.relation.visitor.RelationFieldId;
import com.finebi.conf.internalimp.path.FineBusinessTableRelationPathImp;
import com.finebi.conf.internalimp.relation.FineBusinessTableRelationIml;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.engine.constant.Null;
import com.fr.engine.exception.RuntimeEngineException;
import com.fr.engine.logger.DirectLoggerFactory;
import com.fr.engine.utils.CollectionUtils;
import com.fr.swift.conf.dashboard.store.DashboardConfManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardRelationPathService {

    private static DashboardRelationPathService service;

    private DashboardRelationPathService() {
    }

    public static DashboardRelationPathService getService() {
        if (null == service) {
            synchronized (DashboardRelationPathService.class) {
                service = new DashboardRelationPathService();
            }
        }
        return service;
    }

    public List<FineBusinessTableRelation> getAllAnalysisRelations() {
        List<Relation> relations = DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig().getRelations();
        return transRelationToBIRelation(relations);
    }

    public List<FineBusinessTableRelation> getAnalysisRelationsByTables(String s, String s1) {
        EntryInfo from = DashboardConfManager.getManager().getEntryInfoSession().findByName(s1);
        EntryInfo to = DashboardConfManager.getManager().getEntryInfoSession().findByName(s);
        if (null == from || null == to) {
            return new ArrayList<FineBusinessTableRelation>();
        }
        List<Relation> relations = RelationUtils.getDirectRelationsBetweenTwoTables(from.getID(), to.getID(), DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig().getRelations());
        return transRelationToBIRelation(relations);
    }

    public List<FineBusinessTableRelation> getAnalysisRelationsByTable(String s) {
        EntryInfo from = DashboardConfManager.getManager().getEntryInfoSession().findByName(s);
        if (null == from) {
            return new ArrayList<FineBusinessTableRelation>();
        }
        List<Relation> relations = RelationUtils.getDirectRelationsByTableId(from.getID(), DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig().getRelations());
        return transRelationToBIRelation(relations);
    }

    public boolean isAnalysisRelationExist(String s, String s1) {
        return !getAnalysisRelationsByTables(s, s1).isEmpty();
    }

    public boolean isAnalysisRelationExist(String s) {
        return !getAnalysisRelationsByTable(s).isEmpty();
    }

    public List<FineBusinessTableRelationPath> getAllAnalysisRelationPaths() {
        Map<PathInfo, List<RelationPath>> pathMap = DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig().getMultipath();
        Map<PathInfo, List<RelationPath>> unusedPathMap = DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig().getUnusedMultipath();
        Map<PathInfo, List<RelationPath>> singlePathMap = getSinglePaths();
        List<RelationPath> paths = new ArrayList<RelationPath>();
        for (PathInfo pathInfo : pathMap.keySet()) {
            paths.addAll(pathMap.get(pathInfo));
        }
        for (PathInfo pathInfo : singlePathMap.keySet()) {
            paths.addAll(singlePathMap.get(pathInfo));
        }
        List<RelationPath> unusedPaths = new ArrayList<RelationPath>();
        for (PathInfo pathInfo : unusedPathMap.keySet()) {
            unusedPaths.addAll(unusedPathMap.get(pathInfo));
        }
        List<FineBusinessTableRelationPath> biPaths = transPathsToBIPaths(paths, false);
        biPaths.addAll(transPathsToBIPaths(unusedPaths, true));
        return biPaths;
    }

    public List<FineBusinessTableRelationPath> getRelationPathsByTables(String s, String s1) {
        EntryInfo from = DashboardConfManager.getManager().getEntryInfoSession().findByName(s1);
        EntryInfo to = DashboardConfManager.getManager().getEntryInfoSession().findByName(s);
        if (null == from || null == to) {
            return new ArrayList<FineBusinessTableRelationPath>();
        }
        String primaryTableId = from.getID();
        String attachTableId = to.getID();
        List<RelationPath> paths = DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig().getRelationsBetweenTwoTables(primaryTableId, attachTableId, new AllPathVisitor());
        if (paths == null || paths.isEmpty()) {
            return new ArrayList<FineBusinessTableRelationPath>();
        }
        PathInfo pathInfo = new PathInfo(primaryTableId, attachTableId);
        List<RelationPath> usedPaths = DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig().getMultipath().get(pathInfo);
        if (usedPaths != null) {
            paths.removeAll(usedPaths);
            List<FineBusinessTableRelationPath> unUsedBiPaths = transPathsToBIPaths(paths, true);
            List<FineBusinessTableRelationPath> biPaths = transPathsToBIPaths(usedPaths, false);
            biPaths.addAll(unUsedBiPaths);
            return biPaths;
        } else {
            List<FineBusinessTableRelationPath> unUsedBiPaths = transPathsToBIPaths(paths, true);
            return unUsedBiPaths;
        }
    }

    public List<FineBusinessTableRelationPath> getRelationPaths(String s) {
        List<FineBusinessTableRelationPath> paths = new ArrayList<FineBusinessTableRelationPath>();
        List<FineBusinessTable> tables = DashboardPackageTableService.getService().getAllBusinessTable();
        for (FineBusinessTable table : tables) {
            paths.addAll(getRelationPathsByTables(s, table.getName()));
        }
        return paths;
    }

    public boolean isRelationPathExist(String s, String s1) {
        return !getRelationPathsByTables(s, s1).isEmpty();
    }

    public boolean isRelationPathExist(String s) {
        return !getRelationPaths(s).isEmpty();
    }

    protected List<FineBusinessTableRelation> transRelationToBIRelation(List<Relation> relations) {
        List<FineBusinessTableRelation> birelations = new ArrayList<FineBusinessTableRelation>();
        List<Relation> relationsMerged = DirectRelationPathManagerUtils.mergeOne2OneRelation(relations);
        for (Relation relation : relationsMerged) {
            birelations.add(getBIRelationByRelation(relation));
        }
        return birelations;
    }

    FineBusinessTableRelation getBIRelationByRelation(Relation relation) {
        List<FineBusinessField> primaryBIFields = getBIFields(relation.getPrimaryTableId(), relation.getPrimaryFieldId());
        List<FineBusinessField> attachedBIFields = getBIFields(relation.getAttachedTableId(), relation.getAttachedFieldId());
        FineBusinessTable primaryBITable = getBITable(relation.getPrimaryTableId());
        FineBusinessTable attachedBIField = getBITable(relation.getAttachedTableId());
        int relationBIType = getBIRelationType(relation);
        return new FineBusinessTableRelationIml(primaryBIFields, attachedBIFields, primaryBITable, attachedBIField, relationBIType);
    }

    private List<FineBusinessField> getBIFields(String tableId, RelationFieldId relationFieldId) {
        List<FineBusinessField> biFields = new ArrayList<FineBusinessField>();
        List<String> fields = relationFieldId.getIds();
        for (String id : fields) {
            FineBusinessField biField = getBIField(tableId, id);
            if (Null.isNull(biField)) {
                String errmsg = "can not find field: " + id;
                DirectLoggerFactory.getLogger().error(errmsg);
                throw new RuntimeEngineException("errmsg");
            }
            biFields.add(biField);
        }
        return biFields;
    }

    private FineBusinessField getBIField(String tableId, String fieldID) {
        EntryInfo entryInfo = DashboardConfManager.getManager().getEntryInfoSession().find(tableId);
        FieldInfo fieldinfo = DashboardConfManager.getManager().getFieldSession().find(tableId);
        if (null == fieldID) {
            //在上层处理
            return null;
        }
        return FieldUtils.translateField(entryInfo, fieldinfo, fieldID, FineEngineType.Cube);
    }

    private FineBusinessTable getBITable(String tableID) {
        EntryInfo info = DashboardConfManager.getManager().getEntryInfoSession().find(tableID);
        if (null != info) {
            CommonDataSourceDriver driver = CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube).getDriver(info.getType());
            return driver.createBusinessTable(info);
        }
        return null;
    }

    private int getBIRelationType(Relation relation) {
        if (relation.isOneToOne()) {
            return BaseConstant.RELATION_TYPE.ONE_TO_ONE;
        } else {
            return BaseConstant.RELATION_TYPE.MANY_TO_ONE;
        }
    }

    protected Map<PathInfo, List<RelationPath>> getSinglePaths() {
        Map<PathInfo, List<RelationPath>> result = new HashMap<PathInfo, List<RelationPath>>();
        RelationConfiguration configuration = DashboardConfManager.getManager().getRelationConfigurationSession().getCheckedConfig();
        List<String> tables = configuration.getAllTables();
        if (!CollectionUtils.isEmpty(tables)) {
            for (int i = 0; i < tables.size(); i++) {
                for (int j = 0; j < tables.size(); j++) {
                    if (tables.get(i).equals(tables.get(j))) {
                        continue;
                    }
                    List<RelationPath> paths = configuration.getRelationsBetweenTwoTables(tables.get(i), tables.get(j), new AllPathVisitor());
                    PathInfo pathInfo = new PathInfo(tables.get(i), tables.get(j));
                    if (paths != null && paths.size() == 1) {
                        result.put(pathInfo, paths);
                    }
                }
            }
        }
        return result;
    }

    private List<FineBusinessTableRelationPath> transPathsToBIPaths(List<RelationPath> relationPaths, boolean isUnused) {
        List<FineBusinessTableRelationPath> biPaths = new ArrayList<FineBusinessTableRelationPath>();
        for (RelationPath path : relationPaths) {
            biPaths.add(getBIRelationPathByRelationPath(path, isUnused));
        }
        return biPaths;
    }

    public FineBusinessTableRelationPath getBIRelationPathByRelationPath(RelationPath relationPath, boolean isUnused) {
        List<FineBusinessTableRelation> biRelations = new ArrayList<FineBusinessTableRelation>();
        for (Relation relation : relationPath.getPath()) {
            biRelations.add(getBIRelationByRelation(relation));
        }
        FineBusinessTableRelationPath biPath = new FineBusinessTableRelationPathImp(biRelations);
        biPath.setPathDisable(isUnused);
        return biPath;
    }
}

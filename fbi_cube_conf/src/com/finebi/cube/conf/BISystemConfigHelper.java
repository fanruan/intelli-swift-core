package com.finebi.cube.conf;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;

import java.util.*;

/**
 * This class created on 2016/11/8.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BISystemConfigHelper {
    private Set<BIBusinessTable> businessTables = new HashSet<BIBusinessTable>();
    private Map<CubeTableSource, Map<String, ICubeFieldSource>> tableDBFieldMaps = new HashMap<CubeTableSource, Map<String, ICubeFieldSource>>();
    private Set<CubeTableSource> tableSources = new HashSet<CubeTableSource>();
    private Set<BITableRelation> tableRelations = new HashSet<BITableRelation>();
    protected Set<BITableRelationPath> tablePaths = new HashSet<BITableRelationPath>();

    public BISystemConfigHelper() {
        prepare(UserControl.getInstance().getSuperManagerID());
    }

    public BISystemConfigHelper(long userID) {
        prepare(userID);
    }

    public void prepare(long userID) {
        prepareBusinessTables(userID);
        prepareTableSources();
        prepareTableDBFieldMaps();
        prepareTableRelation(userID);
        prepareTablePath(userID);
    }

    public Set<BITableRelation> getSystemTableRelations() {
        return tableRelations;
    }

    public Set<BITableRelationPath> getSystemTablePaths() {
        return tablePaths;
    }

    public Set<BIBusinessTable> getSystemBusinessTables() {
        return businessTables;
    }

    private void prepareBusinessTables(long userID) {
        businessTables.clear();
        businessTables.addAll(extractBusinessTable(userID));
    }

    private void prepareTableSources() {
        tableSources.clear();
        tableSources.addAll(extractTableSource(businessTables));
    }

    private void prepareTableDBFieldMaps() {
        tableDBFieldMaps.clear();
        tableDBFieldMaps.putAll(extractFieldSource(tableSources));
    }

    private void prepareTableRelation(long userID) {
        tableRelations.clear();
        tableRelations.addAll(BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userID));
    }

    private void prepareTablePath(long userID) {
        tablePaths.clear();
        try {
            tablePaths.addAll(BICubeConfigureCenter.getTableRelationManager().getAllTablePath(userID));
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public static boolean isRelationValid(BITableRelation relation) {
        boolean checkNull = null != relation.getPrimaryTable() && null != relation.getForeignTable() && null != relation.getPrimaryField() && null != relation.getForeignField();
        boolean isStructureCorrect = relation.getForeignField().getTableBelongTo().getID().getIdentity().equals(relation.getForeignTable().getID().getIdentity()) && relation.getPrimaryField().getTableBelongTo().getID().getIdentity().equals(relation.getPrimaryTable().getID().getIdentity());
        boolean isTypeCorrect = relation.getForeignField().getFieldType() == relation.getPrimaryField().getFieldType();
        return checkNull && isStructureCorrect && isTypeCorrect;
    }

    private Set<BIBusinessTable> extractBusinessTable(long userID) {
        Set<BIBusinessTable> systemBusinessTables = new HashSet<BIBusinessTable>();
        for (BusinessTable table : BICubeConfigureCenter.getPackageManager().getAllTables(userID)) {
            systemBusinessTables.add((BIBusinessTable) table);
        }
        return systemBusinessTables;
    }

    public Set<CubeTableSource> extractTableSource(Set<BIBusinessTable> systemBusinessTables) {
        Set<CubeTableSource> allTableSources = new HashSet<CubeTableSource>();
        for (Object biBusinessTable : systemBusinessTables) {
            BusinessTable table = (BusinessTable) biBusinessTable;
            allTableSources.add(table.getTableSource());
        }
        return allTableSources;
    }

    private Map<CubeTableSource, Map<String, ICubeFieldSource>> extractFieldSource(Set<CubeTableSource> tableSources) {
        Map<CubeTableSource, Map<String, ICubeFieldSource>> tableDBFieldMaps = new HashMap<CubeTableSource, Map<String, ICubeFieldSource>>();
        Iterator<CubeTableSource> iterator = tableSources.iterator();
        while (iterator.hasNext()) {
            CubeTableSource tableSource = iterator.next();
            Set<ICubeFieldSource> BICubeFieldSources = tableSource.getFacetFields(tableSources);
            Map<String, ICubeFieldSource> name2Field = new HashMap<String, ICubeFieldSource>();
            Iterator<ICubeFieldSource> it = BICubeFieldSources.iterator();
            while (it.hasNext()) {
                ICubeFieldSource field = it.next();
                name2Field.put(field.getFieldName(), field);
            }
            tableDBFieldMaps.put(tableSource, name2Field);
        }
        return tableDBFieldMaps;
    }

    public BITableSourceRelation convertRelation(BITableRelation relation) {
        if (!isTableRelationValid(relation)) {
            BILoggerFactory.getLogger().error("tableRelation invalid:" + relation.toString());
            return null;
        }
        BITableSourceRelation biTableSourceRelation;
        try {
            CubeTableSource primaryTable;
            CubeTableSource foreignTable;
            primaryTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getPrimaryField().getTableBelongTo());
            foreignTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getForeignField().getTableBelongTo());
            ICubeFieldSource primaryField = tableDBFieldMaps.get(primaryTable).get(relation.getPrimaryField().getFieldName());
            ICubeFieldSource foreignField = tableDBFieldMaps.get(foreignTable).get(relation.getForeignField().getFieldName());
            if (primaryField == null) {
                throw BINonValueUtils.beyondControl("The field:" + relation.getPrimaryField().getFieldName() +
                        " is absent in table:" + primaryTable.getTableName());
            }
            if (foreignField == null) {
                throw BINonValueUtils.beyondControl("The field:" + relation.getForeignField().getFieldName() +
                        " is absent in table:" + foreignTable.getTableName());
            }

            biTableSourceRelation = new BITableSourceRelation(
                    primaryField,
                    foreignField,
                    primaryTable,
                    foreignTable
            );
            primaryField.setTableBelongTo(primaryTable);
            foreignField.setTableBelongTo(foreignTable);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            return null;
        }
        return biTableSourceRelation;
    }

    public boolean isTableRelationValid(BITableRelation relation) {
        boolean relationValid = isRelationValid(relation);
        boolean isStructureValid = businessTables.contains(relation.getPrimaryTable()) && businessTables.contains(relation.getForeignTable());
        return isStructureValid && relationValid;
    }

    public BITableSourceRelationPath convertPath(BITableRelationPath path) {
        try {
            BITableSourceRelationPath tableSourceRelationPath = new BITableSourceRelationPath();
            for (BITableRelation biTableRelation : path.getAllRelations()) {
                BITableSourceRelation biTableSourceRelation = convertRelation(biTableRelation);
                if (null == biTableSourceRelation) {
                    return null;
                }
                tableSourceRelationPath.addRelationAtTail(biTableSourceRelation);
            }
            return tableSourceRelationPath;
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}

package com.finebi.cube.structure.table;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BICubeTableAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * This class created on 2016/5/9.
 *
 * @author Connery
 * @since 4.0
 */
public class CompoundCubeTableReader implements CubeTableEntityService {
    private BICubeTableEntity hostTable;
    /**
     * 上次Table对象
     */
    private CubeTableEntityService parentTable;
    protected Map<ICubeFieldSource, CubeTableEntityService> fieldSource = new HashMap<ICubeFieldSource, CubeTableEntityService>();
    private List<ICubeFieldSource> compoundFields = new ArrayList<ICubeFieldSource>();

    public CompoundCubeTableReader(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        hostTable = new BICubeTableEntity(tableKey, resourceRetrievalService, discovery);
        if (hostTable.getParentsTable() != null && !hostTable.getParentsTable().isEmpty()) {
            parentTable = new CompoundCubeTableReaderNode(hostTable.getParentsTable(), resourceRetrievalService, discovery);
            parentTable.setTableOwner(tableKey);
        }
        initialFields();
    }

    public CubeTableEntityService getParentTable() {
        return parentTable;
    }

    private void initialFields() {
        if (hostTable.tableDataAvailable()) {
            for (ICubeFieldSource field : hostTable.getFieldInfo()) {
                if (!compoundFields.contains(field)) {
                    compoundFields.add(field);
                    fieldSource.put(field, hostTable);
                }
            }
        } else {
            throw new BICubeTableAbsentException("Please generate Cube firstly");
        }
        if (isParentAvailable()) {
            for (ICubeFieldSource field : parentTable.getFieldInfo()) {
                if (!compoundFields.contains(field) && isInFacedFields(field)) {
                    compoundFields.add(field);
                    fieldSource.put(field, parentTable);

                }
            }
        }
    }

    private boolean isInFacedFields(ICubeFieldSource field) {
        return getFieldNamesFromParent().contains(field.getFieldName());
    }

    private boolean isParentAvailable() {
        return parentTable != null;
    }

    @Override
    public void recordTableStructure(List<ICubeFieldSource> fields) {
        throw new UnsupportedOperationException();

    }


    @Override
    public void recordRowCount(long rowCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recordLastTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recordRemovedLine(TreeSet<Integer> removedLine) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void addDataValue(BIDataValue originalDataValue) throws BICubeColumnAbsentException {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean checkRelationVersion(List<BITableSourceRelation> relations, int relation_version) {
        return hostTable.checkRelationVersion(relations, relation_version);
    }

    @Override
    public boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations, int relation_version) {
        return hostTable.checkRelationVersion(key, relations, relation_version);
    }

    @Override
    public boolean checkCubeVersion() {
        return hostTable.checkCubeVersion();
    }

    @Override
    public void copyDetailValue(CubeTableEntityService cube, long rowCount) {
        throw new UnsupportedOperationException();

    }


    @Override
    public List<ICubeFieldSource> getFieldInfo() {
        return compoundFields;
    }

    @Override
    public Set<BIColumnKey> getCubeColumnInfo() {
        Set<BIColumnKey> result = new HashSet<BIColumnKey>();
        for (BIColumnKey columnKey : hostTable.getCubeColumnInfo()) {
            if (!result.contains(columnKey)) {
                result.add(columnKey);
            }
        }
        if (isParentAvailable()) {
            for (BIColumnKey columnKey : parentTable.getCubeColumnInfo()) {
                if (!result.contains(columnKey)) {
                    result.add(columnKey);
                }
            }
        }
        return result;
    }

    @Override
    public int getRowCount() {
        if (hostTable.isRowCountAvailable()) {
            return hostTable.getRowCount();
        } else {
            return parentTable.getRowCount();
        }
    }

    @Override
    public TreeSet<Integer> getRemovedList() {
        if (hostTable.isRemovedListAvailable()) {
            return hostTable.getRemovedList();
        } else {
            return parentTable.getRemovedList();
        }

    }

    @Override
    public ICubeFieldSource getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        for (ICubeFieldSource field : compoundFields) {
            if (ComparatorUtils.equals(field.getFieldName(), fieldName)) {
                return field;
            }
        }
        throw new BICubeColumnAbsentException();
    }

    @Override
    public Date getCubeLastTime() {
        return hostTable.getCubeLastTime();
    }

    private CubeTableEntityService pickTableService(String fieldName) throws BICubeColumnAbsentException {
        ICubeFieldSource field = getSpecificColumn(fieldName);
        return fieldSource.get(field);
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return pickTableService(columnKey.getColumnName()).getColumnDataGetter(columnKey);
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        return pickTableService(columnName).getColumnDataGetter(columnName);
    }

    @Override
    public CubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {

        return hostTable.getRelationIndexGetter(path);

    }

    @Override
    public boolean tableDataAvailable() {
        return hostTable.tableDataAvailable() || (isParentAvailable() && parentTable.tableDataAvailable());
    }

    @Override
    public void clear() {
        hostTable.clear();
        if (isParentAvailable()) {
            parentTable.clear();
        }
    }

    @Override
    public void recordParentsTable(List<ITableKey> parents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ITableKey> getParentsTable() {
        return hostTable.getParentsTable();
    }

    @Override
    public boolean isRowCountAvailable() {
        return hostTable.isRowCountAvailable() || (isParentAvailable() && parentTable.isRowCountAvailable());
    }

    @Override
    public void recordFieldNamesFromParent(Set<String> fieldNames) {
        hostTable.recordFieldNamesFromParent(fieldNames);
    }

    @Override
    public Set<String> getFieldNamesFromParent() {
        return hostTable.getFieldNamesFromParent();
    }

    public long getCubeVersion() {
        return hostTable.getCubeVersion();
    }

    @Override
    public void addVersion(long version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTableOwner(ITableKey owner) {
        hostTable.setTableOwner(owner);
        if (parentTable != null) {
            parentTable.setTableOwner(owner);
        }
    }

    @Override
    public boolean isRemovedListAvailable() {
        return hostTable.isRemovedListAvailable() || (isParentAvailable() && parentTable.isRemovedListAvailable());

    }

    @Override
    public Boolean isVersionAvailable() {
        return hostTable.isVersionAvailable();
    }
}

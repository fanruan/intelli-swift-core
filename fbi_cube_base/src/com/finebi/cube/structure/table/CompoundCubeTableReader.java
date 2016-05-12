package com.finebi.cube.structure.table;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ICubeRelationEntityGetterService;
import com.finebi.cube.structure.ICubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * This class created on 2016/5/9.
 *
 * @author Connery
 * @since 4.0
 */
public class CompoundCubeTableReader implements ICubeTableEntityService {
    private BICubeTableEntity hostTable;
    /**
     * 上次Table对象
     */
    private ICubeTableEntityService parentTable;
    protected Map<DBField, ICubeTableEntityService> fieldSource = new HashMap<DBField, ICubeTableEntityService>();
    private List<DBField> compoundFields = new ArrayList<DBField>();

    public CompoundCubeTableReader(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        hostTable = new BICubeTableEntity(tableKey, resourceRetrievalService, discovery);
        if (hostTable.getParentsTable() != null && !hostTable.getParentsTable().isEmpty()) {
            parentTable = new CompoundCubeTableReaderNode(hostTable.getParentsTable(), resourceRetrievalService, discovery);
        }
        initialFields();
    }

    public ICubeTableEntityService getParentTable() {
        return parentTable;
    }

    private void initialFields() {
        if (hostTable.tableDataAvailable()) {
            for (DBField field : hostTable.getFieldInfo()) {
                if (!compoundFields.contains(field)) {
                    compoundFields.add(field);
                    fieldSource.put(field, hostTable);
                }
            }
        } else {
            throw BINonValueUtils.beyondControl("Please generate Cube firstly");
        }
        if (isParentAvailable()) {
            for (DBField field : parentTable.getFieldInfo()) {
                if (!compoundFields.contains(field)) {
                    compoundFields.add(field);
                    fieldSource.put(field, parentTable);

                }
            }
        }
    }

    private boolean isParentAvailable() {
        return parentTable != null;
    }

    @Override
    public void recordTableStructure(List<DBField> fields) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void recordTableGenerateVersion(int version) {
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
    public void copyDetailValue(ICubeTableEntityService cube, long rowCount) {
        throw new UnsupportedOperationException();

    }

    @Override
    public int getTableVersion() {
        return hostTable.getTableVersion();
    }

    @Override
    public List<DBField> getFieldInfo() {
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
    public DBField getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        for (DBField field : compoundFields) {
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

    private ICubeTableEntityService pickTableService(String fieldName) throws BICubeColumnAbsentException {
        DBField field = getSpecificColumn(fieldName);
        return fieldSource.get(field);
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return pickTableService(columnKey.getColumnName()).getColumnDataGetter(columnKey);
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        return pickTableService(columnName).getColumnDataGetter(columnName);
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        try {
            return pickTableService(path.getPrimaryField().getColumnName()).getRelationIndexGetter(path);
        } catch (BITablePathEmptyException e) {
            throw BINonValueUtils.beyondControl(e);
        }

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
}

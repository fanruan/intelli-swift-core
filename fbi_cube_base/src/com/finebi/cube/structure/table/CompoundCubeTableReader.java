package com.finebi.cube.structure.table;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.relation.BITableSourceRelation;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
    private Set<ICubeTableEntityService> parents;

    public CompoundCubeTableReader(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
//        super(tableKey, resourceRetrievalService, discovery);
    }

    @Override
    public void recordTableStructure(List<DBField> fields) {
        hostTable.recordTableStructure(fields);
    }

    @Override
    public void recordTableGenerateVersion(int version) {
        hostTable.recordTableGenerateVersion(version);
    }

    @Override
    public void recordRowCount(long rowCount) {
        hostTable.recordRowCount(rowCount);
    }

    @Override
    public void recordLastTime() {
        hostTable.recordLastTime();
    }

    @Override
    public void recordRemovedLine(TreeSet<Integer> removedLine) {
        hostTable.recordRemovedLine(removedLine);
    }

    @Override
    public void addDataValue(BIDataValue originalDataValue) throws BICubeColumnAbsentException {
        hostTable.addDataValue(originalDataValue);
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
        hostTable.copyDetailValue(cube, rowCount);
    }

    @Override
    public int getTableVersion() {
        return hostTable.getTableVersion();
    }

    @Override
    public List<DBField> getFieldInfo() {
        return null;
    }

    @Override
    public Set<BIColumnKey> getCubeColumnInfo() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public DBField getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        return null;
    }

    @Override
    public Date getCubeLastTime() {
        return null;
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return null;
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        return null;
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return null;
    }

    @Override
    public boolean tableDataAvailable() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void recordParentsTable(List<ITableKey> parents) {

    }
}

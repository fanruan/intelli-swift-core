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
import com.fr.bi.stable.relation.BITableSourceRelation;

import java.util.*;

/**
 * 组合表的一个层级
 * <p/>
 * 一个层级理论上可以有多个表。这个是按照第一个表来取。
 * 这么做的因为有些点还是很明确，例如获得字段信息，是同一层的组合？还是有优先级？
 * <p/>
 * 当前是全部从第一个表来取数据
 * This class created on 2016/5/10.
 *
 * @author Connery
 * @since 4.0
 */
public class CompoundCubeTableReaderNode implements ICubeTableEntityService {

    private Set<ICubeTableEntityService> currentLevelTables = new HashSet<ICubeTableEntityService>();
    private ICubeTableEntityService masterTable = null;
    protected ICubeResourceDiscovery discovery;
    protected ICubeResourceRetrievalService resourceRetrievalService;

    public CompoundCubeTableReaderNode(Set<ITableKey> tableKeys, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        this.resourceRetrievalService = resourceRetrievalService;
        this.discovery = discovery;
        for (ITableKey tableKey : tableKeys) {
            ICubeTableEntityService tableEntityService = new CompoundCubeTableReader(tableKey, resourceRetrievalService, discovery);
            if (masterTable == null) {
                masterTable = tableEntityService;
            }
            currentLevelTables.add(tableEntityService);
        }
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations, int relation_version) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean checkCubeVersion() {
        throw new UnsupportedOperationException();

    }

    @Override
    public void recordParentsTable(List<ITableKey> parents) {

    }

    @Override
    public void copyDetailValue(ICubeTableEntityService cube, long rowCount) {
        throw new UnsupportedOperationException();

    }

    @Override
    public int getTableVersion() {
        throw new UnsupportedOperationException();

    }

    @Override
    public List<DBField> getFieldInfo() {
        return masterTable.getFieldInfo();
    }

    @Override
    public Set<BIColumnKey> getCubeColumnInfo() {
        return masterTable.getCubeColumnInfo();
    }

    @Override
    public int getRowCount() {
        return masterTable.getRowCount();
    }

    @Override
    public DBField getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        return masterTable.getSpecificColumn(fieldName);
    }

    @Override
    public Date getCubeLastTime() {
        return masterTable.getCubeLastTime();
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return masterTable.getColumnDataGetter(columnKey);
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        return masterTable.getColumnDataGetter(columnName);
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return masterTable.getRelationIndexGetter(path);
    }

    @Override
    public boolean tableDataAvailable() {
        return masterTable.tableDataAvailable();
    }

    @Override
    public void clear() {
        for (ICubeTableEntityService table : currentLevelTables) {
            table.clear();
        }
    }
}

package com.finebi.cube.structure.table;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
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
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

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
public class CompoundCubeTableReaderNode implements CubeTableEntityService {

    private List<CubeTableEntityService> currentLevelTables = new ArrayList<CubeTableEntityService>();
    private CubeTableEntityService masterTable = null;
    protected ICubeResourceDiscovery discovery;
    protected ICubeResourceRetrievalService resourceRetrievalService;
    protected Map<ICubeFieldSource, CubeTableEntityService> fieldSource = new HashMap<ICubeFieldSource, CubeTableEntityService>();
    private List<ICubeFieldSource> currentLevelFields = new ArrayList<ICubeFieldSource>();

    public CompoundCubeTableReaderNode(List<ITableKey> tableKeys, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        this.resourceRetrievalService = resourceRetrievalService;
        this.discovery = discovery;
        for (ITableKey tableKey : tableKeys) {
            CubeTableEntityService tableEntityService = new CompoundCubeTableReader(tableKey, resourceRetrievalService, discovery);
            if (masterTable == null) {
                masterTable = tableEntityService;
            }
            initialFieldSource(tableEntityService);
            currentLevelTables.add(tableEntityService);
        }
    }

    private void initialFieldSource(CubeTableEntityService tableEntityService) {
        if (tableEntityService.tableDataAvailable()) {
            for (ICubeFieldSource field : tableEntityService.getFieldInfo()) {

                if (!fieldSource.containsKey(field)) {
                    fieldSource.put(field, tableEntityService);
                    currentLevelFields.add(field);
                }
            }
        }
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyDetailValue(CubeTableEntityService cube, long rowCount) {
        throw new UnsupportedOperationException();

    }


    @Override
    public List<ICubeFieldSource> getFieldInfo() {
        return currentLevelFields;
    }

    @Override
    public Set<BIColumnKey> getCubeColumnInfo() {
        Set<BIColumnKey> result = new HashSet<BIColumnKey>();
        for (CubeTableEntityService tableEntityService : currentLevelTables) {
            for (BIColumnKey columnKey : tableEntityService.getCubeColumnInfo()) {
                if (!result.contains(columnKey)) {
                    result.add(columnKey);
                }
            }
        }
        return result;
    }

    @Override
    public int getRowCount() {
        return masterTable.getRowCount();
    }

    @Override
    public IntList getRemovedList() {
        return masterTable.getRemovedList();
    }

    @Override
    public ICubeFieldSource getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        for (ICubeFieldSource field : currentLevelFields) {
            if (ComparatorUtils.equals(fieldName, field.getFieldName())) {
                return field;
            }
        }
        throw new BICubeColumnAbsentException();
    }

    @Override
    public Date getCubeLastTime() {
        return masterTable.getCubeLastTime();
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return pickTableService(columnKey.getColumnName()).getColumnDataGetter(columnKey);
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        return pickTableService(columnName).getColumnDataGetter(columnName);
    }

    private CubeTableEntityService pickTableService(String fieldName) throws BICubeColumnAbsentException {
        ICubeFieldSource field = getSpecificColumn(fieldName);
        return fieldSource.get(field);
    }

    @Override
    public CubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        try {
            BIColumnKey columnKey = path.getFirstRelation().getPrimaryField();
            return pickTableService(columnKey.getColumnName()).getRelationIndexGetter(path);
        } catch (BITablePathEmptyException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public boolean tableDataAvailable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        for (CubeTableEntityService table : currentLevelTables) {
            table.clear();
        }
    }

    @Override
    public List<ITableKey> getParentsTable() {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean isRowCountAvailable() {
        return masterTable.isRowCountAvailable();
    }

    @Override
    public void recordFieldNamesFromParent(Set<String> fieldNames) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getFieldNamesFromParent() {
        throw new UnsupportedOperationException();
    }

    public long getCubeVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addVersion(long version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTableOwner(ITableKey owner) {
        for (CubeTableEntityService tableEntityService : currentLevelTables) {
            tableEntityService.setTableOwner(owner);
        }
    }

    @Override
    public boolean isRemovedListAvailable() {
        return masterTable.isRemovedListAvailable();
    }

    @Override
    public Boolean isVersionAvailable() {
        return masterTable.isVersionAvailable();
    }
}

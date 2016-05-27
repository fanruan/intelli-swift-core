package com.finebi.cube.structure.table;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ICubeRelationEntityGetterService;
import com.finebi.cube.structure.ICubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
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
public class CompoundCubeTableReaderNode implements ICubeTableEntityService {

    private List<ICubeTableEntityService> currentLevelTables = new ArrayList<ICubeTableEntityService>();
    private ICubeTableEntityService masterTable = null;
    protected ICubeResourceDiscovery discovery;
    protected ICubeResourceRetrievalService resourceRetrievalService;
    protected Map<ICubeFieldSource, ICubeTableEntityService> fieldSource = new HashMap<ICubeFieldSource, ICubeTableEntityService>();
    private List<ICubeFieldSource> currentLevelFields = new ArrayList<ICubeFieldSource>();

    public CompoundCubeTableReaderNode(List<ITableKey> tableKeys, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        this.resourceRetrievalService = resourceRetrievalService;
        this.discovery = discovery;
        for (ITableKey tableKey : tableKeys) {
            ICubeTableEntityService tableEntityService = new CompoundCubeTableReader(tableKey, resourceRetrievalService, discovery);
            if (masterTable == null) {
                masterTable = tableEntityService;
            }
            initialFieldSource(tableEntityService);
            currentLevelTables.add(tableEntityService);
        }
    }

    private void initialFieldSource(ICubeTableEntityService tableEntityService) {
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
        throw new UnsupportedOperationException();
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
    public List<ICubeFieldSource> getFieldInfo() {
        return currentLevelFields;
    }

    @Override
    public Set<BIColumnKey> getCubeColumnInfo() {
        Set<BIColumnKey> result = new HashSet<BIColumnKey>();
        for (ICubeTableEntityService tableEntityService : currentLevelTables) {
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
    public ICubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return pickTableService(columnKey.getColumnName()).getColumnDataGetter(columnKey);
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        return pickTableService(columnName).getColumnDataGetter(columnName);
    }

    private ICubeTableEntityService pickTableService(String fieldName) throws BICubeColumnAbsentException {
        ICubeFieldSource field = getSpecificColumn(fieldName);
        return fieldSource.get(field);
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
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
        for (ICubeTableEntityService table : currentLevelTables) {
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
}

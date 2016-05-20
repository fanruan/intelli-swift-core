package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.calculator.bidouble.MaxCalculator;
import com.finebi.cube.calculator.bidouble.MinCalculator;
import com.finebi.cube.calculator.bidouble.SumCalculator;
import com.finebi.cube.calculator.biint.GroupSizeCalculator;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.gen.oper.BIFieldPathIndexBuilder;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.finebi.cube.utils.BICubePathUtils;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * This class created on 2016/4/15.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableAdapter implements ICubeTableService {
    private ICube cube;
    private ICubeTableEntityGetterService primaryTable;

    public BICubeTableAdapter(ICube cube, ICubeTableSource tableSource) {
        this.cube = cube;
            primaryTable = cube.getCubeTable(new BITableKey(tableSource.getSourceID()));
        Iterator<Set<ICubeTableSource>> it = tableSource.createGenerateTablesMap().values().iterator();
        while (it.hasNext()) {
            Iterator<ICubeTableSource> tableSourceIterator = it.next().iterator();
            while (tableSourceIterator.hasNext()) {
                initial(tableSourceIterator.next());
            }
        }
    }

    private void initial(ICubeTableSource tableSource) {
        ICubeTableEntityGetterService tableEntityGetterService = cube.getCubeTable(new BITableKey(tableSource.getSourceID()));
        Iterator<BIColumnKey> it = tableEntityGetterService.getCubeColumnInfo().iterator();
    }

    @Override
    public Object getRow(BIKey columnIndex, int row) {
        try {
            return getColumnReader(columnIndex).getOriginalValueByRow(row);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public Object getRowValue(BIKey columnIndex, int row) {
        return getRow(columnIndex, row);
    }

    @Override
    public Object[] getRow(BIKey columnIndex, int[] rows) {
        Object[] objects = new Object[rows.length];
        for (int i = 0; i < rows.length; i++) {
            objects[i] = getRow(columnIndex, rows[i]);
        }
        return objects;
    }

    @Override
    public double getMAXValue(GroupValueIndex gvi, BIKey summaryIndex) {
        return MaxCalculator.INSTANCE.calculate(this, summaryIndex, gvi);
    }

    @Override
    public double getMAXValue(BIKey summaryIndex) {
        return getMAXValue(getAllShowIndex(), summaryIndex);
    }

    @Override
    public double getMINValue(GroupValueIndex gvi, BIKey summaryIndex) {
        return MinCalculator.INSTANCE.calculate(this, summaryIndex, gvi);
    }

    @Override
    public double getMINValue(BIKey summaryIndex) {
        return getMINValue(getAllShowIndex(), summaryIndex);
    }

    @Override
    public double getSUMValue(GroupValueIndex gvi, BIKey summaryIndex) {
        return SumCalculator.INSTANCE.calculate(this, summaryIndex, gvi);
    }

    @Override
    public double getSUMValue(BIKey summaryIndex) {
        return getSUMValue(getAllShowIndex(), summaryIndex);

    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey key) {
        return loadGroup(key, null);
    }

    @Override
    public double getDistinctCountValue(GroupValueIndex gvi, BIKey distinct_field) {
        return GroupSizeCalculator.INSTANCE.calculate(this, distinct_field, gvi);
    }

    @Override
    public Map<BIKey, BICubeFieldSource> getColumns() {
        Map<BIKey, BICubeFieldSource> result = new HashMap<BIKey, BICubeFieldSource>();

        List<BICubeFieldSource> list = primaryTable.getFieldInfo();
        Iterator<BICubeFieldSource> tableFieldIt = list.iterator();
        while (tableFieldIt.hasNext()) {
            BICubeFieldSource field = tableFieldIt.next();
            result.put(getColumnIndex(field), field);
        }

        return result;
    }

    @Override
    public int getColumnSize() {
        return primaryTable.getFieldInfo().size();
    }

    @Override
    public BIKey getColumnIndex(String fieldName) {
        return new IndexKey(fieldName);
    }

    @Override
    public BIKey getColumnIndex(BIField field) {
        return getColumnIndex(field.getFieldName());
    }

    @Override
    public int getTableVersion(BIKey key) {
        return (int) primaryTable.getCubeLastTime().getTime();
    }

    @Override
    public GroupValueIndex[] getIndexes(BIKey columnIndex, Object[] values) {
        GroupValueIndex[] result = new GroupValueIndex[values.length];
        ICubeColumnReaderService columnReaderService;
        try {
            columnReaderService = getColumnReader(columnIndex);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
        for (int i = 0; i < values.length; i++) {
            try {
                result[i] = columnReaderService.getIndexByGroupValue(values[i]);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            }
        }
        return result;
    }

    @Override
    public int getRowCount() {
        return primaryTable.getRowCount();
    }

    @Override
    public Date getLastTime() {
        return primaryTable.getCubeLastTime();
    }

    @Override
    public IntList getRemovedList() {
        return new IntList();
    }

    @Override
    public GroupValueIndex getAllShowIndex() {
        return GVIFactory.createAllShowIndexGVI(getRowCount());
    }

    @Override
    public boolean isDistinct(String columnName) {
        BIKey columnIndex = getColumnIndex(columnName);
        return getDistinctCountValue(getAllShowIndex(), columnIndex) == getRowCount();
    }

    @Override
    public ICubeTableIndexReader ensureBasicIndex(List<BITableSourceRelation> relations) {
        try {
            if (relations.isEmpty()) {
                throw BINonValueUtils.beyondControl();
            }
            BITableSourceRelation startRelation = relations.get(0);
            BIKey key = getColumnIndex(startRelation.getPrimaryField());
            ICubeRelationEntityGetterService getterService = getTableReader(key).getRelationIndexGetter(BICubePathUtils.convert(relations));
            return new BICubeTableRelationIndexReader(getterService);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public int getLinkedRowCount(List<BITableSourceRelation> relations) {
        return 0;
    }

    @Override
    public GroupValueIndex getNullGroupValueIndex(BIKey key) {
        try {
            return getColumnReader(key).getNULLIndex(0);
        } catch (BICubeIndexException e) {
            //TODO BICubeIndexException含义不清晰
            return GVIFactory.createAllEmptyIndexGVI();
        }
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey columnIndex, List<BITableSourceRelation> relationList) {
        ICubeColumnReaderService columnReaderService = getColumnReader(columnIndex);
        if (relationList != null) {
            try {
                BICubeTablePath path = BICubePathUtils.convert(relationList);
                if (path.size() > 0 && !columnReaderService.existRelationPath(path)) {
                    BIFieldPathIndexBuilder indexBuilder = new BIFieldPathIndexBuilder(cube, getDBField(columnIndex), path);
                    indexBuilder.mainTask(null);
                }
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            }
        }
        return new BIColumnIndexReader(columnReaderService, relationList);
    }

    private ICubeColumnReaderService getColumnReader(BIKey biKey) {
        ICubeColumnReaderService columnReaderService;
        try {
            BIColumnKey columnKey;
            BICubeFieldSource field = getDBField(biKey);
            if (biKey instanceof IndexTypeKey) {
                columnKey = BIColumnKeyAdapter.covert(field, ((IndexTypeKey) biKey).getType());
            } else {
                columnKey = BIColumnKey.covertColumnKey(field);
            }
            columnReaderService = primaryTable.getColumnDataGetter(columnKey);

        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
        return columnReaderService;
    }

    private ICubeTableEntityGetterService getTableReader(BIKey biKey) {
        return primaryTable;
    }

    private BICubeFieldSource getDBField(BIKey biKey) throws BIKeyAbsentException {
        Map<BIKey, BICubeFieldSource> map = getColumns();
        Iterator<BIKey> it = map.keySet().iterator();
        while (it.hasNext()) {
            BIKey key = it.next();
            if (ComparatorUtils.equals(key.getKey(), biKey.getKey())) {
                return map.get(key);
            }
        }
        throw new BIKeyAbsentException();
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey key, List<BITableSourceRelation> relationList, boolean useRealData, int groupLimit) {
        return loadGroup(key, relationList);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public GroupValueIndex getIndexByRow(BIKey key, int row) {
        ICubeColumnReaderService columnReaderService = null;

        try {
            columnReaderService = getColumnReader(key);
            return columnReaderService.getIndexByRow(row);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }


    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isDataAvailable() {
        return primaryTable.tableDataAvailable();
    }
}

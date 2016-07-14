package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.calculator.bidouble.MaxCalculator;
import com.finebi.cube.calculator.bidouble.MinCalculator;
import com.finebi.cube.calculator.bidouble.SumCalculator;
import com.finebi.cube.calculator.biint.GroupSizeCalculator;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.gen.oper.BIFieldPathIndexBuilder;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.finebi.cube.utils.BICubePathUtils;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/4/15.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableAdapter implements ICubeTableService {
    private Cube cube;
    private CubeTableEntityGetterService primaryTable;
    private Map<BIKey, ICubeFieldSource> columnSet = null;
    private Map<BIKey, CubeColumnReaderService> columnReaderServiceMap = new ConcurrentHashMap<BIKey, CubeColumnReaderService>();
    private Map<BIKey, ICubeColumnDetailGetter> columnDetailReaderServiceMap = new ConcurrentHashMap<BIKey, ICubeColumnDetailGetter>();

    public BICubeTableAdapter(Cube cube, CubeTableSource tableSource) {
        this.cube = cube;
        primaryTable = cube.getCubeTable(new BITableKey(tableSource.getSourceID()));
        Iterator<Set<CubeTableSource>> it = tableSource.createGenerateTablesMap().values().iterator();
        while (it.hasNext()) {
            Iterator<CubeTableSource> tableSourceIterator = it.next().iterator();
            while (tableSourceIterator.hasNext()) {
                initial(tableSourceIterator.next());
            }
        }
    }

    private void initial(CubeTableSource tableSource) {
        CubeTableEntityGetterService tableEntityGetterService = cube.getCubeTable(new BITableKey(tableSource.getSourceID()));
        Iterator<BIColumnKey> it = tableEntityGetterService.getCubeColumnInfo().iterator();
    }

    @Override
    public ICubeColumnDetailGetter getColumnDetailReader(BIKey key) {
        if (columnDetailReaderServiceMap.containsKey(key)) {
            return columnDetailReaderServiceMap.get(key);
        } else {
            ICubeColumnDetailGetter columnDetailGetter = new BICubeColumnDetailGetter(getColumnReader(key));
            columnDetailReaderServiceMap.put(key, columnDetailGetter);
            return columnDetailGetter;
        }
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
    public Map<BIKey, ICubeFieldSource> getColumns() {
        if (!isColumnInitial()) {
            columnSet = new ConcurrentHashMap<BIKey, ICubeFieldSource>();
            List<ICubeFieldSource> list = primaryTable.getFieldInfo();
            Iterator<ICubeFieldSource> tableFieldIt = list.iterator();
            while (tableFieldIt.hasNext()) {
                ICubeFieldSource field = tableFieldIt.next();
                columnSet.put(getColumnIndex(field.getFieldName()), field);
            }
        }

        return columnSet;
    }

    private boolean isColumnInitial() {
        return columnSet != null;
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
    public BIKey getColumnIndex(BusinessField field) {
        return getColumnIndex(field.getFieldName());
    }

    @Override
    public long getTableVersion(BIKey key) {
        return primaryTable.getCubeVersion();
    }

    @Override
    public GroupValueIndex[] getIndexes(BIKey columnIndex, Object[] values) {
        GroupValueIndex[] result = new GroupValueIndex[values.length];
        CubeColumnReaderService columnReaderService;
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
            BIKey key = getColumnIndex(startRelation.getPrimaryField().getFieldName());
            CubeRelationEntityGetterService getterService = getTableReader(key).getRelationIndexGetter(BICubePathUtils.convert(relations));
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
        CubeColumnReaderService columnReaderService = getColumnReader(columnIndex);
        checkFieldPathIndex(columnIndex, relationList, columnReaderService);
        return new BIColumnIndexReader(columnReaderService, relationList);
    }

    private void checkFieldPathIndex(BIKey columnIndex, List<BITableSourceRelation> relationList, CubeColumnReaderService columnReaderService) {
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
    }

    private CubeColumnReaderService buildColumnReader(BIKey biKey) {
        CubeColumnReaderService columnReaderService;
        try {
            BIColumnKey columnKey;
            ICubeFieldSource field = getDBField(biKey);
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

    private CubeColumnReaderService getColumnReader(BIKey biKey) {
        if (columnReaderServiceMap.containsKey(biKey)) {
            return columnReaderServiceMap.get(biKey);
        } else {
            CubeColumnReaderService columnReaderService = buildColumnReader(biKey);
            columnReaderServiceMap.put(biKey, columnReaderService);
            return columnReaderService;
        }
    }

    private CubeTableEntityGetterService getTableReader(BIKey biKey) {
        return primaryTable;
    }

    private ICubeFieldSource getDBField(BIKey biKey) throws BIKeyAbsentException {
        Map<BIKey, ICubeFieldSource> map = getColumns();
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
        ICubeColumnIndexReader loadAll = loadGroup(key, relationList);
        if (!useRealData) {
            CubeTreeMap m = new CubeTreeMap(BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC);
            Iterator iter = loadAll.iterator();
            int i = 0;
            while (iter.hasNext() && i < groupLimit) {
                Map.Entry entry = (Map.Entry) iter.next();
                m.put(entry.getKey(), entry.getValue());
                i++;
            }
            return m;
        } else {
            return loadAll;
        }
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public ICubeValueEntryGetter getValueEntryGetter(BIKey key, List<BITableSourceRelation> relationList) {
        CubeColumnReaderService columnReaderService = getColumnReader(key);
        checkFieldPathIndex(key, relationList, columnReaderService);
        CubeRelationEntityGetterService getterService = null;
        if(relationList != null && relationList.size() > 0){
            BITableSourceRelation startRelation = relationList.get(0);
            BIKey keyRelation = getColumnIndex(startRelation.getPrimaryField().getFieldName());
            try {
                getterService = getTableReader(keyRelation).getRelationIndexGetter(BICubePathUtils.convert(relationList));
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            }
        }
        return new BICubeValueEntryGetter(columnReaderService, relationList, getterService);
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isDataAvailable() {
        return primaryTable.tableDataAvailable();
    }
}

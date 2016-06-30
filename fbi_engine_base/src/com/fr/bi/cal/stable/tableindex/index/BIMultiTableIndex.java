package com.fr.bi.cal.stable.tableindex.index;


import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.fr.bi.stable.structure.object.CubeValueEntry;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/18.
 */
public class BIMultiTableIndex implements ICubeTableService {

    private ICubeTableService[] cs;

    private Map<BIKey, ICubeTableService> childs = new ConcurrentHashMap<BIKey, ICubeTableService>();

    private Map<BIKey, ICubeFieldSource> columns = new ConcurrentHashMap<BIKey, ICubeFieldSource>();

    public BIMultiTableIndex(ICubeTableService[] childs) {
        cs = childs;
        init(childs);
    }

    private void init(ICubeTableService[] childs) {
        for (ICubeTableService ti : childs) {
            Map<BIKey, ICubeFieldSource> c = ti.getColumns();
            for (BIKey key : c.keySet()) {
                this.childs.put(key, ti);
            }
            columns.putAll(c);
        }
    }


    @Override
    public ICubeColumnDetailGetter getColumnDetailReader(BIKey key) {
        return childs.get(key).getColumnDetailReader(key);
    }


    /**
     * 求最大值
     *
     * @param gvi
     * @param summaryIndex
     * @return
     */
    @Override
    public double getMAXValue(GroupValueIndex gvi, BIKey summaryIndex) {
        return childs.get(summaryIndex).getMAXValue(gvi, summaryIndex);
    }

    @Override
    public double getMAXValue(BIKey summaryIndex) {
        return childs.get(summaryIndex).getMAXValue(summaryIndex);
    }

    /**
     * 求最小值
     *
     * @param gvi
     * @param summaryIndex
     * @return
     */
    @Override
    public double getMINValue(GroupValueIndex gvi, BIKey summaryIndex) {
        return childs.get(summaryIndex).getMINValue(gvi, summaryIndex);
    }

    @Override
    public double getMINValue(BIKey summaryIndex) {
        return childs.get(summaryIndex).getMINValue(summaryIndex);
    }

    /**
     * 求和运算
     *
     * @param gvi
     * @param summaryIndex
     * @return
     */
    @Override
    public double getSUMValue(GroupValueIndex gvi, BIKey summaryIndex) {
        return childs.get(summaryIndex).getSUMValue(gvi, summaryIndex);
    }

    @Override
    public double getSUMValue(BIKey summaryIndex) {
        return childs.get(summaryIndex).getSUMValue(summaryIndex);
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey key) {
        return childs.get(key).loadGroup(key);
    }

    /**
     * 求某个字段的去重，distinct_field != -1;
     *
     * @param gvi
     * @param distinct_field
     */
    @Override
    public double getDistinctCountValue(GroupValueIndex gvi, BIKey distinct_field) {
        return childs.get(distinct_field).getDistinctCountValue(gvi, distinct_field);
    }

    @Override
    public Map<BIKey, ICubeFieldSource> getColumns() {
        return columns;
    }

    @Override
    public int getColumnSize() {
        return columns.size();
    }

    @Override
    public BIKey getColumnIndex(String fieldName) {
        return new IndexKey(fieldName);
    }

    @Override
    public BIKey getColumnIndex(BusinessField field) {
        return new IndexKey(field.getFieldName());
    }

    @Override
    public long getTableVersion(BIKey key) {
        return childs.get(key).getTableVersion(key);
    }

    /**
     * 获取第columnindex列值分别为values的分组索引
     *
     * @param columnIndex
     * @param values
     * @return
     */
    @Override
    public GroupValueIndex[] getIndexes(BIKey columnIndex, Object[] values) {
        return childs.get(columnIndex).getIndexes(columnIndex, values);
    }

    @Override
    public int getRowCount() {
        return cs[0].getRowCount();
    }

    @Override
    public Date getLastTime() {
        throw new UnsupportedOperationException("MultiTableIndex can not support increase update");
    }

    @Override
    public IntList getRemovedList() {
        throw new UnsupportedOperationException("MultiTableIndex can not support increase update");
    }

    @Override
    public GroupValueIndex getAllShowIndex() {
        return cs[0].getAllShowIndex();
    }

    @Override
    public boolean isDistinct(String columnName) {
        return childs.get(new IndexKey(columnName)).isDistinct(columnName);
    }

    /**
     * 获取直接关联索引
     *
     * @param relations
     * @return
     */
    @Override
    public ICubeTableIndexReader ensureBasicIndex(List<BITableSourceRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return null;
        }
        return childs.get(new IndexKey(relations.get(0).getPrimaryKey().getFieldName())).ensureBasicIndex(relations);
    }

    @Override
    public int getLinkedRowCount(List<BITableSourceRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return -1;
        }
        return childs.get(new IndexKey(relations.get(0).getPrimaryKey().getFieldName())).getLinkedRowCount(relations);
    }


    @Override
    public GroupValueIndex getNullGroupValueIndex(BIKey key) {
        return childs.get(key).getNullGroupValueIndex(key);
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey columnIndex, List<BITableSourceRelation> relationList) {
        return childs.get(columnIndex).loadGroup(columnIndex, relationList);
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey key, List<BITableSourceRelation> relationList, boolean useRealData, int groupLimit) {
        return childs.get(key).loadGroup(key, relationList, useRealData, groupLimit);
    }

    @Override
    public String getId() {
        return cs[0].getId();
    }

    /**
     * 释放资源
     */
    @Override
    public void clear() {
        for (ICubeTableService ti : cs) {
            ti.clear();
        }
    }

    @Override
    public GroupValueIndex getIndexByRow(BIKey key, int row) {
        return childs.get(key).getIndexByRow(key, row);
    }

    @Override
    public CubeValueEntry getEntryByRow(BIKey key, int row) {
        return null;
    }

    @Override
    public boolean isDataAvailable() {
        return true;
    }
}
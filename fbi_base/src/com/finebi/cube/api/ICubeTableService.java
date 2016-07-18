package com.finebi.cube.api;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.engine.index.TableIndexAdapter;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by GUY on 2015/3/19.
 */
public interface ICubeTableService extends Release {

    ICubeTableService NULL_TABLE_INDEX = new TableIndexAdapter();

    ICubeColumnDetailGetter getColumnDetailReader(BIKey key);

    /**
     * 求最大值
     *
     * @param gvi
     * @param summaryIndex
     * @return
     */
    double getMAXValue(GroupValueIndex gvi, BIKey summaryIndex);

    double getMAXValue(BIKey summaryIndex);

    /**
     * 求最小值
     *
     * @param gvi
     * @param summaryIndex
     * @return
     */
    double getMINValue(GroupValueIndex gvi, BIKey summaryIndex);

    double getMINValue(BIKey summaryIndex);

    /**
     * 求和运算
     *
     * @param gvi
     * @param summaryIndex
     * @return
     */
    double getSUMValue(GroupValueIndex gvi, BIKey summaryIndex);

    double getSUMValue(BIKey summaryIndex);

    ICubeColumnIndexReader loadGroup(BIKey key);

    /**
     * 求某个字段的去重，distinct_field != -1;
     *
     * @param gvi
     * @param distinct_field
     */
    double getDistinctCountValue(GroupValueIndex gvi, BIKey distinct_field);

    Map<BIKey, ICubeFieldSource> getColumns();

    int getColumnSize();

    BIKey getColumnIndex(String fieldName);

    BIKey getColumnIndex(BusinessField field);

    long getTableVersion(BIKey key);

    /**
     * 获取第columnindex列值分别为values的分组索引
     *
     * @param columnIndex
     * @param values
     * @return
     */
    GroupValueIndex[] getIndexes(BIKey columnIndex, Object[] values);

    int getRowCount();

    Date getLastTime();

    IntList getRemovedList();

    GroupValueIndex getAllShowIndex();

    boolean isDistinct(String columnName);

    /**
     * 获取直接关联索引
     *
     * @param relations
     * @return
     */
    ICubeTableIndexReader ensureBasicIndex(List<BITableSourceRelation> relations);

    int getLinkedRowCount(List<BITableSourceRelation> relations);

    GroupValueIndex getNullGroupValueIndex(BIKey key);

    ICubeColumnIndexReader loadGroup(BIKey columnIndex, List<BITableSourceRelation> relationList);

    ICubeColumnIndexReader loadGroup(BIKey key, List<BITableSourceRelation> relationList, boolean useRealData, int groupLimit);

    String getId();

    /**
     * 根据key， relation获取ICubeValueEntryGetter
     * @param key
     * @param relationList
     * @return
     */
    ICubeValueEntryGetter getValueEntryGetter(BIKey key, List<BITableSourceRelation> relationList);

    boolean isDataAvailable();
}

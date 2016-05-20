/**
 *
 */
package com.fr.bi.stable.engine.index;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.GroupValueIndexArrayReader;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel
 */
public class TableIndexAdapter implements ICubeTableService {
    private final RuntimeException NULL_EXCEPTION = new NullTableIndexException();

    @Override
    public void clear() {
        throw NULL_EXCEPTION;
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey key, List<BITableSourceRelation> relationList, boolean useRealData, int groupLimit) {
        throw NULL_EXCEPTION;
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey columnIndex,
                                            List<BITableSourceRelation> relationList) {
        throw NULL_EXCEPTION;
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey key) {
        throw NULL_EXCEPTION;
    }

    @Override
    public boolean isDistinct(String columnName) {
        throw NULL_EXCEPTION;
    }

    @Override
    public int getTableVersion(BIKey key) {
        throw NULL_EXCEPTION;
    }

    @Override
    public double getSUMValue(BIKey summaryIndex) {
        throw NULL_EXCEPTION;
    }

    @Override
    public double getSUMValue(GroupValueIndex gvi, BIKey summaryIndex) {
        throw NULL_EXCEPTION;
    }

    @Override
    public Object getRowValue(BIKey columnIndex, int row) {
        throw NULL_EXCEPTION;
    }

    @Override
    public int getRowCount() {
        throw NULL_EXCEPTION;
    }

    @Override
    public Object[] getRow(BIKey columnIndex, int[] rows) {
        throw NULL_EXCEPTION;
    }

    @Override
    public Object getRow(BIKey columnIndex, int row) {
        throw NULL_EXCEPTION;
    }

    @Override
    public GroupValueIndex getNullGroupValueIndex(BIKey key) {
        throw NULL_EXCEPTION;
    }

    @Override
    public double getMINValue(BIKey summaryIndex) {
        throw NULL_EXCEPTION;
    }

    @Override
    public double getMINValue(GroupValueIndex gvi, BIKey summaryIndex) {
        throw NULL_EXCEPTION;
    }

    @Override
    public double getMAXValue(BIKey summaryIndex) {
        throw NULL_EXCEPTION;
    }

    @Override
    public double getMAXValue(GroupValueIndex gvi, BIKey summaryIndex) {
        throw NULL_EXCEPTION;
    }

    @Override
    public Date getLastTime() {
        throw NULL_EXCEPTION;
    }

    @Override
    public IntList getRemovedList() {
        throw NULL_EXCEPTION;
    }

    @Override
    public GroupValueIndex[] getIndexes(BIKey columnIndex, Object[] values) {
        throw NULL_EXCEPTION;
    }

    @Override
    public String getId() {
        throw NULL_EXCEPTION;
    }

    @Override
    public double getDistinctCountValue(GroupValueIndex gvi,
                                        BIKey distinct_field) {
        throw NULL_EXCEPTION;
    }

    @Override
    public Map<BIKey, BICubeFieldSource> getColumns() {
        throw NULL_EXCEPTION;
    }

    @Override
    public int getColumnSize() {
        throw NULL_EXCEPTION;
    }

    @Override
    public BIKey getColumnIndex(BIField field) {
        throw NULL_EXCEPTION;
    }

    @Override
    public BIKey getColumnIndex(String fieldName) {
        throw NULL_EXCEPTION;
    }

    @Override
    public GroupValueIndex getAllShowIndex() {
        throw NULL_EXCEPTION;
    }

    @Override
    public GroupValueIndexArrayReader ensureBasicIndex(
            List<BITableSourceRelation> relations) {
        throw NULL_EXCEPTION;
    }

    @Override
    public int getLinkedRowCount(List<BITableSourceRelation> relations) {
        throw NULL_EXCEPTION;
    }

    @Override
    public GroupValueIndex getIndexByRow(BIKey key, int row) {
        throw NULL_EXCEPTION;
    }

    @Override
    public boolean isDataAvailable() {
        throw NULL_EXCEPTION;

    }
}
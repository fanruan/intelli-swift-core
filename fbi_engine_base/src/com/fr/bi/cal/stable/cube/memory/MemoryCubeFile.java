package com.fr.bi.cal.stable.cube.memory;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.AbstractCubeFile;
import com.fr.bi.cal.stable.cube.ColumnFiles;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.*;

/**
 * Created by 小灰灰 on 2016/1/13.
 */
public class MemoryCubeFile extends AbstractCubeFile {
    private ICubeFieldSource[] BICubeFieldSources;
    private int rowCount;
    private static final String UNSUPPORT = "Memory Cube Not Support Link";

    public MemoryCubeFile(ICubeFieldSource[] BICubeFieldSources) {
        this.BICubeFieldSources = BICubeFieldSources;
        initColumns();
    }



    @Override
    public void mkDir() {

    }

    @Override
    public void writeMain(List<String> columnList) {

    }

    @Override
    public void writeVersionCheck() {

    }

    @Override
    public void writeTableGenerateVersion(int version) {

    }

    @Override
    public int getTableVersion() {
        return 0;
    }

    @Override
    public void writeRowCount(long rowCount) {
        this.rowCount = (int)rowCount;
    }

    @Override
    public void writeLastTime() {

    }

    @Override
    public void writeRemovedLine(TreeSet<Integer> removedLine) {

    }

    @Override
    public ICubeFieldSource[] getBIField() {
        return BICubeFieldSources;
    }

    @Override
    public void createDetailDataWriter() {

    }

    @Override
    public void releaseDetailDataWriter() {

    }

    @Override
    public boolean checkRelationVersion(List<BITableSourceRelation> relations, int relation_version) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations, int relation_version) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public IndexFile getLinkIndexFile(List<BITableSourceRelation> relations) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public boolean checkCubeVersion() {
        return true;
    }

    @Override
    public IndexFile getLinkIndexFile(BIKey key, List<BITableSourceRelation> relations) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public Date getCubeLastTime() {
        return new Date();
    }

    @Override
    public IntList getRemoveList(SingleUserNIOReadManager manager) {
        return new IntList();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public void copyDetailValue(BITableCubeFile cube, SingleUserNIOReadManager manager, long rowCount) {

    }


    @Override
    public GroupValueIndex getNullGroupValueIndex(BIKey key, SingleUserNIOReadManager manager) {
        return GVIFactory.createAllEmptyIndexGVI();
    }

    @Override
    public ICubeTableIndexReader getGroupValueIndexArrayReader(BIKey key, SingleUserNIOReadManager manager) {
        return getColumnFile(key).getGroupValueIndexArrayReader(manager);
    }

    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        return getColumnFile(key).createGroupByType(key, relationList, manager);
    }

    @Override
    public ICubeTableIndexReader getBasicGroupValueIndexArrayReader(List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }


    @Override
    public void delete() {

    }

    /**
     * 释放资源
     */
    @Override
    public void clear() {

    }

    @Override
    protected ColumnFiles initColumns() {
        if (columns != null) {
            return columns;
        }
        synchronized (this) {
            if (columns == null) {
                ICubeFieldSource[] fields = getBIField();
                ColumnFile<?>[] columns = new ColumnFile[fields.length];
                Map<String, Integer> colIndexMap = new HashMap<String, Integer>(fields.length);
                for (int i = 0, ilen = fields.length; i < ilen; i++) {
                    ICubeFieldSource field = fields[i];
                    colIndexMap.put(field.getFieldName(), i);
                    switch (field.getFieldType()) {
                        case DBConstant.COLUMN.DATE:
                            columns[i] = new MemoryDateColumn();
                            break;
                        case DBConstant.COLUMN.NUMBER:
                            switch (field.getClassType()) {
                                case DBConstant.CLASS.INTEGER:
                                case DBConstant.CLASS.LONG: {
                                    columns[i] = new MemoryLongColumn();
                                    break;
                                }
                                default: {
                                    columns[i] = new MemoryDoubleColumn();
                                    break;
                                }
                            }
                            break;
                        case DBConstant.COLUMN.STRING:
                            columns[i] = new MemoryStringColumn();
                            break;
                    }
                }
                this.columns = new ColumnFiles(columns, colIndexMap);
            }
            return columns;
        }
    }
}
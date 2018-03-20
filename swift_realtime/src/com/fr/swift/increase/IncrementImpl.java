package com.fr.swift.increase;

import com.fr.swift.increment.Increment;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.excel.ExcelDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-5 14:20:28
 *
 * @author Lucifer
 * @description 增量更新数据结构
 * @since Advanced FineBI Analysis 1.0
 */
public class IncrementImpl implements Increment {

    //增
    private QueryDBSource increaseSource;
    //减
    private QueryDBSource decreaseSource;
    //改
    private QueryDBSource modifySource;

    private ExcelDataSource excelDataSource;

    private SourceKey targetSourceKey;

    private List<String> incrementAppendFiles;
    private String firstName;

    private int updateType = UPDATE_TYPE.ALL;

    public IncrementImpl(String[] columnNames, ColumnTypeConstants.ColumnType[] columnTypes, List<String> appendedFileNames) throws Exception {
        try {
            incrementAppendFiles = new ArrayList<String>(appendedFileNames);
            firstName = incrementAppendFiles.remove(0);
            this.excelDataSource = new ExcelDataSource(firstName, columnNames, columnTypes, incrementAppendFiles);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public IncrementImpl(String increaseQuery, String decreaseQuery, String modifyQuery, SourceKey targetSourceKey, String connectionName) {
        this.increaseSource = createTempQueryDBSource(increaseQuery, connectionName);
        this.decreaseSource = createTempQueryDBSource(decreaseQuery, connectionName);
        this.modifySource = createTempQueryDBSource(modifyQuery, connectionName);
        this.targetSourceKey = targetSourceKey;
    }

    public IncrementImpl(String increaseQuery, String decreaseQuery, String modifyQuery, SourceKey targetSourceKey, String connectionName, int updateType) {
        this.increaseSource = createTempQueryDBSource(increaseQuery, connectionName);
        this.decreaseSource = createTempQueryDBSource(decreaseQuery, connectionName);
        this.modifySource = createTempQueryDBSource(modifyQuery, connectionName);
        this.targetSourceKey = targetSourceKey;
        this.updateType = updateType;
    }

    @Override
    public QueryDBSource getIncreaseSource() {
        return increaseSource;
    }

    @Override
    public QueryDBSource getDecreaseSource() {
        return decreaseSource;
    }

    @Override
    public QueryDBSource getModifySource() {
        return modifySource;
    }

    @Override
    public ExcelDataSource getIncreaseExcelSource() {
        return excelDataSource;
    }

    @Override
    public int getUpdateType() {
        return updateType;
    }

    private QueryDBSource createTempQueryDBSource(String query, String connectionName) {
        if (query == null) {
            return null;
        }
        QueryDBSource queryDBSource = new QueryDBSource(query, connectionName);
        return queryDBSource;
    }
}

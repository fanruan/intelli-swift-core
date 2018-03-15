package com.fr.swift.segment;

import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.IMetaDataColumn;
import com.fr.swift.config.unique.MetaDataColumnUnique;
import com.fr.swift.config.unique.SegmentUnique;
import com.fr.swift.config.unique.SwiftMetaDataUnique;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceAlloter;
import com.fr.swift.source.SwiftSourceAlloterFactory;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-10 11:00:26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractSegmentOperator implements SegmentOperator {

    protected SourceKey sourceKey;
    protected SwiftMetaData metaData;
    protected SwiftSourceAlloter alloter;
    protected List<SegmentHolder> segmentList;
    protected String cubeSourceKey;
    protected SwiftResultSet swiftResultSet;

    public AbstractSegmentOperator(SourceKey sourceKey, List<Segment> segments, String cubeSourceKey, SwiftResultSet swiftResultSet) {
        Util.requireNonNull(sourceKey);
        this.sourceKey = sourceKey;
        try {
            this.metaData = swiftResultSet.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.alloter = SwiftSourceAlloterFactory.createSourceAlloter(sourceKey);
        this.segmentList = new ArrayList<SegmentHolder>();
        this.cubeSourceKey = cubeSourceKey;
        this.swiftResultSet = swiftResultSet;
    }

    protected int indexOfColumn(String columnName) throws SwiftMetaDataException {
        for (int i = 1, len = metaData.getColumnCount(); i <= len; i++) {
            if (metaData.getColumnName(i).equals(columnName)) {
                return i - 1;
            }
        }
        return Crasher.crash("Can not find column named: " + columnName);
    }

    @Override
    public List<String> getIndexFields() throws SwiftMetaDataException {
        List<String> fields = new ArrayList<String>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            fields.add(metaData.getColumnName(i));
        }
        return fields;
    }

    @Override
    public void transport() throws Exception {

    }

    @Override
    public void finishTransport() {

    }

    @Override
    public int getSegmentCount() {
        return segmentList.size();
    }

    protected IMetaData convert2ConfigMetaData() throws SwiftMetaDataException {
        List<MetaDataColumnUnique> columns = new ArrayList<MetaDataColumnUnique>();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            SwiftMetaDataColumn column = metaData.getColumn(i);
            columns.add(new MetaDataColumnUnique(column.getType(), column.getName(), column.getRemark(), column.getPrecision(), column.getScale(), column.getColumnId()));
        }
        return new SwiftMetaDataUnique(metaData.getSchemaName(), metaData.getTableName(), metaData.getRemark(), columns);
    }
}

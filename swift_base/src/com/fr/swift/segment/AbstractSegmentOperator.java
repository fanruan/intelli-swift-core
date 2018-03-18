package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
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

    public AbstractSegmentOperator(SourceKey sourceKey, String cubeSourceKey, SwiftResultSet swiftResultSet) throws SQLException {
        Util.requireNonNull(sourceKey);
        this.sourceKey = sourceKey;
        this.metaData = swiftResultSet.getMetaData();
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
    public int getSegmentCount() {
        return segmentList.size();
    }
}

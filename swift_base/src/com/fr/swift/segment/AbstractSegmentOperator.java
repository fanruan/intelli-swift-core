package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftSourceAlloter;
import com.fr.swift.source.SwiftSourceAlloterFactory;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-10 11:00:26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractSegmentOperator implements ISegmentOperator {

    protected SourceKey sourceKey;
    protected SwiftMetaData metaData;
    protected SwiftSourceAlloter alloter;
    protected List<ISegmentHolder> segmentList;
    protected String cubeSourceKey;

    public AbstractSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData, List<Segment> segments, String cubeSourceKey) throws SwiftMetaDataException {
        Util.requireNonNull(sourceKey, metaData);
        this.sourceKey = sourceKey;
        this.metaData = metaData;
        this.alloter = SwiftSourceAlloterFactory.createSourceAlloter(sourceKey);
        this.segmentList = new ArrayList<ISegmentHolder>();
        this.cubeSourceKey = cubeSourceKey;
    }

    protected int indexOfColumn(String columnName) throws SwiftMetaDataException {
        for (int i = 1, len = metaData.getColumnCount(); i <= len; i++) {
            if (metaData.getColumnName(i).equals(columnName)) {
                return i - 1;
            }
        }
        return Crasher.crash("Can not find column named: " + columnName);
    }

}

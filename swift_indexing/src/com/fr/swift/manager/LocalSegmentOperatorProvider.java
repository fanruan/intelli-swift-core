package com.fr.swift.manager;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.DecreaseSegmentOperator;
import com.fr.swift.segment.HistorySegmentOperator;
import com.fr.swift.segment.ISegmentOperator;
import com.fr.swift.segment.IncreaseSegmentOperator;
import com.fr.swift.segment.SegmentOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.DataSourceUtils;

public class LocalSegmentOperatorProvider implements SegmentOperatorProvider {

    private static LocalSegmentOperatorProvider INSTANCE = new LocalSegmentOperatorProvider();

    public static LocalSegmentOperatorProvider getInstance() {
        return INSTANCE;
    }

    private SwiftSegmentManager manager;

    private LocalSegmentOperatorProvider() {
        manager = new LineSegmentManager();
    }

    public void registerSwiftSegmentManager(SwiftSegmentManager manager) {
        this.manager = manager;
    }

    @Override
    public ISegmentOperator getIndexSegmentOperator(DataSource dataSource) {
        try {
            return new HistorySegmentOperator(dataSource.getSourceKey(), dataSource.getMetadata(),
                    manager.getSegment(dataSource.getSourceKey()), DataSourceUtils.getSwiftSourceKey(dataSource));
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public ISegmentOperator getRealtimeSegmentOperator(DataSource dataSource) {
        try {
            return new IncreaseSegmentOperator(dataSource.getSourceKey(), dataSource.getMetadata(),
                    manager.getSegment(dataSource.getSourceKey()), DataSourceUtils.getSwiftSourceKey(dataSource));
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public ISegmentOperator getDecreaseSegmentOperator(DataSource dataSource) {
        try {
            return new DecreaseSegmentOperator(dataSource.getSourceKey(), dataSource.getMetadata(),
                    manager.getSegment(dataSource.getSourceKey()), DataSourceUtils.getSwiftSourceKey(dataSource));
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }
}

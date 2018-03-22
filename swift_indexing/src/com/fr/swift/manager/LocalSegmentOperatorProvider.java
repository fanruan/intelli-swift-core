package com.fr.swift.manager;

import com.fr.swift.segment.HistoryFieldsSegmentOperator;
import com.fr.swift.segment.HistorySegmentOperator;
import com.fr.swift.segment.SegmentOperator;
import com.fr.swift.segment.SegmentOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.decrease.DecreaseSegmentOperator;
import com.fr.swift.segment.increase.IncreaseFieldsSegmentOperator;
import com.fr.swift.segment.increase.IncreaseSegmentOperator;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.DataSourceUtils;

import java.sql.SQLException;

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
    public SegmentOperator getHistorySegmentOperator(DataSource dataSource, SwiftResultSet resultSet) {
        try {
            if (DataSourceUtils.isAddColumn(dataSource)) {
                return new HistoryFieldsSegmentOperator(dataSource.getSourceKey(), DataSourceUtils.getSwiftSourceKey(dataSource),
                        resultSet, DataSourceUtils.getAddFields(dataSource));
            }
            return new HistorySegmentOperator(dataSource.getSourceKey(), DataSourceUtils.getSwiftSourceKey(dataSource), resultSet);
        } catch (SQLException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public SegmentOperator getIncreaseSegmentOperator(DataSource dataSource, SwiftResultSet resultSet) {
        try {
            if (DataSourceUtils.isAddColumn(dataSource)) {
                return new IncreaseFieldsSegmentOperator(dataSource.getSourceKey(),
                        manager.getSegment(dataSource.getSourceKey()), DataSourceUtils.getSwiftSourceKey(dataSource),
                        resultSet, DataSourceUtils.getAddFields(dataSource));
            }
            return new IncreaseSegmentOperator(dataSource.getSourceKey(),
                    manager.getSegment(dataSource.getSourceKey()), DataSourceUtils.getSwiftSourceKey(dataSource), resultSet);
        } catch (SQLException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public SegmentOperator getDecreaseSegmentOperator(DataSource dataSource, SwiftResultSet resultSet) {
        try {
            return new DecreaseSegmentOperator(dataSource.getSourceKey(),
                    manager.getSegment(dataSource.getSourceKey()), DataSourceUtils.getSwiftSourceKey(dataSource), resultSet);
        } catch (SQLException e) {
            return Crasher.crash(e);
        }
    }
}

package com.fr.swift.segment;

import com.fr.swift.db.Table;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.operator.Deleter;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;

/**
 * @author yee
 * @date 2018/1/4
 */
public interface SwiftDataOperatorProvider {
    Inserter getHistorySwiftInserter(Segment segment, DataSource dataSource) throws Exception;

    Inserter getRealtimeSwiftInserter(Segment segment, DataSource dataSource) throws Exception;

    Inserter getHistoryBlockSwiftInserter(DataSource dataSource);

    /**
     * 导入
     *
     * @param table 表
     * @return inserter
     * @throws Exception 异常
     */
    Inserter getHistoryInserter(Table table) throws Exception;

    Inserter getRealtimeBlockSwiftInserter(DataSource dataSource);

    /**
     * 插入
     *
     * @param table 表
     * @return inserter
     * @throws Exception 异常
     */
    Inserter getRealtimeInserter(Table table) throws Exception;

    Deleter getSwiftDeleter(Segment segment) throws SwiftMetaDataException;
}

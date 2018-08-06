package com.fr.swift.segment;

import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;

/**
 * @author yee
 * @date 2018/1/4
 */
public interface SwiftDataOperatorProvider {
    @Deprecated
    Inserter getHistoryBlockSwiftInserter(DataSource dataSource);
}

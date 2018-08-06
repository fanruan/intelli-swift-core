package com.fr.swift.manager;

import com.fr.swift.generate.segment.operator.inserter.BlockInserter;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.util.DataSourceUtils;
import com.fr.third.springframework.stereotype.Service;

@Service
public class LocalDataOperatorProvider implements SwiftDataOperatorProvider {

    @Override
    public Inserter getHistoryBlockSwiftInserter(DataSource dataSource) {
        if (DataSourceUtils.isAddColumn(dataSource)) {
            return new BlockInserter(dataSource.getSourceKey(), DataSourceUtils.getSwiftSourceKey(dataSource).getId(),
                    dataSource.getMetadata(), DataSourceUtils.getAddFields(dataSource));
        }
        return new BlockInserter(dataSource.getSourceKey(), DataSourceUtils.getSwiftSourceKey(dataSource).getId(),
                dataSource.getMetadata());
    }
}

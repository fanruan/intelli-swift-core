package com.fr.swift.source.etl;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pony on 2018/1/5.
 */
public abstract class AbstractOperator implements ETLOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ETLOperator.class);
    private Core core;

    @Override
    public Core fetchObjectCore() {
        if (core == null) {
            core = new CoreGenerator(this).fetchObjectCore();
        }
        return core;
    }

    @Override
    public List<SwiftMetaDataColumn> getBaseColumns(SwiftMetaData[] metaDatas) {
        if (getOperatorType().isAddColumn()) {
            List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
            for (SwiftMetaData basedMeta : metaDatas) {
                try {
                    for (int i = 0; i < basedMeta.getColumnCount(); i++) {
                        columns.add(basedMeta.getColumn(i + 1));
                    }
                } catch (Exception e) {
                    LOGGER.error(getOperatorType() + "get origin meta failed");
                }
            }
            return columns;
        }
        return new ArrayList<SwiftMetaDataColumn>();
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.emptyList();
    }
}

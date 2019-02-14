package com.fr.swift.reliance;

import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.BasicCore;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.List;

/**
 * This class created on 2018/4/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TestOperator implements ETLOperator {
    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        return null;
    }

    @Override
    public List<SwiftMetaDataColumn> getBaseColumns(SwiftMetaData[] metaDatas) {
        return null;
    }

    @Override
    public OperatorType getOperatorType() {
        return null;
    }

    @Override
    public List<String> getNewAddedName() {
        return null;
    }

    @Override
    public Core fetchObjectCore() {
        return new BasicCore();
    }
}
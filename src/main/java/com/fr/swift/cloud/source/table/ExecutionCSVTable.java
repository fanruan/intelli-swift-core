package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/5/10
 *
 * @author Lucifer
 * @description
 */
public class ExecutionCSVTable extends SwiftCSVTable {

    public static final SwiftMetaDataColumn coreConsume = new MetaDataColumnBean("coreConsume", Types.BIGINT);

    public ExecutionCSVTable(SwiftMetaData dbMetadata, String appId, String yearMonth) {
        super(dbMetadata, appId, yearMonth);
    }

    @Override
    public List<SwiftMetaDataColumn> getExtraColumns() {
        return Collections.singletonList(coreConsume);
    }

    public List<String> getExtraColumnNames() {
        return Collections.singletonList(coreConsume.getName());
    }
}

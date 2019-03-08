package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.db.SwiftDatabase;

/**
 * Created by lyon on 2019/2/28.
 */
public interface CSVTable {

    SwiftMetaDataBean createBean(SwiftDatabase db);

    LineParser getParser();

    String getTableName();
}

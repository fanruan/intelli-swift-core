package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.cloud.source.load.LineParser;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public interface CloudTable {

    SwiftMetaDataColumn appIdColumn = new MetaDataColumnBean("appId", Types.VARCHAR);
    SwiftMetaDataColumn yearMonthColumn = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    CloudTableType getTableType();

    String getTableName();

    SwiftMetaDataBean getDBMetadata();

    SwiftMetaDataBean getVersionMetadata();

    LineParser getParser();
}
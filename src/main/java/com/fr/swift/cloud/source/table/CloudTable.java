package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.db.SwiftDatabase;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
public interface CloudTable {

    CloudTableType getTableType();

    String getTableName();

    SwiftMetaDataBean createBean(SwiftDatabase db);
}
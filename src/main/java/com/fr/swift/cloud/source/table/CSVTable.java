package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.SwiftDatabase;

/**
 * Created by lyon on 2019/2/28.
 */
interface CSVTable {

    SwiftMetaDataBean createBean(SwiftDatabase db);
}

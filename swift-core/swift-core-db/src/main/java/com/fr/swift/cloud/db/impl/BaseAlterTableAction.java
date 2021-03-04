package com.fr.swift.cloud.db.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.db.AlterTableAction;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.util.Crasher;

/**
 * @author anchore
 * @date 2018/8/14
 */
abstract class BaseAlterTableAction implements AlterTableAction {
    static final SwiftMetaDataService CONF_SVC = SwiftContext.get().getBean(SwiftMetaDataService.class);

    SwiftMetaDataColumn[] relatedColumnMeta;

    BaseAlterTableAction(SwiftMetaDataColumn... relatedColumnMeta) {
        this.relatedColumnMeta = relatedColumnMeta;
    }

    boolean existsColumn(SwiftMetaData meta) {
        try {
            for (SwiftMetaDataColumn relatedColumn : relatedColumnMeta) {
                if (meta.getColumnIndex(relatedColumn.getName()) == -1) {
                    return false;
                }
            }
            return true;
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }
}
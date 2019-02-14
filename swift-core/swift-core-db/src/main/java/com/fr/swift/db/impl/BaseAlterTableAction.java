package com.fr.swift.db.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.AlterTableAction;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 * @date 2018/8/14
 */
abstract class BaseAlterTableAction implements AlterTableAction {
    static final SwiftMetaDataService CONF_SVC = SwiftContext.get().getBean(SwiftMetaDataService.class);

    SwiftMetaDataColumn relatedColumnMeta;

    BaseAlterTableAction(SwiftMetaDataColumn relatedColumnMeta) {
        this.relatedColumnMeta = relatedColumnMeta;
    }

    boolean existsColumn(SwiftMetaData meta) {
        try {
            for (int i = 0; i < meta.getColumnCount(); i++) {
                if (meta.getColumnName(i + 1).equals(relatedColumnMeta.getName())) {
                    return true;
                }
            }
            return false;
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }
}
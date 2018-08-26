package com.fr.swift.db.impl;

import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.AlterTableAction;
import com.fr.swift.source.SwiftMetaDataColumn;

/**
 * @author anchore
 * @date 2018/8/14
 */
abstract class BaseAlterTableAction implements AlterTableAction {
    static final SwiftMetaDataService CONF_SVC = SwiftContext.get().getBean(SwiftMetaDataService.class);

    SwiftMetaDataColumn relatedColumnMeta;

    public BaseAlterTableAction(SwiftMetaDataColumn relatedColumnMeta) {
        this.relatedColumnMeta = relatedColumnMeta;
    }
}
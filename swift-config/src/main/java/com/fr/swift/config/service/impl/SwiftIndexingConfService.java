package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.entity.key.ColumnId;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.IndexingConfService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.function.Function;

/**
 * @author anchore
 * @date 2018/7/2
 */
@SwiftBean
public class SwiftIndexingConfService implements IndexingConfService {
    private SwiftHibernateConfigCommandBus<SwiftColumnIndexingConf> commandBus = new SwiftHibernateConfigCommandBus<>(SwiftColumnIndexingConf.class);
    private SwiftHibernateConfigQueryBus<SwiftColumnIndexingConf> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftColumnIndexingConf.class);



    @Override
    public SwiftColumnIndexingConf getColumnConf(final SourceKey table, final String columnName) {
        final SwiftColumnIndexingConf defaultConf = new SwiftColumnIndexingConf(new SourceKey(table.getId()), columnName, true, false);

        return queryBus.select(new ColumnId(table, columnName), new Function<SwiftColumnIndexingConf, SwiftColumnIndexingConf>() {
            @Override
            public SwiftColumnIndexingConf apply(SwiftColumnIndexingConf p) {
                return null == p ? defaultConf : p;
            }
        });
    }


    @Override
    public void setColumnConf(final SwiftColumnIndexingConf conf) {
        commandBus.merge(conf);
    }

}
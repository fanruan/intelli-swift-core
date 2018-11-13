package com.fr.swift.jdbc.metadata.emb;

import com.fr.swift.api.rpc.TableService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.jdbc.metadata.AbstractTableMetaDataGetter;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/9/4
 */
public class EmbMetaDataGetter extends AbstractTableMetaDataGetter {
    public EmbMetaDataGetter(SwiftDatabase database, String tableName) {
        super(database, tableName);
    }

    @Override
    public SwiftMetaData get() {
        try {
            return SwiftContext.get().getBean(TableService.class).detectiveMetaData(database, tableName);
        } catch (SwiftMetaDataAbsentException e) {
            return null;
        }
    }
}

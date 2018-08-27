package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.third.org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
@RpcService(value = TableService.class, type = RpcServiceType.EXTERNAL)
class TableServiceImpl implements TableService {
    @Override
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        SwiftMetaDataService swiftMetaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
        List<SwiftMetaData> metaDataList = swiftMetaDataService.find(Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_TABLE_NAME, tableName),
                Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_SWIFT_SCHEMA, schema));
        if (metaDataList.isEmpty()) {
            throw new SwiftMetaDataAbsentException(tableName);
        }
        return metaDataList.get(0);
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        return null != detectiveMetaData(schema, tableName);
    }
}

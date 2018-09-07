package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.third.org.hibernate.criterion.Restrictions;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
@RpcService(value = TableService.class, type = RpcService.RpcServiceType.EXTERNAL)
class TableServiceImpl implements TableService {
    @Autowired(required = false)
    SwiftMetaDataService swiftMetaDataService;
    @Override
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        List<SwiftMetaData> metaDataList = swiftMetaDataService.find(Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_TABLE_NAME, tableName),
                Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_SWIFT_SCHEMA, schema));
        if (metaDataList.isEmpty()) {
            throw new SwiftMetaDataAbsentException(tableName);
        }
        return metaDataList.get(0);
    }

    @Override
    public List<String> detectiveAllTableNames(SwiftDatabase schema) {
        List<SwiftMetaData> metaDataList = swiftMetaDataService.find(Restrictions.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_SWIFT_SCHEMA, schema));
        if (metaDataList.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> tableNames = new ArrayList<String>();
            try {
                for (SwiftMetaData metaData : metaDataList) {
                    tableNames.add(metaData.getTableName());
                }
            } catch (Exception ignore) {
            }
            return tableNames;
        }
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) {
        try {
            return null != detectiveMetaData(schema, tableName);
        } catch (Exception e) {
            return false;
        }
    }
}

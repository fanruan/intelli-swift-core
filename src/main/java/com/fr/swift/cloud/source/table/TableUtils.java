package com.fr.swift.cloud.source.table;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;

/**
 * Created by lyon on 2019/2/28.
 */
public class TableUtils {

    /**
     * 根据版本获得对应metadata，并和db里的metadata比较
     *
     * @param versionMetadata
     * @return
     * @throws SwiftMetaDataException
     */
    public static CloudTable createIfAbsent(SwiftMetaData versionMetadata, String appId, String yearMonth) throws SwiftMetaDataException {
        SwiftMetaDataService service = SwiftContext.get().getBean(SwiftMetaDataService.class);
        String tableName = versionMetadata.getTableName();
        SwiftMetaData dbMetadata = service.getMetaDataByKey(tableName);
        CloudTable cloudTable;
        if (dbMetadata == null) {
            service.addMetaData(tableName, versionMetadata);
            dbMetadata = versionMetadata;
        }
        if (tableName.equals("gc_record")) {
            cloudTable = new SwiftGCTable(dbMetadata, versionMetadata, appId, yearMonth);
        } else {
            if (tableName.equals("execution")) {
                cloudTable = new ExecutionCSVTable(dbMetadata, versionMetadata, appId, yearMonth);
            } else {
                cloudTable = new SwiftCSVTable(dbMetadata, versionMetadata, appId, yearMonth);
            }
        }
        return cloudTable;
    }
}

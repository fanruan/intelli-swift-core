package com.fr.swift.cloud.config.service;

import com.fr.swift.cloud.annotation.service.DbService;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@DbService
public interface SwiftMetaDataService {

    void saveMeta(SwiftMetaData meta);

    void updateMeta(SwiftMetaData newMeta);

    List<SwiftMetaData> getAllMetas();

    List<SwiftMetaData> getMetasBySchema(SwiftDatabase schema);

    SwiftMetaData getMeta(SourceKey tableKey);

    boolean existsMeta(SourceKey tableKey);

    void deleteMeta(SourceKey tableKey);

    List<SwiftMetaData> getFuzzyMetaData(SourceKey tableKey);
}
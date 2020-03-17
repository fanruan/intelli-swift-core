package com.fr.swift.config.service;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
public interface SwiftMetaDataService {

    void saveMeta(SwiftMetaData meta);

    void updateMeta(SwiftMetaData newMeta);

    List<SwiftMetaData> getAllMetas();

    SwiftMetaData getMeta(SourceKey tableKey);

    boolean existsMeta(SourceKey tableKey);

    void deleteMeta(SourceKey tableKey);

    List<SwiftMetaData> getFuzzyMetaData(SourceKey tableKey);
}
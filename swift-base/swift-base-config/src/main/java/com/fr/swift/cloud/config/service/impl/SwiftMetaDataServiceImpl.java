package com.fr.swift.cloud.config.service.impl;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.dao.SwiftDao;
import com.fr.swift.cloud.config.dao.SwiftDaoImpl;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yee
 * @date 2018/6/6
 */
@SwiftBean(name = "swiftMetaDataService")
public class SwiftMetaDataServiceImpl implements SwiftMetaDataService {
    private SwiftDao<SwiftMetaDataEntity> dao = new SwiftDaoImpl<>(SwiftMetaDataEntity.class);

    public SwiftMetaDataServiceImpl() {
        MetadataContainer.DEFAULT.initMetas((List<SwiftMetaData>) dao.selectAll());
    }

    @Override
    public void saveMeta(SwiftMetaData meta) {
        dao.insert((SwiftMetaDataEntity) meta);
        MetadataContainer.DEFAULT.saveMeta(meta);
    }

    @Override
    public void updateMeta(SwiftMetaData newMeta) {
        dao.update((SwiftMetaDataEntity) newMeta);
        MetadataContainer.DEFAULT.updateMeta(newMeta);
    }

    @Override
    public List<SwiftMetaData> getAllMetas() {
        return MetadataContainer.DEFAULT.getAllMetas();
    }

    @Override
    public List<SwiftMetaData> getMetasBySchema(SwiftDatabase schema) {
        return MetadataContainer.DEFAULT.getMetasBySchema(schema);
    }

    @Override
    public SwiftMetaData getMeta(final SourceKey tableKey) {
        return MetadataContainer.DEFAULT.getMeta(tableKey);
    }

    @Override
    public boolean existsMeta(SourceKey tableKey) {
        return MetadataContainer.DEFAULT.existsMeta(tableKey);
    }

    @Override
    public void deleteMeta(final SourceKey tableKey) {
        dao.deleteQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get("id"), tableKey.getId())));
        MetadataContainer.DEFAULT.deleteMeta(tableKey);
    }

    @Override
    public List<SwiftMetaData> getFuzzyMetaData(SourceKey tableKey) {
        return MetadataContainer.DEFAULT.getFuzzyMetaData(tableKey);
    }

    enum MetadataContainer implements SwiftMetaDataService {
        DEFAULT;

        Map<String, SwiftMetaData> metaDataMap;

        void initMetas(List<SwiftMetaData> metaDatas) {
            metaDataMap = metaDatas.stream().collect(Collectors.toConcurrentMap(meta -> meta.getTableName(), meta -> meta));
        }

        @Override
        public void saveMeta(SwiftMetaData meta) {
            metaDataMap.put(meta.getTableName(), meta);
        }

        @Override
        public void updateMeta(SwiftMetaData newMeta) {
            metaDataMap.put(newMeta.getTableName(), newMeta);

        }

        @Override
        public List<SwiftMetaData> getAllMetas() {
            return metaDataMap.values().stream().sorted(Comparator.comparing(SwiftMetaData::getId)).collect(Collectors.toList());
        }

        @Override
        public List<SwiftMetaData> getMetasBySchema(SwiftDatabase schema) {
            return metaDataMap.values().stream()
                    .filter(metaData -> metaData.getSwiftDatabase() == schema)
                    .collect(Collectors.toList());
        }

        @Override
        public SwiftMetaData getMeta(SourceKey tableKey) {
            return metaDataMap.get(tableKey.getId());
        }

        @Override
        public boolean existsMeta(SourceKey tableKey) {
            return metaDataMap.containsKey(tableKey.getId());
        }

        @Override
        public void deleteMeta(SourceKey tableKey) {
            metaDataMap.remove(tableKey.getId());
        }

        @Override
        public List<SwiftMetaData> getFuzzyMetaData(SourceKey tableKey) {
            return metaDataMap.values().stream()
                    .filter(metaData -> metaData.getTableName().contains(tableKey.getId()))
                    .collect(Collectors.toList());
        }
    }
}

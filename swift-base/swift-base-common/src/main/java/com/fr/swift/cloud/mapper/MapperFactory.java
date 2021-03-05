package com.fr.swift.cloud.mapper;

import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.List;

/**
 * @author lucifer
 * @date 2020/3/13
 * @description
 * @since swift 1.1
 */
public interface MapperFactory {

    <T> List<T> getMapperedList(SwiftResultSet swiftResultSet, Class<T> tClass) throws Exception;

    <T> T getMappered(Row row, SwiftMetaData metaData, Class<T> tClass) throws Exception;

    static MapperFactory getMapper() {
        return SwiftMapperFactory.get();
    }
}

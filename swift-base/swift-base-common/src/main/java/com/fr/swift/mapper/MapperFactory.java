package com.fr.swift.mapper;

import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

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

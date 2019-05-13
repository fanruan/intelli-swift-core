package com.fr.swift.cloud.source.load;

import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
public interface LineParser<T> {

    Map<String, Object> parseToMap(T input);

    List<SwiftMetaDataColumn> getFields();
}

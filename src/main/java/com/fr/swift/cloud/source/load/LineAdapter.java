package com.fr.swift.cloud.source.load;

import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
public interface LineAdapter {

    Map<String, Object> adapt(Map<String, Object> origin);

    List<SwiftMetaDataColumn> getFields();

    LineAdapter DUMMY = new LineAdapter() {
        @Override
        public Map<String, Object> adapt(Map<String, Object> origin) {
            return origin;
        }

        @Override
        public List<SwiftMetaDataColumn> getFields() {
            return new ArrayList<SwiftMetaDataColumn>();
        }
    };
}

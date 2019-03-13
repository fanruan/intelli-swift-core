package com.fr.swift.cloud.source.load;

import com.fr.swift.cloud.source.table.Execution;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
public class AddColumnAdapter implements LineAdapter {

    private String consume = Execution.consume.getName();
    private String sqlTime = Execution.sqlTime.getName();

    @Override
    public Map<String, Object> adapt(Map<String, Object> origin) {
        Object v1 = origin.get(consume);
        Object v2 = origin.get(sqlTime);
        Object value = null;
        if (v1 != null && v2 != null) {
            value = (Long) v1 - (Long) v2;
        }
        origin.put(Execution.coreConsume.getName(), value);
        return origin;
    }

    @Override
    public List<SwiftMetaDataColumn> getFields() {
        return Collections.singletonList(Execution.coreConsume);
    }
}

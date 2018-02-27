package com.fr.swift.adaptor.builder;

import com.fr.json.JSONObject;
import com.fr.swift.adaptor.WidgetAdaptor;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.SwiftResultSet;

/**
 * @author pony
 * @date 2017/12/21
 */
public abstract class AbstractWidgetAdaptor implements WidgetAdaptor {
    protected abstract QueryInfo buildQueryInfo();

    protected abstract JSONObject convert(SwiftResultSet swiftResultSet) throws Exception;

    public JSONObject getResult() throws Exception {
        QueryInfo info = buildQueryInfo();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(info);
        return convert(resultSet);
    }
}

package com.fr.swift.api.info;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.api.info.api.CreateTableRequestInfo;
import com.fr.swift.api.info.api.DeleteRequestInfo;
import com.fr.swift.api.info.api.DropRequestInfo;
import com.fr.swift.api.info.api.InsertRequestInfo;
import com.fr.swift.api.info.api.QueryRequestInfo;
import com.fr.swift.api.info.api.TruncateRequestInfo;
import com.fr.swift.api.info.jdbc.CatalogRequestInfo;
import com.fr.swift.api.info.jdbc.ColumnsRequestInfo;
import com.fr.swift.api.info.jdbc.SqlRequestInfo;
import com.fr.swift.api.info.jdbc.TablesRequestInfo;
import com.fr.swift.base.json.JsonBuilder;

/**
 * basic interface of requestType info for api and jdbc
 *
 * @author yee
 * @date 2018/11/16
 * @see JsonBuilder#writeJsonString(Object)
 * @see BaseRequestInfo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "requestType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "AUTH", value = AuthRequestInfo.class),
        @JsonSubTypes.Type(name = "SQL", value = SqlRequestInfo.class),
        @JsonSubTypes.Type(name = "TABLES", value = TablesRequestInfo.class),
        @JsonSubTypes.Type(name = "CATALOGS", value = CatalogRequestInfo.class),
        @JsonSubTypes.Type(name = "COLUMNS", value = ColumnsRequestInfo.class),
        @JsonSubTypes.Type(name = "CREATE_TABLE", value = CreateTableRequestInfo.class),
        @JsonSubTypes.Type(name = "DELETE", value = DeleteRequestInfo.class),
        @JsonSubTypes.Type(name = "INSERT", value = InsertRequestInfo.class),
        @JsonSubTypes.Type(name = "JSON_QUERY", value = QueryRequestInfo.class),
        @JsonSubTypes.Type(name = "DROP_TABLE", value = DropRequestInfo.class),
        @JsonSubTypes.Type(name = "TRUNCATE_TABLE", value = TruncateRequestInfo.class)
})
public interface RequestInfo<T extends RequestParserVisitor> extends Accepter<T> {
    /**
     * any requestType should contains an auth code except auth requestType.
     *
     * @return return null if it's an auth requestType.
     * return auth code if it's other request.
     */
    String getAuthCode();

    /**
     * get requestType type like <code>AUTH</code>,
     * <code>SQL</code>...
     *
     * @return requestType type for this requestType
     */
    RequestType getRequestType();
}

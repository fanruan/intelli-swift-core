package com.fr.swift.api.info;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.base.json.annotation.JsonSubTypes;

/**
 * basic interface of request info for api and jdbc
 *
 * @author yee
 * @date 2018/11/16
 * @see JsonBuilder#writeJsonString(Object)
 * @see BaseRequestInfo
 */
public interface RequestInfo<T extends RequestParserVisitor> extends Accepter<T> {
    /**
     * any request should contains an auth code except auth request.
     *
     * @return return null if it's an auth request.
     * return auth code if it's other request.
     */
    String getAuthCode();

    Request AUTH = new Request() {
        @Override
        public String name() {
            return "AUTH";
        }
    };

    /**
     * get request type like <code>AUTH</code>,
     * <code>SQL</code>...
     *
     * @param <R> type extend Request
     * @return request type for this request
     * @see RequestInfo#AUTH
     * @see Request
     */
    <R extends Request> R getRequest();

    /**
     * basic interface for request type
     */
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ApiRequestType.class, name = "JSON_QUERY"),
            @JsonSubTypes.Type(value = ApiRequestType.class, name = "INSERT"),
            @JsonSubTypes.Type(value = ApiRequestType.class, name = "DELETE"),
            @JsonSubTypes.Type(value = ApiRequestType.class, name = "CREATE_TABLE"),
            @JsonSubTypes.Type(value = ApiRequestType.class, name = "DROP_TABLE"),
            @JsonSubTypes.Type(value = ApiRequestType.class, name = "TRUNCATE_TABLE"),
            @JsonSubTypes.Type(value = JdbcRequestType.class, name = "SQL"),
            @JsonSubTypes.Type(value = JdbcRequestType.class, name = "TABLES"),
            @JsonSubTypes.Type(value = JdbcRequestType.class, name = "COLUMNS")
    }
    )
    interface Request {
        /**
         * return request type name
         *
         * @return request type name
         */
        String name();
    }
}

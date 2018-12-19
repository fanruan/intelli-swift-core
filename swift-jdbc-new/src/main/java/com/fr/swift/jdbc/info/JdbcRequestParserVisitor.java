package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.RequestParserVisitor;

/**
 * Request parser visitor for jdbc
 * @author lucifer
 */
public interface JdbcRequestParserVisitor extends RequestParserVisitor {

    /**
     * Visit tables request info
     *
     * @param tablesRequestInfo tables request info
     * @return invocation for tables request
     */
    ApiInvocation visit(TablesRequestInfo tablesRequestInfo);

    /**
     * Visit sql request info
     * @param tablesRequestInfo sql request info
     * @return invocation for sql request
     */
    ApiInvocation visit(SqlRequestInfo tablesRequestInfo);

    /**
     * Visit columns request info
     * @param tablesRequestInfo columns request info
     * @return invocation for columns request
     */
    ApiInvocation visit(ColumnsRequestInfo tablesRequestInfo);
}

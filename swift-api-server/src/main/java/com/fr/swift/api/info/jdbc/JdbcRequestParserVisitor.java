package com.fr.swift.api.info.jdbc;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.RequestParserVisitor;

/**
 * Request parser visitor for jdbc
 *
 * @author lucifer
 */
public interface JdbcRequestParserVisitor extends RequestParserVisitor {

    /**
     * Visit tables requestType info
     *
     * @param tablesRequestInfo tables requestType info
     * @return invocation for tables requestType
     */
    ApiInvocation visit(TablesRequestInfo tablesRequestInfo);

    /**
     * Visit sql requestType info
     *
     * @param tablesRequestInfo sql requestType info
     * @return invocation for sql requestType
     */
    ApiInvocation visit(SqlRequestInfo tablesRequestInfo);

    /**
     * Visit columns requestType info
     *
     * @param tablesRequestInfo columns requestType info
     * @return invocation for columns requestType
     */
    ApiInvocation visit(ColumnsRequestInfo tablesRequestInfo);

    /**
     * Visit catalogs requestType info
     *
     * @param tablesRequestInfo columns requestType info
     * @return invocation for columns requestType
     */

    ApiInvocation visit(CatalogRequestInfo tablesRequestInfo);
}

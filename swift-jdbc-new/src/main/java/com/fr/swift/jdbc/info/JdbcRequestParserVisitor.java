package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.RequestParserVisitor;

/**
 * @author lucifer
 */
public interface JdbcRequestParserVisitor extends RequestParserVisitor {

    ApiInvocation visit(TablesRequestInfo tablesRequestInfo);

    ApiInvocation visit(SqlRequestInfo tablesRequestInfo);

    ApiInvocation visit(ColumnsRequestInfo tablesRequestInfo);
}

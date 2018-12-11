package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.ApiInvocation;

public interface RequestParserVisitor {

    ApiInvocation visit(TablesRequestInfo tablesRequestInfo);

    ApiInvocation visit(SqlRequestInfo tablesRequestInfo);

    ApiInvocation visit(ColumnsRequestInfo tablesRequestInfo);
}

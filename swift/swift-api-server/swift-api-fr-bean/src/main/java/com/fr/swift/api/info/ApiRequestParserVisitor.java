package com.fr.swift.api.info;

/**
 * @author yee
 * @date 2018-12-11
 */
public interface ApiRequestParserVisitor extends RequestParserVisitor {
    ApiInvocation visit(QueryRequestInfo queryRequestInfo);

    ApiInvocation visit(CreateTableRequestInfo createTableRequestInfo);

    ApiInvocation visit(DeleteTableRequestInfo deleteTableRequestInfo);

    ApiInvocation visit(InsertRequestInfo insertRequestInfo);

    ApiInvocation visit(TableRequestInfo tableRequestInfo);

}

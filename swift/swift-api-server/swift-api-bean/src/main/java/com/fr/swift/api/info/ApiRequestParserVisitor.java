package com.fr.swift.api.info;

/**
 * request visitor for api
 * @author yee
 * @date 2018-12-11
 */
public interface ApiRequestParserVisitor extends RequestParserVisitor {
    /**
     * visit query request info
     *
     * @param queryRequestInfo query request info
     * @return invocation for query request
     */
    ApiInvocation visit(QueryRequestInfo queryRequestInfo);

    /**
     * visit query request info
     *
     * @param createTableRequestInfo create table request info
     * @return invocation for create table request
     */
    ApiInvocation visit(CreateTableRequestInfo createTableRequestInfo);

    /**
     * visit query request info
     *
     * @param deleteRequestInfo delete table request info
     * @return invocation for delete table request
     */
    ApiInvocation visit(DeleteRequestInfo deleteRequestInfo);

    /**
     * visit query request info
     *
     * @param insertRequestInfo insert request info
     * @return invocation for insert request
     */
    ApiInvocation visit(InsertRequestInfo insertRequestInfo);

    /**
     * visit query request info
     * @param tableRequestInfo table request info
     *                         the request type might be TRUNCATE, DROP
     * @see ApiRequestType
     * @return invocation for table request
     */
    ApiInvocation visit(TableRequestInfo tableRequestInfo);

}

package com.fr.swift.api.info.api;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.RequestParserVisitor;
import com.fr.swift.api.info.RequestType;

/**
 * requestType visitor for api
 * @author yee
 * @date 2018-12-11
 */
public interface ApiRequestParserVisitor extends RequestParserVisitor {
    /**
     * visit query requestType info
     *
     * @param queryRequestInfo query requestType info
     * @return invocation for query requestType
     */
    ApiInvocation visit(QueryRequestInfo queryRequestInfo);

    /**
     * visit query requestType info
     *
     * @param createTableRequestInfo create table requestType info
     * @return invocation for create table requestType
     */
    ApiInvocation visit(CreateTableRequestInfo createTableRequestInfo);

    /**
     * visit query requestType info
     *
     * @param deleteRequestInfo delete table requestType info
     * @return invocation for delete table requestType
     */
    ApiInvocation visit(DeleteRequestInfo deleteRequestInfo);

    /**
     * visit query requestType info
     *
     * @param insertRequestInfo insert requestType info
     * @return invocation for insert requestType
     */
    ApiInvocation visit(InsertRequestInfo insertRequestInfo);

    /**
     * visit query requestType info
     * @param tableRequestInfo table requestType info
     *                         the requestType type might be TRUNCATE, DROP
     * @see RequestType
     * @return invocation for table requestType
     */
    ApiInvocation visit(TableRequestInfo tableRequestInfo);

}

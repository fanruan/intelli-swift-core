package com.fr.swift.api.info;

/**
 * this is a common interface to visit requestType info
 * @author yee
 * @date 2018-12-11
 */
public interface RequestParserVisitor {
    /**
     * just visit auth requestType info
     *
     * @param authRequestInfo auth requestType info.
     * @return return an invocation to invoke this requestType
     * @see AuthRequestInfo
     * @see RequestInfo
     */
    ApiInvocation visit(AuthRequestInfo authRequestInfo);
}

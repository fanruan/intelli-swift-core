package com.fr.swift.api.info;

/**
 * this is a common interface to visit request info
 * @author yee
 * @date 2018-12-11
 */
public interface RequestParserVisitor {
    /**
     * just visit auth request info
     *
     * @param authRequestInfo auth request info.
     * @return return an invocation to invoke this request
     * @see AuthRequestInfo
     * @see RequestInfo
     */
    ApiInvocation visit(AuthRequestInfo authRequestInfo);
}

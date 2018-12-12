package com.fr.swift.api.info;

/**
 * @author yee
 * @date 2018-12-11
 */
public interface RequestParserVisitor {
    ApiInvocation visit(AuthRequestInfo authRequestInfo);
}

package com.fr.swift.api.info;

/**
 * accept visitor
 *
 * @param <T> any type extend RequestParserVisitor
 * @author lucifer
 * @see RequestParserVisitor
 * @see RequestInfo
 */
public interface Accepter<T extends RequestParserVisitor> {

    /**
     * visitor visits and get api invocation
     *
     * @param visitor any object implement RequestParserVisitor
     * @return invocation for request
     * @see RequestParserVisitor
     */
    ApiInvocation accept(T visitor);

}

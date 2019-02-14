package com.fr.swift.base.json.token;

/**
 * @author yee
 * @date 2018-12-13
 */
public enum TokenType {
    /**
     * JsonObject {
     */
    BEGIN_OBJECT,
    /**
     * JsonObject }
     */
    END_OBJECT,
    /**
     * JsonArray [
     */
    BEGIN_ARRAY,
    /**
     * JsonArray ]
     */
    END_ARRAY,
    /**
     * NullValue null
     */
    NULL,
    /**
     * NumberValue
     */
    NUMBER,
    /**
     * StringValue
     */
    STRING,
    /**
     * BooleanValue
     */
    BOOLEAN,
    /**
     * Colon :
     */
    SEP_COLON,
    /**
     * Comma ,
     */
    SEP_COMMA,
    /**
     * All End EOF
     */
    END_DOCUMENT
}

package com.fr.swift.base.json.token;

/**
 * @author yee
 * @date 2018-12-13
 */
public class Token<T> {
    private TokenType tokenType;
    private T value;

    public Token(TokenType tokenType, T value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}

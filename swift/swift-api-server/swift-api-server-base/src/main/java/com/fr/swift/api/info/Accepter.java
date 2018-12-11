package com.fr.swift.api.info;

public interface Accepter<T> {

    ApiInvocation accept(T visitor);

}

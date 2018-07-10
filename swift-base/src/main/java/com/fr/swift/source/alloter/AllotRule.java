package com.fr.swift.source.alloter;

/**
 * @author anchore
 * @date 2018/6/5
 */
public interface AllotRule {
    Type getType();

    enum Type {
        //
        LINE, HASH
    }
}
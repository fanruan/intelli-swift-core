package com.fr.swift.event;

/**
 * @author anchore
 * @date 12/4/2018
 */
public interface SwiftEventListener<D> {

    void on(D data);
}
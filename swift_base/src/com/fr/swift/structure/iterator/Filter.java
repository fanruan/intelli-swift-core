package com.fr.swift.structure.iterator;

/**
 * Created by Lyon on 2018/4/4.
 */
public interface Filter<Element> {

    boolean accept(Element element);
}

package com.fr.swift.query.group.by.paging;

/**
 * Created by Lyon on 2018/4/4.
 */
public interface Filter<Element> {

    boolean accept(Element element);
}

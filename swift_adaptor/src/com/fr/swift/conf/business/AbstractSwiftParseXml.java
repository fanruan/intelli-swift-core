package com.fr.swift.conf.business;

import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-23 16:21:58
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractSwiftParseXml<T> extends DefaultHandler {

    protected List<T> list;
    protected T resource;

    public List<T> getList() throws Exception {
        List<T> ret = new ArrayList<T>();
        for (T pack : list) {
            ret.add(pack);
        }
        return ret;
    }
}

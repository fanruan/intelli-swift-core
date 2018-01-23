package com.fr.swift.conf.business;

import java.io.OutputStream;
import java.util.List;

/**
 * This class created on 2018-1-23 16:32:57
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface ISwiftXmlWriter<T> {

    void write(List<T> tList, OutputStream outputStream) throws Exception;

}

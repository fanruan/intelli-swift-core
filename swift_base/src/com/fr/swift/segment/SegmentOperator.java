package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;

import java.util.List;

/**
 * This class created on 2018-1-10 10:51:13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface SegmentOperator {

    void transport() throws Exception;

    void finishTransport();

    List<String> getIndexFields() throws SwiftMetaDataException;
}

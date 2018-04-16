package com.fr.swift.exception;

import com.fr.swift.source.DataSource;

/**
 * This class created on 2018/4/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SegmentAbsentException extends Exception {

    public SegmentAbsentException(DataSource dataSource) {
        super(dataSource.getSourceKey().getId() + "'s segments absent!");
    }
}

package com.fr.swift.source.etl.expression;

import com.fr.swift.segment.Segment;

/**
 * Created by Handsome on 2018/3/1 0001 15:37
 */
public interface Expression {

    Object get(Segment segment, int row, int columnType);

}

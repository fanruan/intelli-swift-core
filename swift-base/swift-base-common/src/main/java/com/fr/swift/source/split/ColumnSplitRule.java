package com.fr.swift.source.split;

import com.fr.swift.source.Row;

/**
 * @author lucifer
 * @date 2019/7/26
 * @description
 * @since swift 1.1
 */
public interface ColumnSplitRule<S extends Row, R extends SubRow> {
    R split(S source) throws Exception;
}
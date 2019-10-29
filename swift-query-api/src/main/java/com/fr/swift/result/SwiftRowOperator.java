package com.fr.swift.result;

import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author Lyon
 * @date 2018/6/13
 */
public interface SwiftRowOperator<T extends Row> {

    List<T> operate(SwiftNode node);
}

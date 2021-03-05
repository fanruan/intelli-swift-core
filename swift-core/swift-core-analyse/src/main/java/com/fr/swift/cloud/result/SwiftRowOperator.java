package com.fr.swift.cloud.result;

import com.fr.swift.cloud.source.Row;

import java.util.List;

/**
 * @author Lyon
 * @date 2018/6/13
 */
public interface SwiftRowOperator<T extends Row> {

    List<T> operate(SwiftNode node);
}

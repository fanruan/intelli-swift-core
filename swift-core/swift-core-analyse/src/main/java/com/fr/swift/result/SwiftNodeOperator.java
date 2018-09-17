package com.fr.swift.result;

import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.Function;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/12.
 */
public interface SwiftNodeOperator extends Function<Pair<? extends SwiftNode, List<Map<Integer, Object>>>, Pair<SwiftNode, List<Map<Integer, Object>>>> {
}

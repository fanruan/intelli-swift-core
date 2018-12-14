package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2018/4/19
 */
public interface NodeResultSet<T extends SwiftNode> extends SwiftResultSet, QueryResultSet<Pair<T, List<Map<Integer, Object>>>> {
}

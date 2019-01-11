package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public interface NodeMergeQRS<T extends SwiftNode> extends QueryResultSet<Pair<T, List<Map<Integer, Object>>>> {
}

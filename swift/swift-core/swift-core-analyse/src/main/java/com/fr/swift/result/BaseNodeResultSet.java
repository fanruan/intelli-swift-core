package com.fr.swift.result;

import com.fr.swift.result.qrs.DSType;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 12/11/2018
 */
public abstract class BaseNodeResultSet<T extends SwiftNode> implements QueryResultSet<Pair<T, List<Map<Integer, Object>>>> {

    @Override
    public DSType type() {
        return DSType.NODE;
    }
}
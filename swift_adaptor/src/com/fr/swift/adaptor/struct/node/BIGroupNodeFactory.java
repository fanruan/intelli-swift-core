package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.KeyValue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BIGroupNodeFactory {

    public static BIGroupNode create(GroupByResultSet resultSet) {
        Iterator<KeyValue<int[], AggregatorValue[]>> iterator = resultSet.getRowResultIterator();
        List<Map<Integer, Object>> dictionaries = resultSet.getGlobalDictionaries();
        List<Sort> sorts = resultSet.getIndexSorts();
        Set<KeyValue<int[], Number[]>> rows = new TreeSet<KeyValue<int[], Number[]>>();
        return null;
    }
}
package com.fr.swift.result.node.resultset;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.result.group.GroupNodeMergeUtils;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/7/27.
 *
 *
 */
class GroupPageMerger implements Function<List<GroupPage>, GroupPage> {
    List<Aggregator> aggregators;
    List<Comparator<SwiftNode>> comparators;

    GroupPageMerger(List<Aggregator> aggregators, List<Comparator<SwiftNode>> comparators) {
        this.aggregators = aggregators;
        this.comparators = comparators;
    }

    @Override
    public GroupPage apply(List<GroupPage> groupByResultSets) {
        List<SwiftNode> roots = new ArrayList<SwiftNode>();
        List<Map<Integer, Object>> totalDictionaries = new ArrayList<Map<Integer, Object>>();
        for (GroupPage page : groupByResultSets) {
            roots.add(page.getRoot());
            addDictionaries(page.getGlobalDicts(), totalDictionaries);
        }
        SwiftNode mergeNode = GroupNodeMergeUtils.merge(roots, comparators, aggregators);
        return new GroupPage(mergeNode, totalDictionaries);
    }

    private void addDictionaries(List<Map<Integer, Object>> dictionaries,
                                 List<Map<Integer, Object>> totalDictionaries) {
        if (totalDictionaries.size() == 0) {
            for (int i = 0; i < dictionaries.size(); i++) {
                totalDictionaries.add(new HashMap<Integer, Object>());
            }
        }
        for (int i = 0; i < dictionaries.size(); i++) {
            Map<Integer, Object> dict = dictionaries.get(i);
            if (dict != null && !dict.isEmpty()) {
                totalDictionaries.get(i).putAll(dict);
            }
        }
    }
}

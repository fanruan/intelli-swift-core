package com.fr.swift.cloud.query.info.segment;

import com.fr.swift.cloud.source.alloter.AllotRule;
import com.fr.swift.cloud.source.alloter.impl.BaseAllotRule;
import com.fr.swift.cloud.source.alloter.impl.hash.HashAllotRule;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/5/7
 * @description
 * @since swift 1.1
 */
public class SegmentFuzzyBucket {

    protected final static int ALL_SEGMENT = -1;

    private final Set<Integer> bucketIndexes;

    private final Map<Integer, HashSet<Integer>> fuzzyBucketKeys = new HashMap<>();

    public SegmentFuzzyBucket(AllotRule allotRule, Set<Integer> bucketIndexes) {
        this.bucketIndexes = bucketIndexes;
        initByAllotRule(allotRule);
    }

    private void initByAllotRule(AllotRule allotRule) {
        if (allotRule.getType() == BaseAllotRule.AllotType.HASH) {
            HashAllotRule hashAllotRule = (HashAllotRule) allotRule;
            for (Integer bucketKey : bucketIndexes) {
                List<Integer> fuzzyKeys = hashAllotRule.getHashFunction().divideOf(bucketKey);
                fuzzyKeys.forEach(key -> fuzzyBucketKeys.computeIfAbsent(key, k -> new HashSet<>()).add(bucketKey));
            }
        }
    }

    public Set<Integer> getIncludedKey(Set<Integer> hashValueSet) {
        if (hashValueSet.contains(ALL_SEGMENT)) {
            return Sets.newHashSet(bucketIndexes);
        }
        Set<Integer> resultSet = new HashSet<>();
        hashValueSet.stream().filter(fuzzyBucketKeys::containsKey).map(fuzzyBucketKeys::get).forEach(resultSet::addAll);
        return resultSet;
    }
}

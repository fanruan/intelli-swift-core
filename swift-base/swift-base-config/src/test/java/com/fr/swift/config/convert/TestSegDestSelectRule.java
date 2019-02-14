package com.fr.swift.config.convert;

import com.fr.swift.config.SegmentDestSelectRule;
import com.fr.swift.segment.SegmentDestination;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-29
 */
public class TestSegDestSelectRule implements SegmentDestSelectRule {
    private String name;

    public TestSegDestSelectRule(String name) {
        this.name = name;
    }

    public TestSegDestSelectRule() {
    }

    @Override
    public List<SegmentDestination> selectDestination(List<SegmentDestination> duplicate) {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return name;
    }
}

package com.fr.swift.segment.operator.insert.impl;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.SwiftInserterBuilder;
import com.fr.swift.segment.operator.insert.SwiftInserter;

/**
 * @author yee
 * @date 2018/9/7
 */
public class SwiftInserterBuilderImpl implements SwiftInserterBuilder {
    @Override
    public Inserter build(Segment segment) {
        return new SwiftInserter(segment);
    }
}

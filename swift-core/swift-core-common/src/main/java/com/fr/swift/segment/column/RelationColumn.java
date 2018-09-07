package com.fr.swift.segment.column;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.segment.Segment;

/**
 * @author yee
 * @date 2018/9/7
 */
public interface RelationColumn extends Releasable {
    Object getPrimaryValue(int row);

    Column buildRelationColumn(Segment segment);

    int[] getPrimarySegAndRow(int row);

}

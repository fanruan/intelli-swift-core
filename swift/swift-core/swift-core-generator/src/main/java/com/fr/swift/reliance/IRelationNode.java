package com.fr.swift.reliance;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.Source;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public interface IRelationNode<N extends RelationSource, D extends Source> {
    SourceKey getKey();

    N getNode();

    List<D> getDepend();
}

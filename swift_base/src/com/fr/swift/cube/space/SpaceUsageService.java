package com.fr.swift.cube.space;

import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/4/13
 */
public interface SpaceUsageService {
    double getTableUsedSpace(SourceKey key);

    SpaceUsage getUsageOverall();
}
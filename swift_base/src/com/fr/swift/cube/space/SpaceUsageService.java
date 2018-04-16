package com.fr.swift.cube.space;

import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/13
 */
public interface SpaceUsageService {
    double getTableUsedSpace(SourceKey key) throws Exception;

    double getTableUsedSpace(List<SourceKey> key) throws Exception;

    SpaceUsage getUsageOverall();
}
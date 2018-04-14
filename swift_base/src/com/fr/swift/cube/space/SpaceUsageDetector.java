package com.fr.swift.cube.space;

import java.net.URI;

/**
 * @author anchore
 * @date 2018/4/13
 */
public interface SpaceUsageDetector {
    /**
     * 通过uri拿使用情况
     *
     * @param uri uri
     * @return 使用情况
     */
    SpaceUsage detect(URI uri);
}

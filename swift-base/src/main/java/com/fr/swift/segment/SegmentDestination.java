package com.fr.swift.segment;

import java.io.Serializable;
import java.net.URI;

/**
 * @author yee
 * @date 2018/6/13
 */
public interface SegmentDestination extends Serializable {
    boolean isRemote();

    String getNode();

    int order();

    URI getUri();
}

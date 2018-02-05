package com.fr.swift.cube.io.input;

import com.fr.swift.cube.io.Releasable;

/**
 * @author anchore
 */
public interface Reader extends Releasable {
    /**
     * 是否可读
     *
     * @return 是否可读
     */
    boolean isReadable();
}
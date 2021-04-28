package com.fr.swift.cloud.cube.io.impl.mem;

import com.fr.swift.cloud.cube.io.input.ObjectReader;
import com.fr.swift.cloud.cube.io.output.ObjectWriter;

/**
 * @author anchore
 * @date 2018/6/13
 */
public interface ObjectMemIo<T> extends ObjectWriter<T>, ObjectReader<T> {
}
package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.cube.io.input.ObjectReader;
import com.fr.swift.cube.io.output.ObjectWriter;

/**
 * @author anchore
 * @date 2018/6/13
 */
public interface ObjectMemIo<T> extends ObjectWriter<T>, ObjectReader<T> {
}
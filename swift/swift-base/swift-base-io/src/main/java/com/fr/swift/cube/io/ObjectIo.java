package com.fr.swift.cube.io;

import com.fr.swift.cube.io.input.ObjectReader;
import com.fr.swift.cube.io.output.ObjectWriter;

/**
 * @author anchore
 * @date 2018/7/20
 */
public interface ObjectIo<T> extends ObjectWriter<T>, ObjectReader<T> {
}
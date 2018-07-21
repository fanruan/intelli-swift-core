package com.fr.swift.io;

import com.fr.swift.cube.io.input.ObjectReader;
import com.fr.swift.cube.io.output.ObjectWriter;

/**
 * @author anchore
 * @date 2018/7/20
 */
public interface ObjectIo<W, R> extends ObjectWriter<W>, ObjectReader<R> {
}
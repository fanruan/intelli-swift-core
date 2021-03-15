package com.fr.swift.cloud.cube.io;

import com.fr.swift.cloud.cube.io.input.ObjectReader;
import com.fr.swift.cloud.cube.io.output.ObjectWriter;

/**
 * @author anchore
 * @date 2018/7/20
 */
public interface ObjectIo<T> extends Io, ObjectWriter<T>, ObjectReader<T> {
}
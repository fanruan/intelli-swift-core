package com.fr.swift.cube.io.input;

import com.fr.swift.cube.io.output.LongArrayWriter;
import com.fr.swift.structure.array.LongArray;

/**
 * @author yee
 * @date 2018/4/9
 */
public interface LongArrayReader extends ObjectReader<LongArray> {
    String CONTENT = LongArrayWriter.CONTENT;
    String POSITION = LongArrayWriter.POSITION;
    String LENGTH = LongArrayWriter.LENGTH;

    LongArray NULL_VALUE = LongArrayWriter.NULL_VALUE;
}

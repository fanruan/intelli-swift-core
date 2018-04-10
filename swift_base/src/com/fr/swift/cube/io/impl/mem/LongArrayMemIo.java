package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.cube.io.input.LongArrayReader;
import com.fr.swift.cube.io.output.LongArrayWriter;
import com.fr.swift.structure.array.LongArray;

/**
 * @author yee
 * @date 2018/4/9
 */
public class LongArrayMemIo extends BaseObjectMemIo<LongArray> implements LongArrayReader, LongArrayWriter {
    public LongArrayMemIo() {
        mem = new LongArray[DEFAULT_CAPACITY];
    }
}

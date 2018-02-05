package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.output.BitMapWriter;

/**
 * @author anchore
 * @date 2017/11/23
 */
public class BitMapMemIo extends BaseObjectMemIo<ImmutableBitMap> implements BitMapReader, BitMapWriter {
    public BitMapMemIo() {
        mem = new ImmutableBitMap[DEFAULT_CAPACITY];
    }
}

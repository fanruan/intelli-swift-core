package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.output.StringWriter;

/**
 * @author anchore
 * @date 2017/11/23
 */
public class StringMemIo extends BaseObjectMemIo<String> implements StringReader, StringWriter {
    public StringMemIo() {
        mem = new String[DEFAULT_CAPACITY];
    }
}

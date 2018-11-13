package com.fr.swift.cube.io.input;

import com.fr.swift.cube.io.output.StringWriter;

import java.nio.charset.Charset;

/**
 * @author anchore
 */
public interface StringReader extends ObjectReader<String> {

    Charset CHARSET = StringWriter.CHARSET;

}

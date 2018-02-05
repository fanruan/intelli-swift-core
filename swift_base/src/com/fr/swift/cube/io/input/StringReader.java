package com.fr.swift.cube.io.input;

import com.fr.swift.cube.io.output.StringWriter;

/**
 * @author anchore
 */
public interface StringReader extends ObjectReader<String> {

    String DEFAULT_CHARSET = StringWriter.DEFAULT_CHARSET;

}

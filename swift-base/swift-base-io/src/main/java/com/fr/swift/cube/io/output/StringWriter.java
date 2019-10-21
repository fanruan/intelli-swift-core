package com.fr.swift.cube.io.output;

import java.nio.charset.Charset;

/**
 * @author anchore
 */
public interface StringWriter extends ObjectWriter<String> {

    Charset CHARSET = Charset.forName("UTF-8");
}
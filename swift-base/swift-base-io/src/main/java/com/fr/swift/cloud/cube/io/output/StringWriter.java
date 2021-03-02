package com.fr.swift.cloud.cube.io.output;

import java.nio.charset.Charset;

/**
 * @author anchore
 */
public interface StringWriter extends ObjectWriter<String> {

    Charset CHARSET = Charset.forName("UTF-8");
}
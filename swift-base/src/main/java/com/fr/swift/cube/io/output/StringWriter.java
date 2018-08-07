package com.fr.swift.cube.io.output;

import com.fr.stable.StringUtils;

import java.nio.charset.Charset;

/**
 * @author anchore
 */
public interface StringWriter extends ObjectWriter<String> {

    Charset CHARSET = Charset.forName("UTF-8");

    String NULL_VALUE = StringUtils.EMPTY;

}

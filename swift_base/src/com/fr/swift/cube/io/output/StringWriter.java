package com.fr.swift.cube.io.output;

import com.fr.stable.StringUtils;

/**
 * @author anchore
 */
public interface StringWriter extends ObjectWriter<String> {

    String DEFAULT_CHARSET = "UTF-8";

    String NULL_VALUE = StringUtils.EMPTY;

}

package com.fr.swift.config.convert;

import com.fr.stable.db.entity.converter.BaseConverter;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FRURIConverter extends BaseConverter<URI, String> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(URI uri) {
        try {
            return mapper.writeValueAsString(uri);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public URI convertToEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? mapper.readValue(s, URI.class) : URI.create("0");
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}

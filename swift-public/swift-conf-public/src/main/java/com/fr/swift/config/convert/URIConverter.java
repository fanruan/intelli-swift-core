package com.fr.swift.config.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;

import javax.persistence.AttributeConverter;
import java.net.URI;

/**
 * @author yee
 * @date 2018-11-27
 */
public class URIConverter implements AttributeConverter<URI, String> {

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

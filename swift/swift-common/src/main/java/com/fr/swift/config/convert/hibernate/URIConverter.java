package com.fr.swift.config.convert.hibernate;

import com.fr.stable.StringUtils;
import com.fr.swift.util.Crasher;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.javax.persistence.AttributeConverter;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
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
            return StringUtils.isNotEmpty(s) ? mapper.readValue(s, URI.class) : URI.create("0");
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}

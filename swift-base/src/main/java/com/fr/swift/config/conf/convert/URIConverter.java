package com.fr.swift.config.conf.convert;

import com.fr.stable.StringUtils;
import com.fr.swift.util.Crasher;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.javax.persistence.AttributeConverter;

import java.io.IOException;
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

package com.fr.swift.config.convert;

import com.fr.swift.config.convert.hibernate.ConfigAttributeConverter;
import com.fr.swift.util.Crasher;

import java.net.URI;

/**
 * @author yee
 * @date 2018-11-27
 */
public class URIConverter implements ConfigAttributeConverter<URI, String> {

//    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(URI uri) {
        try {
            return "";
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public URI convertToEntityAttribute(String s) {
        try {
            return /*Strings.isNotEmpty(s) ? mapper.readValue(s, URI.class) :*/ URI.create("0");
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}

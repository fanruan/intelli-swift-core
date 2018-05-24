package com.fr.swift.config.conf.convert;

import com.fr.third.javax.persistence.AttributeConverter;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
 */
public class URIConverter implements AttributeConverter<URI, String> {
    @Override
    public String convertToDatabaseColumn(URI uri) {
        return uri.getPath();
    }

    @Override
    public URI convertToEntityAttribute(String s) {
        return URI.create(s);
    }
}

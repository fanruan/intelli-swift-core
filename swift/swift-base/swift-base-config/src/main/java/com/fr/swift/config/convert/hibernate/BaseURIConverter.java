package com.fr.swift.config.convert.hibernate;

import com.fr.swift.config.json.ConfigBeanMapper;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/24
 */
public abstract class BaseURIConverter implements ConfigAttributeConverter<URI, String> {
    private ConfigBeanMapper mapper;

    public BaseURIConverter(ConfigBeanMapper mapper) {
        this.mapper = mapper;
    }

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

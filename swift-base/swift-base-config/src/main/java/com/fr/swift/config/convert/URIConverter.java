package com.fr.swift.config.convert;

import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;

import java.net.URI;

/**
 * @author yee
 * @date 2018-11-27
 */
public class URIConverter implements ConfigAttributeConverter<URI, String> {

    private BeanMapper mapper;

    public URIConverter() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        mapper = (BeanMapper) Class.forName("com.fr.swift.bytebuddy.SwiftBeanMapper").newInstance();
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
            return Strings.isNotEmpty(s) ? mapper.string2Object(s, URI.class) : URI.create("0");
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}

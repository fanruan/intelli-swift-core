package com.fr.swift.config.convert;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;

import java.net.URI;

/**
 * @author yee
 * @date 2018-11-27
 */
public class URIConverter implements ConfigAttributeConverter<URI, String> {


    @Override
    public String convertToDatabaseColumn(URI uri) {
        try {
            return JsonBuilder.writeJsonString(uri);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public URI convertToEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? JsonBuilder.readValue(s, URI.class) : URI.create("0");
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}

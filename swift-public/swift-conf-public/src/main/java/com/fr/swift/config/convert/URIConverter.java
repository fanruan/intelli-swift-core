package com.fr.swift.config.convert;

import com.fr.swift.config.convert.hibernate.BaseURIConverter;
import com.fr.swift.config.json.DefaultBeanMapper;

import javax.persistence.AttributeConverter;
import java.net.URI;

/**
 * @author yee
 * @date 2018-11-27
 */
public class URIConverter extends BaseURIConverter implements AttributeConverter<URI, String> {
    public URIConverter() {
        super(DefaultBeanMapper.INSTANCE);
    }
}

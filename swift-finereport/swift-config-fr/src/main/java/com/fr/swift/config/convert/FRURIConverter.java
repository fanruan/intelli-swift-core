package com.fr.swift.config.convert;

import com.fr.swift.config.convert.hibernate.BaseURIConverter;
import com.fr.swift.config.json.FRBeanMapper;
import com.fr.third.javax.persistence.AttributeConverter;

import java.net.URI;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FRURIConverter extends BaseURIConverter implements AttributeConverter<URI, String> {
    public FRURIConverter() {
        super(FRBeanMapper.INSTANCE);
    }
}

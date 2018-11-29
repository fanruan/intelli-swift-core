package com.fr.swift.config.convert;

import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.convert.hibernate.BaseMetaDataColumnListConverter;
import com.fr.swift.config.json.ConfigBeanTypeReference;
import com.fr.swift.config.json.FRConfigBeanMapper;
import com.fr.third.fasterxml.jackson.core.type.TypeReference;
import com.fr.third.javax.persistence.AttributeConverter;

import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FRMetaDataColumnListConverter extends BaseMetaDataColumnListConverter implements AttributeConverter<List<MetaDataColumnBean>, String> {
    public FRMetaDataColumnListConverter() {
        super(FRConfigBeanMapper.INSTANCE);
    }

    @Override
    protected ConfigBeanTypeReference getConfigBeanTypeReference() {
        return new FRJacksonTypeReference<List<MetaDataColumnBean>>();
    }

    private class FRJacksonTypeReference<T> extends TypeReference<T> implements ConfigBeanTypeReference {
    }
}

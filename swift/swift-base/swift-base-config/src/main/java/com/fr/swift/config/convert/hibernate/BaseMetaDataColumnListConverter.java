package com.fr.swift.config.convert.hibernate;

import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.base.json.mapper.BeanTypeReference;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public abstract class BaseMetaDataColumnListConverter extends LongStringTypeConverter<List<MetaDataColumnBean>> {
    private BeanMapper mapper;

    public BaseMetaDataColumnListConverter(BeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected String toDatabaseColumn(List<MetaDataColumnBean> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return Strings.EMPTY;
        }
    }

    @Override
    public List<MetaDataColumnBean> toEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? (List<MetaDataColumnBean>) mapper.readValue(s, getConfigBeanTypeReference()) : new ArrayList<MetaDataColumnBean>();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    protected abstract BeanTypeReference getConfigBeanTypeReference();
}

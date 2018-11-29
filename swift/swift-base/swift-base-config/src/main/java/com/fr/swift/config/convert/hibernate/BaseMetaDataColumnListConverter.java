package com.fr.swift.config.convert.hibernate;

import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.json.ConfigBeanMapper;
import com.fr.swift.config.json.ConfigBeanTypeReference;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public abstract class BaseMetaDataColumnListConverter extends LongStringTypeConverter<List<MetaDataColumnBean>> {
    private ConfigBeanMapper mapper;

    public BaseMetaDataColumnListConverter(ConfigBeanMapper mapper) {
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

    protected abstract ConfigBeanTypeReference getConfigBeanTypeReference();
}

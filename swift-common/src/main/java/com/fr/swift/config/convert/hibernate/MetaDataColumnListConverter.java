package com.fr.swift.config.convert.hibernate;

import com.fr.stable.StringUtils;
import com.fr.stable.db.extra.converter.LongStringTypeConverter;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.core.type.TypeReference;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public class MetaDataColumnListConverter extends LongStringTypeConverter<List<MetaDataColumnBean>> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected String toDatabaseColumn(List<MetaDataColumnBean> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            SwiftLoggers.getLogger().error(e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public List<MetaDataColumnBean> toEntityAttribute(String s) {
        try {
            return StringUtils.isNotEmpty(s) ? (List<MetaDataColumnBean>) mapper.readValue(s, new TypeReference<List<MetaDataColumnBean>>() {
            }) : new ArrayList<MetaDataColumnBean>();
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}

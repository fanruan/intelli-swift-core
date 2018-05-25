package com.fr.swift.config.conf.convert;

import com.fr.stable.StringUtils;
import com.fr.swift.config.conf.bean.MetaDataColumnBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.core.type.TypeReference;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.javax.persistence.AttributeConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public class MetaDataColumnListConverter implements AttributeConverter<List<MetaDataColumnBean>, String> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<MetaDataColumnBean> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            SwiftLoggers.getLogger(MetaDataColumnListConverter.class).error(e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public List<MetaDataColumnBean> convertToEntityAttribute(String s) {
        try {
            return StringUtils.isNotEmpty(s) ? (List<MetaDataColumnBean>) mapper.readValue(s, new TypeReference<List<MetaDataColumnBean>>() {
            }) : new ArrayList<MetaDataColumnBean>();
        } catch (IOException e) {
            SwiftLoggers.getLogger(MetaDataColumnListConverter.class).error(e);
            return null;
        }
    }
}

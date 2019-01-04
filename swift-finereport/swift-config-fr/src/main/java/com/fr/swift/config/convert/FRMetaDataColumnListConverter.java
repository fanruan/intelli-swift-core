package com.fr.swift.config.convert;

import com.fr.stable.db.entity.converter.BaseConverter;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.util.Strings;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.core.type.TypeReference;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FRMetaDataColumnListConverter extends BaseConverter<List<MetaDataColumnBean>, String> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<MetaDataColumnBean> metaDataColumnBeans) {
        try {
            return objectMapper.writeValueAsString(metaDataColumnBeans);
        } catch (JsonProcessingException e) {
            return Strings.EMPTY;
        }
    }

    @Override
    public List<MetaDataColumnBean> convertToEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? (List<MetaDataColumnBean>) objectMapper.readValue(s, new TypeReference<List<MetaDataColumnBean>>() {
            }) : Collections.<MetaDataColumnBean>emptyList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}

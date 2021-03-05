package com.fr.swift.cloud.config.entity.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.cloud.config.entity.MetaDataColumnEntity;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.util.Strings;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public class MetaDataColumnListConverter implements AttributeConverter<List<MetaDataColumnEntity>, String> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<MetaDataColumnEntity> metaDataColumnEntities) {
        try {
            return objectMapper.writeValueAsString(metaDataColumnEntities);
        } catch (JsonProcessingException e) {
            SwiftLoggers.getLogger().error(e);
            return Strings.EMPTY;

        }
    }

    @Override
    public List<MetaDataColumnEntity> convertToEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? objectMapper.readValue(s, new TypeReference<List<MetaDataColumnEntity>>() {
            }) : new ArrayList<>();
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}

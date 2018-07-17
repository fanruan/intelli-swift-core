package com.fr.swift.config.indexing.impl;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.javax.persistence.AttributeConverter;

import java.io.IOException;

/**
 * @author anchore
 * @date 2018/7/16
 */
public class AllotRuleConverter implements AttributeConverter<AllotRule, String> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AllotRule allotRule) {
        try {
            return mapper.writeValueAsString(allotRule);
        } catch (JsonProcessingException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public AllotRule convertToEntityAttribute(String s) {
        try {
            return mapper.readValue(s, AllotRule.class);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}
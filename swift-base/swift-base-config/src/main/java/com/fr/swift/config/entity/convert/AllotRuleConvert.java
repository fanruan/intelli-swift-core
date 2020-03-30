package com.fr.swift.config.entity.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.util.Strings;

import javax.persistence.AttributeConverter;
import java.io.IOException;

/**
 * @author lucifer
 * @date 2020/3/14
 * @description
 * @since swift-log 10.0.5
 */
public class AllotRuleConvert implements AttributeConverter<AllotRule, String> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AllotRule allotRule) {
        try {
            return objectMapper.writeValueAsString(allotRule);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return Strings.EMPTY;
        }
    }

    @Override
    public AllotRule convertToEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? objectMapper.readValue(s, AllotRule.class) : new LineAllotRule();
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}

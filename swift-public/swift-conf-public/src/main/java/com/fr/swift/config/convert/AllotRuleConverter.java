package com.fr.swift.config.convert;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.util.Strings;

import javax.persistence.AttributeConverter;

/**
 * @author yee
 * @date 2018-11-27
 */
public class AllotRuleConverter implements AttributeConverter<AllotRule, String> {
    @Override
    public String convertToDatabaseColumn(AllotRule allotRule) {
        try {
            return JsonBuilder.writeJsonString(allotRule);
        } catch (Exception e) {
            return Strings.EMPTY;
        }
    }

    @Override
    public AllotRule convertToEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? JsonBuilder.readValue(s, AllotRule.class) : null;
        } catch (Exception e) {
            return null;
        }
    }
}

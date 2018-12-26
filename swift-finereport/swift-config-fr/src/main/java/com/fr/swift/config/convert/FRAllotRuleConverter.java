package com.fr.swift.config.convert;

import com.fr.stable.db.entity.converter.BaseConverter;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.util.Strings;

/**
 * @author yee
 * @date 2018-11-27
 */
public class FRAllotRuleConverter extends BaseConverter<AllotRule, String> {
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

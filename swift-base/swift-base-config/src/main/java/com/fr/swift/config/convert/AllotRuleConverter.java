package com.fr.swift.config.convert;

import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.util.Strings;

/**
 * @author yee
 * @date 2018-11-27
 */
public class AllotRuleConverter implements ConfigAttributeConverter<AllotRule, String> {
    private BeanMapper mapper;

    public AllotRuleConverter() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        mapper = (BeanMapper) Class.forName("com.fr.swift.bytebuddy.SwiftBeanMapper").newInstance();
    }

    @Override
    public String convertToDatabaseColumn(AllotRule allotRule) {
        try {
            return mapper.writeValueAsString(allotRule);
        } catch (Exception e) {
            return Strings.EMPTY;
        }
    }

    @Override
    public AllotRule convertToEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? mapper.string2Object(s, AllotRule.class) : null;
        } catch (Exception e) {
            return null;
        }
    }
}

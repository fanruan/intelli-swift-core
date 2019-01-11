package com.fr.swift.config.convert;

import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2019-01-08
 */
public class AllotRuleConverterTest {
    private AllotRule allotRule = new LineAllotRule(100);
    private String allotString = "{\"capacity\":100}";

    @Test
    public void convertToDatabaseColumn() {
        String allotColumn = new AllotRuleConverter().convertToDatabaseColumn(allotRule);
        assertEquals(allotString, allotColumn);
    }

    @Test
    public void convertToEntityAttribute() {
        AllotRule allotRule = new AllotRuleConverter().convertToEntityAttribute(allotString);
        assertEquals(this.allotRule.getType(), allotRule.getType());
        assertEquals(this.allotRule.getCapacity(), allotRule.getCapacity());
    }
}
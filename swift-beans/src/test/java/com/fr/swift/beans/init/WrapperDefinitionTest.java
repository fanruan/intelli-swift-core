package com.fr.swift.beans.init;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.init.WrapperDefinition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class WrapperDefinitionTest {

    public WrapperDefinition definition1;
    public WrapperDefinition definition2;
    public WrapperDefinition definition3;

    @Before
    public void before() {
        definition1 = new WrapperDefinition(new SwiftBeanDefinition(WrapperDefinitionTest.class, "test"), 0);
        definition2 = new WrapperDefinition(new SwiftBeanDefinition(WrapperDefinitionTest.class, "test"), 0);
        definition3 = new WrapperDefinition(new SwiftBeanDefinition(WrapperDefinitionTest.class, "testHashEquals"), 0);
    }

    @Test
    public void testHashEquals() {
        assertEquals(definition1, definition2);
        assertEquals(definition1.hashCode(), definition2.hashCode());

        assertNotEquals(definition1, definition3);
        assertNotEquals(definition1.hashCode(), definition3.hashCode());
    }
}

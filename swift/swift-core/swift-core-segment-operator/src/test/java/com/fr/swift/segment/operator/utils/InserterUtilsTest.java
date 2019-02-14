package com.fr.swift.segment.operator.utils;

import com.fr.swift.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/15
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Strings.class})
public class InserterUtilsTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(Strings.class);
        when(Strings.isBlank(anyString())).thenReturn(true, false);
    }

    @Test
    public void isBusinessNullValue() {
        assertTrue(InserterUtils.isBusinessNullValue(null));
        assertFalse(InserterUtils.isBusinessNullValue(1));
        assertTrue(InserterUtils.isBusinessNullValue(anyString()));
        assertFalse(InserterUtils.isBusinessNullValue(anyString()));
    }
}
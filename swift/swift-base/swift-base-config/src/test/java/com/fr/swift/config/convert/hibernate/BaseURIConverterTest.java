package com.fr.swift.config.convert.hibernate;

import com.fr.swift.config.json.ConfigBeanMapper;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-29
 */
public class BaseURIConverterTest {
    private URI testUri = URI.create("testUri");
    private URI testExp = URI.create("testExp");

    @Test
    public void convertToDatabaseColumn() throws Exception {
        ConfigBeanMapper mockConfigBeanMapper = PowerMock.createMock(ConfigBeanMapper.class);
        EasyMock.expect(mockConfigBeanMapper.writeValueAsString(EasyMock.notNull())).andReturn(testUri.getPath()).anyTimes();
        EasyMock.expect(mockConfigBeanMapper.writeValueAsString(EasyMock.isNull())).andThrow(new Exception()).anyTimes();
        BaseURIConverter converter = PowerMock.createMock(BaseURIConverter.class, mockConfigBeanMapper);
        PowerMock.replayAll();
        assertEquals(converter.convertToDatabaseColumn(testUri), testUri.getPath());
        boolean testException = false;
        try {
            converter.convertToDatabaseColumn(null);
        } catch (Exception e) {
            testException = true;
        }
        assertTrue(testException);
        PowerMock.verifyAll();
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        ConfigBeanMapper mockConfigBeanMapper = PowerMock.createMock(ConfigBeanMapper.class);
        EasyMock.expect(mockConfigBeanMapper.readValue(EasyMock.eq(testUri.getPath()), EasyMock.eq(URI.class))).andReturn(testUri).anyTimes();
        EasyMock.expect(mockConfigBeanMapper.readValue(EasyMock.eq(testExp.getPath()), EasyMock.eq(URI.class))).andThrow(new Exception()).anyTimes();
        BaseURIConverter converter = PowerMock.createMock(BaseURIConverter.class, mockConfigBeanMapper);
        PowerMock.replayAll();
        URI rule = converter.convertToEntityAttribute(testUri.getPath());
        assertEquals(rule, testUri);
        boolean testException = false;
        try {
            converter.convertToEntityAttribute(testExp.getPath());
        } catch (Exception e) {
            testException = true;
        }
        assertTrue(testException);
        PowerMock.verifyAll();
    }
}
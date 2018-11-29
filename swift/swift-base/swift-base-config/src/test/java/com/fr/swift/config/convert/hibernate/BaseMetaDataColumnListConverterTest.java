package com.fr.swift.config.convert.hibernate;

import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.json.ConfigBeanMapper;
import com.fr.swift.config.json.ConfigBeanTypeReference;
import com.fr.swift.util.Strings;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author yee
 * @date 2018-11-29
 */
public class BaseMetaDataColumnListConverterTest {
    private String writeString = "testString";

    @Test
    public void toDatabaseColumn() throws Exception {
        ConfigBeanMapper mockConfigBeanMapper = PowerMock.createMock(ConfigBeanMapper.class);
        EasyMock.expect(mockConfigBeanMapper.writeValueAsString(EasyMock.notNull())).andReturn(writeString).anyTimes();
        EasyMock.expect(mockConfigBeanMapper.writeValueAsString(EasyMock.isNull())).andThrow(new Exception("Just Test Exception")).anyTimes();
        BaseMetaDataColumnListConverter converter = PowerMock.createMock(BaseMetaDataColumnListConverter.class, mockConfigBeanMapper);
        PowerMock.replayAll();
        assertEquals(converter.convertToDatabaseColumn(new ArrayList<MetaDataColumnBean>()), writeString);
        assertEquals(Strings.EMPTY, converter.convertToDatabaseColumn(null));
        PowerMock.verifyAll();
    }

    @Test
    public void toEntityAttribute() throws Exception {
        ConfigBeanMapper mockConfigBeanMapper = PowerMock.createMock(ConfigBeanMapper.class);
        EasyMock.expect(mockConfigBeanMapper.readValue(EasyMock.eq(writeString), EasyMock.isA(ConfigBeanTypeReference.class))).andReturn(new ArrayList<MetaDataColumnBean>()).anyTimes();
        EasyMock.expect(mockConfigBeanMapper.readValue(EasyMock.eq("testException"), EasyMock.isA(ConfigBeanTypeReference.class))).andThrow(new Exception("Just Test Exception")).anyTimes();
        BaseMetaDataColumnListConverter converter = PowerMock.createMock(BaseMetaDataColumnListConverter.class, mockConfigBeanMapper);
        ConfigBeanTypeReference mockConfigBeanTypeReference = PowerMock.createMock(ConfigBeanTypeReference.class);
        EasyMock.expect(converter.getConfigBeanTypeReference()).andReturn(mockConfigBeanTypeReference).anyTimes();
        PowerMock.replayAll();
        List<MetaDataColumnBean> rule = converter.convertToEntityAttribute(writeString);
        assertNotNull(rule);
        rule = converter.convertToEntityAttribute("testException");
        assertNull(rule);
        PowerMock.verifyAll();
    }
}
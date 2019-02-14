package com.fr.swift.config.convert;

import com.fr.swift.base.meta.MetaDataColumnBean;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * @author yee
 * @date 2019-01-08
 */
public class MetaDataColumnListConverterTest {

    private List<MetaDataColumnBean> columnBeans;
    private String json = "[{\"type\":-5,\"name\":\"columnA\",\"remark\":null,\"precision\":255,\"scale\":15,\"columnId\":\"columnA\"},{\"type\":12,\"name\":\"columnB\",\"remark\":null,\"precision\":255,\"scale\":15,\"columnId\":\"columnB\"},{\"type\":12,\"name\":\"columnC\",\"remark\":null,\"precision\":255,\"scale\":15,\"columnId\":\"columnC\"}]";

    @Before
    public void setUp() throws Exception {
        columnBeans = new ArrayList<MetaDataColumnBean>();
        columnBeans.add(new MetaDataColumnBean("columnA", Types.BIGINT));
        columnBeans.add(new MetaDataColumnBean("columnB", Types.VARCHAR));
        columnBeans.add(new MetaDataColumnBean("columnC", Types.VARCHAR));
    }

    @Test
    public void toDatabaseColumn() {
        String column = new MetaDataColumnListConverter().toDatabaseColumn(columnBeans);
        assertEquals(json, column);
    }

    @Test
    public void toEntityAttribute() {
        List<MetaDataColumnBean> beans = new MetaDataColumnListConverter().toEntityAttribute(json);
        assertEquals(columnBeans.size(), beans.size());
        for (int i = 0; i < columnBeans.size(); i++) {
            assertEquals(columnBeans.get(i).toString(), beans.get(i).toString());
        }
    }
}
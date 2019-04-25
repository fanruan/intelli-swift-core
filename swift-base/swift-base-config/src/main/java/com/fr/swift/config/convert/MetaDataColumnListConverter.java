package com.fr.swift.config.convert;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.config.convert.hibernate.ConfigAttributeConverter;
import com.fr.swift.config.convert.hibernate.LongStringTypeConverter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Strings;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public class MetaDataColumnListConverter extends LongStringTypeConverter<List<MetaDataColumnBean>> implements ConfigAttributeConverter<List<MetaDataColumnBean>, String> {
//    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected String toDatabaseColumn(List<MetaDataColumnBean> list) {
        try {
//            return mapper.writeValueAsString(list);
            return Strings.EMPTY;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return Strings.EMPTY;
        }
    }

    @Override
    public List<MetaDataColumnBean> toEntityAttribute(String s) {
        try {
            return /*Strings.isNotEmpty(s) ? (List<MetaDataColumnBean>) mapper.readValue(s, new TypeReference<List<MetaDataColumnBean>>() {
            }) : */Collections.emptyList();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return Collections.emptyList();
        }
    }
}

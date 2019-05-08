package com.fr.swift.config.convert;

import com.fr.swift.base.json.mapper.BeanMapper;
import com.fr.swift.base.json.mapper.BeanTypeReference;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.config.db.util.DBStringUtil;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Strings;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public class MetaDataColumnListConverter implements ConfigAttributeConverter<List<MetaDataColumnBean>, String> {
    private BeanMapper mapper;

    public MetaDataColumnListConverter() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        mapper = (BeanMapper) Class.forName("com.fr.swift.bytebuddy.SwiftBeanMapper").newInstance();
    }

    @Override
    public String convertToDatabaseColumn(List<MetaDataColumnBean> var1) {
        String var2 = this.toDatabaseColumn(var1);
        var2 = this.convertForOracle9i(var2);
        return var2;
    }

    @Override
    public List<MetaDataColumnBean> convertToEntityAttribute(String var1) {
        String var2 = this.recoverForOracle9i(var1);
        return this.toEntityAttribute(var2);
    }

    private String convertForOracle9i(String var1) {
        return DBStringUtil.dealWithClobStringLengthForOracle9i(var1);
    }

    private String recoverForOracle9i(String var1) {
        if (var1 != null && var1.length() == DBStringUtil.FILL_SIZE_FOR_ORACLE_9i) {
            var1 = var1.trim();
        }

        return var1;
    }
    protected String toDatabaseColumn(List<MetaDataColumnBean> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return Strings.EMPTY;
        }
    }

    public List<MetaDataColumnBean> toEntityAttribute(String s) {
        try {
            return Strings.isNotEmpty(s) ? mapper.string2TypeReference(s, new BeanTypeReference<List<MetaDataColumnBean>>() {
            }) : Collections.<MetaDataColumnBean>emptyList();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return Collections.emptyList();
        }
    }
}

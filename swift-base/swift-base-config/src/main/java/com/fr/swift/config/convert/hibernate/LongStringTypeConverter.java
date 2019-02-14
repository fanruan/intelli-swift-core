package com.fr.swift.config.convert.hibernate;

import com.fr.swift.config.db.util.DBStringUtil;

/**
 * @author yee
 * @date 2018-11-27
 */
public abstract class LongStringTypeConverter<T> implements ConfigAttributeConverter<T, String> {
    public LongStringTypeConverter() {
    }

    protected abstract String toDatabaseColumn(T var1);

    protected abstract T toEntityAttribute(String var1);

    @Override
    public String convertToDatabaseColumn(T var1) {
        String var2 = this.toDatabaseColumn(var1);
        var2 = this.convertForOracle9i(var2);
        return var2;
    }

    @Override
    public T convertToEntityAttribute(String var1) {
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
}
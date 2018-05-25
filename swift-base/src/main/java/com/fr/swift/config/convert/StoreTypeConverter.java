package com.fr.swift.config.convert;

import com.fr.swift.cube.io.Types;
import com.fr.third.javax.persistence.AttributeConverter;

/**
 * @author yee
 * @date 2018/5/24
 */
public class StoreTypeConverter implements AttributeConverter<Types.StoreType, String> {
    @Override
    public String convertToDatabaseColumn(Types.StoreType storeType) {
        return storeType.name();
    }

    @Override
    public Types.StoreType convertToEntityAttribute(String s) {
        return Types.StoreType.valueOf(s);
    }
}

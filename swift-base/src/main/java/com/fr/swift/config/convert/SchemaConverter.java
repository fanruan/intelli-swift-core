package com.fr.swift.config.convert;

import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.third.javax.persistence.AttributeConverter;

/**
 * @author anchore
 * @date 2018/6/20
 */
public class SchemaConverter implements AttributeConverter<Schema, String> {
    @Override
    public String convertToDatabaseColumn(Schema schema) {
        return schema.toString();
    }

    @Override
    public Schema convertToEntityAttribute(String schema) {
        return Schema.valueOf(schema);
    }
}
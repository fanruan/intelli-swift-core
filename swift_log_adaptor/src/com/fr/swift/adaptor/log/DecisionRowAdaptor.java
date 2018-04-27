package com.fr.swift.adaptor.log;

import com.fr.swift.adaptor.log.Converters.ReverseDatumConverter;
import com.fr.swift.source.Row;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.function.Function;
import com.fr.swift.util.function.UnaryOperator;
import com.fr.third.javax.persistence.AttributeConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/4/27
 * <p>
 * swift row -> fr log
 */
public class DecisionRowAdaptor implements Function<Row, Object> {
    private Class<?> table;
    private Map<Field, UnaryOperator<Object>> converters = new LinkedHashMap<Field, UnaryOperator<Object>>();

    DecisionRowAdaptor(Class<?> table) throws Exception {
        this.table = table;
        init();
    }

    private void init() throws Exception {
        for (Field field : table.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            field.setAccessible(true);
            if (field.isAnnotationPresent(Convert.class)) {
                AttributeConverter<Object, Object> converter = (AttributeConverter<Object, Object>) field.getAnnotation(Convert.class).converter().newInstance();
                UnaryOperator<Object> baseConverter = Converters.getReverseConverter(SwiftMetaAdaptor.getClassType(field));
                converters.put(field, new ReverseDatumConverter(converter, baseConverter));
            } else {
                converters.put(field, Converters.getReverseConverter((field.getType())));
            }
        }
    }

    @Override
    public Object apply(Row row) {
        try {
            Object data = table.newInstance();
            int index = 0;
            for (Entry<Field, UnaryOperator<Object>> entry : converters.entrySet()) {
                entry.getKey().set(data, entry.getValue().apply(row.getValue(index++)));
            }
            return data;
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}
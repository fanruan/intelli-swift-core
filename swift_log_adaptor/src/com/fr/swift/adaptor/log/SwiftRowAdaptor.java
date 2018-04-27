package com.fr.swift.adaptor.log;

import com.fr.swift.adaptor.log.Converters.DatumConverter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.function.Function;
import com.fr.swift.util.function.UnaryOperator;
import com.fr.third.javax.persistence.AttributeConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/4/26
 *
 * fr log -> swift row
 */
public class SwiftRowAdaptor implements Function<Object, Row> {
    private Map<Field, UnaryOperator<Object>> converters = new LinkedHashMap<Field, UnaryOperator<Object>>();

    SwiftRowAdaptor(Class<?> table) throws Exception {
        init(table.getDeclaredFields());
    }

    private void init(Field[] fields) throws Exception {
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            field.setAccessible(true);
            if (field.isAnnotationPresent(Convert.class)) {
                AttributeConverter<Object, Object> converter = (AttributeConverter<Object, Object>) field.getAnnotation(Convert.class).converter().newInstance();
                UnaryOperator<Object> baseConverter = Converters.getConverter(SwiftMetaAdaptor.getClassType(field));
                converters.put(field, new DatumConverter(converter, baseConverter));
            } else {
                converters.put(field, Converters.getConverter(field.getType()));
            }
        }
    }

    @Override
    public Row apply(Object data) {
        List<Object> row = new ArrayList<Object>();
        try {
            for (Entry<Field, UnaryOperator<Object>> entry : converters.entrySet()) {
                row.add(entry.getValue().apply(entry.getKey().get(data)));
            }
            return new ListBasedRow(row);
        } catch (IllegalAccessException e) {
            return Crasher.crash(e);
        }
    }

}
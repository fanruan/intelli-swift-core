package com.fr.swift.adaptor.log;

import com.fr.swift.adaptor.log.DatumConverters.DatumConverter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.JpaAdaptor;
import com.fr.swift.util.function.Function;
import com.fr.swift.util.function.UnaryOperator;
import com.fr.third.javax.persistence.AttributeConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/4/26
 * <p>
 * fr log -> swift row
 */
public class SwiftRowAdaptor<T> implements Function<T, Row> {
    /**
     * columnIndex -> (field, converter)
     */
    private Map<Integer, Pair<Field, UnaryOperator<Object>>> converters = new TreeMap<Integer, Pair<Field, UnaryOperator<Object>>>();

    SwiftRowAdaptor(Class<T> entity, SwiftMetaData meta) throws Exception {
        init(JpaAdaptor.getFields(entity), meta);
    }

    private void init(List<Field> fields, SwiftMetaData meta) throws Exception {
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            field.setAccessible(true);
            String columnName = field.getAnnotation(Column.class).name();
            int columnIndex = meta.getColumnIndex(columnName);
            if (field.isAnnotationPresent(Convert.class)) {
                AttributeConverter<Object, Object> converter = (AttributeConverter<Object, Object>) field.getAnnotation(Convert.class).converter().newInstance();
                UnaryOperator<Object> baseConverter = DatumConverters.getConverter(JpaAdaptor.getClassType(field));
                converters.put(columnIndex, Pair.of(field, (UnaryOperator<Object>) new DatumConverter(converter, baseConverter)));
            } else {
                converters.put(columnIndex, Pair.of(field, DatumConverters.getConverter(field.getType())));
            }
        }
    }

    @Override
    public Row apply(T data) {
        List<Object> row = new ArrayList<Object>();
        try {
            for (Entry<Integer, Pair<Field, UnaryOperator<Object>>> entry : converters.entrySet()) {
                Pair<Field, UnaryOperator<Object>> pair = entry.getValue();
                row.add(pair.getValue().apply(pair.getKey().get(data)));
            }
            return new ListBasedRow(row);
        } catch (IllegalAccessException e) {
            return Crasher.crash(e);
        }
    }
}
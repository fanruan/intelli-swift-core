package com.fr.swift.adaptor.log;

import com.fr.swift.adaptor.log.DatumConverters.ReverseDatumConverter;
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
import com.fr.third.org.objenesis.ObjenesisHelper;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/4/27
 * <p>
 * swift row -> fr log
 */
public class DecisionRowAdaptor<T> implements Function<Row, T> {
    private Class<T> entity;

    /**
     * columnIndex -> (field, converter)
     */
    private Map<Integer, Pair<Field, UnaryOperator<Object>>> converters = new TreeMap<Integer, Pair<Field, UnaryOperator<Object>>>();

    DecisionRowAdaptor(Class<T> entity, SwiftMetaData meta) throws Exception {
        this.entity = entity;
        init(meta);
    }

    private void init(SwiftMetaData meta) throws Exception {
        for (Field field : JpaAdaptor.getFields(entity)) {
            field.setAccessible(true);
            String columnName = field.getAnnotation(Column.class).name();
            int columnIndex = meta.getColumnIndex(columnName);
            if (!field.isAnnotationPresent(Convert.class)) {
                converters.put(columnIndex, Pair.of(field, DatumConverters.getReverseConverter(field.getType())));
            } else {
                AttributeConverter<Object, Object> converter = (AttributeConverter<Object, Object>) field.getAnnotation(Convert.class).converter().newInstance();
                UnaryOperator<Object> baseConverter = DatumConverters.getReverseConverter(JpaAdaptor.getClassType(field));
                converters.put(columnIndex, Pair.of(field, (UnaryOperator<Object>) new ReverseDatumConverter(converter, baseConverter)));
            }
        }
    }

    @Override
    public T apply(Row row) {
        try {
            T data = ObjenesisHelper.newInstance(entity);
            for (Entry<Integer, Pair<Field, UnaryOperator<Object>>> entry : converters.entrySet()) {
                Pair<Field, UnaryOperator<Object>> pair = entry.getValue();
                pair.getKey().set(data, pair.getValue().apply(row.getValue(entry.getKey() - 1)));
            }
            return data;
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}
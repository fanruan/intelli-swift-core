package com.fr.swift.util;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;
import com.fr.third.javax.persistence.MappedSuperclass;
import com.fr.third.javax.persistence.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/4/25
 */
public class JpaAdaptor {
    public static SwiftMetaData adapt(Class<?> entity) {
        return adapt(entity, SwiftDatabase.CUBE);
    }

    public static SwiftMetaData adapt(Class<?> entity, SwiftDatabase swiftSchema) {
        List<String> columnNames = new ArrayList<String>();
        List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>();
        for (Field field : getFields(entity)) {
            Column column = field.getAnnotation(Column.class);
            String columnName = column.name();

            Assert.hasText(columnName);

            if (columnNames.contains(columnName)) {
                return Crasher.crash(String.format("column %s already existed", columnName));
            }

            if (SwiftConfigConstants.KeyWords.COLUMN_KEY_WORDS.contains(columnName.toLowerCase())) {
                return Crasher.crash(String.format("%s is a key word", columnName));
            }

            Class<?> classType = getClassType(field);
            SwiftMetaDataColumn columnMeta = new MetaDataColumnBean(
                    columnName,
                    null,
                    ClassToSql.getSqlType(classType),
                    ClassToSql.getPrecision(classType, column.precision()),
                    ClassToSql.getScale(classType, column.scale())
            );
            columnNames.add(columnName);
            columnMetas.add(columnMeta);
        }
        return new SwiftMetaDataBean(swiftSchema, getTableName(entity), columnMetas);
    }

    public static List<Field> getFields(Class<?> entity) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> superEntity = entity.getSuperclass();
        if (superEntity != null && superEntity.isAnnotationPresent(MappedSuperclass.class)) {
            fields.addAll(getFields(superEntity));
        }
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public static String getTableName(Class<?> entity) {
        String tableName = entity.getAnnotation(Table.class).name();
        Assert.hasText(tableName);
        return tableName;
    }

    public static Class<?> getClassType(Field field) {
        if (!field.isAnnotationPresent(Convert.class)) {
            return field.getType();
        }

        Class converter = field.getAnnotation(Convert.class).converter();
        List<Method> methods = new ArrayList<Method>();
        for (Method method : converter.getDeclaredMethods()) {
            if (method.getName().contains("convertToDatabaseColumn")) {
                methods.add(method);
            }
        }
        Assert.notEmpty(methods);

        if (methods.size() == 1) {
            return methods.get(0).getReturnType();
        }
        Collections.sort(methods, MethodCmp.INSTANCE);
        return methods.get(methods.size() - 1).getReturnType();
    }

    enum MethodCmp implements Comparator<Method> {
        //
        INSTANCE;

        @Override
        public int compare(Method o1, Method o2) {
            int cmp = getInheritDepth(o1.getParameterTypes()[0]) - getInheritDepth(o2.getParameterTypes()[0]);
            return cmp == 0
                    ? getInheritDepth(o1.getReturnType()) - getInheritDepth(o2.getReturnType())
                    : cmp;
        }

        private static int getInheritDepth(Class<?> klass) {
            Class<?> superclass = klass.getSuperclass();
            if (superclass == null) {
                return 1;
            }
            return getInheritDepth(superclass) + 1;
        }
    }

    public static int getSqlType(Class<?> field) {
        return ClassToSql.getSqlType(field);
    }

    public static int getStoreSqlType(Class<?> field) {
        switch (ClassToSql.getSqlType(field)) {
            case Types.BIT:
            case Types.BOOLEAN:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                return Types.BIGINT;
            case Types.FLOAT:
            case Types.DOUBLE:
                return Types.DOUBLE;
            case Types.CHAR:
            case Types.VARCHAR:
                return Types.VARCHAR;
            case Types.DATE:
                return Types.DATE;
            default:
                return Crasher.crash(String.format("type unsupported: %s", field));
        }
    }
}

class ClassToSql {

    private static final Map<Class<?>, SqlMeta> CLASS_TO_SQL = new HashMap<Class<?>, SqlMeta>();

    static {
        CLASS_TO_SQL.put(Boolean.class, new SqlMeta(Types.BOOLEAN, 1));
        CLASS_TO_SQL.put(boolean.class, new SqlMeta(Types.BIT, 1));
        CLASS_TO_SQL.put(byte.class, new SqlMeta(Types.TINYINT, 3));
        CLASS_TO_SQL.put(Byte.class, new SqlMeta(Types.TINYINT, 3));
        CLASS_TO_SQL.put(short.class, new SqlMeta(Types.SMALLINT, 5));
        CLASS_TO_SQL.put(Short.class, new SqlMeta(Types.SMALLINT, 5));
        CLASS_TO_SQL.put(int.class, new SqlMeta(Types.INTEGER, 10));
        CLASS_TO_SQL.put(Integer.class, new SqlMeta(Types.INTEGER, 10));
        CLASS_TO_SQL.put(long.class, new SqlMeta(Types.BIGINT, 19));
        CLASS_TO_SQL.put(Long.class, new SqlMeta(Types.BIGINT, 19));
        CLASS_TO_SQL.put(float.class, new SqlMeta(Types.FLOAT, 22, Optional.of(15)));
        CLASS_TO_SQL.put(Float.class, new SqlMeta(Types.FLOAT, 22, Optional.of(15)));
        CLASS_TO_SQL.put(double.class, new SqlMeta(Types.DOUBLE, 22, Optional.of(15)));
        CLASS_TO_SQL.put(Double.class, new SqlMeta(Types.DOUBLE, 22, Optional.of(15)));
        CLASS_TO_SQL.put(char.class, new SqlMeta(Types.CHAR, 1));
        CLASS_TO_SQL.put(Character.class, new SqlMeta(Types.CHAR, 1));
        CLASS_TO_SQL.put(String.class, new SqlMeta(Types.VARCHAR, 255));
        CLASS_TO_SQL.put(Date.class, new SqlMeta(Types.DATE, 19));
        CLASS_TO_SQL.put(java.sql.Date.class, new SqlMeta(Types.DATE, 19));
        CLASS_TO_SQL.put(Time.class, new SqlMeta(Types.TIME, 8));
        CLASS_TO_SQL.put(Timestamp.class, new SqlMeta(Types.TIMESTAMP, 19));
    }

    static int getSqlType(Class<?> field) {
        if (CLASS_TO_SQL.containsKey(field)) {
            return CLASS_TO_SQL.get(field).getSqlType();
        }
        return Crasher.crash(String.format("type unsupported: %s", field));
    }

    static int getPrecision(Class<?> field, int precision) {
        if (CLASS_TO_SQL.containsKey(field)) {
            return precision == 0 ? CLASS_TO_SQL.get(field).getPrecision() : precision;
        }
        return Crasher.crash(String.format("type unsupported: %s", field));
    }

    static int getScale(Class<?> field, int scale) {
        if (CLASS_TO_SQL.containsKey(field)) {
            Optional<Integer> dftScale = CLASS_TO_SQL.get(field).getScale();
            if (dftScale.isPresent() && scale == 0) {
                return dftScale.get();
            }
            return scale;
        }
        return Crasher.crash(String.format("type unsupported: %s", field));
    }


    static class SqlMeta {

        int sqlType;

        int precision;

        Optional<Integer> scale;

        SqlMeta(int sqlType, int precision, Optional<Integer> scale) {
            this.sqlType = sqlType;
            this.precision = precision;
            this.scale = scale;
        }

        SqlMeta(int sqlType, int precision) {
            this(sqlType, precision, Optional.<Integer>empty());
        }

        int getSqlType() {
            return sqlType;
        }

        int getPrecision() {
            return precision;
        }

        Optional<Integer> getScale() {
            return scale;
        }
    }
}
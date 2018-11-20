package com.fr.swift.jdbc.json;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.info.SqlInfo;
import com.fr.swift.jdbc.json.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface JsonRequestBuilder {
    JsonRequestBuilder INSTANCE = new JsonRequestBuilder() {

        private Map<Class, Class> primitiveToWrapper = new HashMap<Class, Class>();

        {
            primitiveToWrapper.put(Integer.TYPE, Integer.class);
            primitiveToWrapper.put(Boolean.TYPE, Boolean.class);
            primitiveToWrapper.put(Character.TYPE, Character.class);
            primitiveToWrapper.put(Long.TYPE, Long.class);
            primitiveToWrapper.put(Short.TYPE, Short.class);
            primitiveToWrapper.put(Double.TYPE, Double.class);
            primitiveToWrapper.put(Float.TYPE, Float.class);
            primitiveToWrapper.put(Byte.TYPE, Byte.class);
        }

        @Override
        public String buildSqlRequest(SqlInfo sql) {
            StringBuffer buffer = new StringBuffer("{");
            buildClassFieldJson(buffer, sql.getClass(), sql);
            buffer.append("\"requestId\": \"").append(UUID.randomUUID()).append("\"");
            buffer.append("}");
            return buffer.toString();
        }

        @Override
        public String buildAuthRequest(String user, String password) {
            return String.format("{\"requestType\": \"AUTH\",\"swiftUser\": \"%s\",\"swiftPassword\": \"%s\",\"requestId\": \"%s\"}", user, password, UUID.randomUUID());
        }

        private void buildClassFieldJson(StringBuffer buffer, Class clazz, Object o) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonProperty.class)) {
                    JsonProperty property = field.getAnnotation(JsonProperty.class);
                    String propertyName = property.value();
                    try {
                        field.setAccessible(true);
                        Object obj = field.get(o);
                        if (null != obj) {
                            buffer.append("\"").append(propertyName).append("\": ").append(getString(obj)).append(",");
                        } else if (property.require()) {
                            throw Exceptions.runtime(String.format("%s is require", propertyName));
                        }
                    } catch (IllegalAccessException ignore) {
                    }
                }
            }
            if (null != clazz.getSuperclass()) {
                buildClassFieldJson(buffer, clazz.getSuperclass(), o);
            }
        }

        /**
         * @param object
         * @return
         */
        private String getString(Object object) {
            if (object instanceof Date) {
                return String.valueOf(((Date) object).getTime());
            }
            Class clazz = object.getClass();
            if (primitiveToWrapper.containsKey(clazz) || primitiveToWrapper.containsValue(clazz)) {
                return object.toString();
            }
            StringBuffer buffer = new StringBuffer();
            if (object instanceof Collection) {
                buffer.append("[");
                for (Object o : ((Collection) object)) {
                    buffer.append(getString(o)).append(",");
                }
                buffer.setLength(buffer.length() - 1);
                buffer.append("]");
                return buffer.toString();
            }
            if (object instanceof Enum) {
                return "\"" + ((Enum) object).name() + "\"";
            }
            buildClassFieldJson(buffer, clazz, object);
            if (buffer.length() > 0) {
                buffer.insert(0, "{");
                buffer.setLength(buffer.length() - 1);
                buffer.append("}");
                return buffer.toString();
            }
            return "\"" + object.toString() + "\"";
        }
    };

    /**
     * build sql request json
     *
     * @param sql
     * @return {
     * requestType: "DETAIL_QUERY",
     * table: "table_name",
     * database: "database",
     * columns: ["column1", "column2"],
     * where: "column1 = 100",
     * order: [{
     * column: "column1",
     * asc: true
     * }]
     * }
     */
    String buildSqlRequest(SqlInfo sql);

    /**
     * build auth request json
     *
     * @param user
     * @param password
     * @return like
     * {
     * requestType: "AUTH",
     * swiftUser: "username",
     * swiftPassword: "password"
     * }
     */
    String buildAuthRequest(String user, String password);
}

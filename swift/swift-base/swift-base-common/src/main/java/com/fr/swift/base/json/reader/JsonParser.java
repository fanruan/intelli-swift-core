package com.fr.swift.base.json.reader;

import com.fr.swift.base.json.exception.JsonException;
import com.fr.swift.base.json.exception.JsonParseException;
import com.fr.swift.base.json.serialization.SerializationConfig;
import com.fr.swift.base.json.serialization.SerializationConfigHolder;
import com.fr.swift.base.json.stack.Stack;
import com.fr.swift.base.json.stack.StackValue;
import com.fr.swift.base.json.token.Token;
import com.fr.swift.base.json.token.Tokenizer;
import com.fr.swift.base.json.writer.impl.ObjectJsonWriter;
import com.fr.swift.converter.FindList;
import com.fr.swift.converter.FindListImpl;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO BeanTypeReference 还没加入
 *
 * @author yee
 * @date 2018-12-13
 */
public class JsonParser {
    /**
     * read EOF for next token.
     */
    private static final int STATUS_EXPECT_END_DOCUMENT = 0x0002;
    /**
     * read "{" for next token.
     */
    private static final int STATUS_EXPECT_BEGIN_OBJECT = 0x0004;
    /**
     * read "}" for next token.
     */
    private static final int STATUS_EXPECT_END_OBJECT = 0x0008;
    /**
     * read object key for next token.
     */
    private static final int STATUS_EXPECT_OBJECT_KEY = 0x0010;
    /**
     * read object value for next token.
     */
    private static final int STATUS_EXPECT_OBJECT_VALUE = 0x0020;
    /**
     * read ":" for next token.
     */
    private static final int STATUS_EXPECT_COLON = 0x0040;
    /**
     * read "," for next token.
     */
    private static final int STATUS_EXPECT_COMMA = 0x0080;
    /**
     * read "[" for next token.
     */
    private static final int STATUS_EXPECT_BEGIN_ARRAY = 0x0100;
    /**
     * read "]" for next token.
     */
    private static final int STATUS_EXPECT_END_ARRAY = 0x0200;
    /**
     * read array value for next token.
     */
    private static final int STATUS_EXPECT_ARRAY_VALUE = 0x0400;
    /**
     * read a single value for next token (must not be "{" or "[").
     */
    private static final int STATUS_EXPECT_SINGLE_VALUE = 0x0800;
    private Tokenizer tokenizer;
    private Stack stack;
    private int status;
    private String json;

    public JsonParser(String json) {
        this.json = json;
        tokenizer = new Tokenizer(new CharReader(new StringReader(json)));
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(Class<T> clazz) throws Exception {
        try {
            Object obj = parse();
            if (obj instanceof Map && !ReflectUtils.isAssignable(clazz, Map.class)) {
                return toTargetBean((Map<String, Object>) obj, clazz);
            }
            return checkExpectedType(obj, clazz);
        } catch (JsonParseException e) {
            return (T) fromInvalidJson(json, clazz);
        }
    }

    private Object fromInvalidJson(String json, Class clazz) {
        SerializationConfig config = SerializationConfigHolder.getInstance().getSerializationConfig(clazz);
        Map<String, Class> map = config.getInstanceMap();
        if (map.containsKey(json)) {
            Class instanceClass = map.get(json);
            try {
                return ReflectUtils.parseObject(instanceClass, json);
            } catch (Exception e) {
                try {
                    return ReflectUtils.newInstance(instanceClass);
                } catch (Exception e1) {
                    return null;
                }
            }
        }
        return null;
    }

    private <T> T toTargetBean(Map<String, Object> map, Class<T> clazz) throws Exception {
        SerializationConfig config = SerializationConfigHolder.getInstance().getSerializationConfig(clazz);
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            if (Strings.isNotEmpty(config.getPropertyName())) {
                String typeName = (String) map.get(config.getPropertyName());
                clazz = config.instanceClass(typeName);
            } else {
                clazz = config.defaultImpl();
            }
        }
        config = SerializationConfigHolder.getInstance().getSerializationConfig(clazz);
        T target = ReflectUtils.newInstance(clazz);
        Map<String, SerializationConfig.BeanSetter> setters = config.setters();
        for (Map.Entry<String, SerializationConfig.BeanSetter> entry : setters.entrySet()) {
            try {
                String key = entry.getKey();
                if (map.containsKey(key)) {
                    SerializationConfig.BeanSetter setter = entry.getValue();
                    Class propertyType = null;
                    boolean generic = false;
                    try {
                        propertyType = setter.propertyType();
                    } catch (Exception e) {
                        propertyType = setter.propertyType(clazz.getGenericSuperclass());
                        generic = true;
                    }
                    Object value = map.get(key);
                    if (value instanceof Map) {
                        value = toTargetBean((Map<String, Object>) value, propertyType);
                    } else if (value instanceof List) {
                        if (ReflectUtils.isAssignable(propertyType, List.class) || propertyType.isArray()) {
                            value = buildObjectFromList(clazz, setter, (List) value, generic);
                            if (propertyType.isArray()) {
                                value = ((List) value).toArray();
                            }
                        } else if (ReflectUtils.isAssignable(propertyType, Set.class)) {
                            value = new HashSet(buildObjectFromList(clazz, setter, (List) value, generic));
                        } else {
                            throw new JsonException("Cannot set Json array to property: " + key + "(type: " + propertyType.getName() + ")");
                        }
                    } else {
                        value = ReflectUtils.parseObject(propertyType, value.toString());
                    }
                    setter.set(target, value);
                }
            } catch (Exception ignore) {
            }
        }
        return target;
    }

    private <T> List buildObjectFromList(Class<T> clazz, SerializationConfig.BeanSetter setter, List value, boolean generic) throws Exception {
        Class<?> genericType = null;
        try {
            if (generic) {
                genericType = setter.genericType(clazz.getGenericSuperclass());
            } else {
                genericType = setter.genericType();
            }
        } catch (Exception e) {
            if (generic) {
                genericType = setter.genericType();
            } else {
                genericType = setter.genericType(clazz.getGenericSuperclass());
            }
        }
        FindList list = new FindListImpl(value);
        final Class<?> finalGenericType = genericType;
        final Class<?> finalGenericType1 = genericType;
        return list.forEach(new FindList.ConvertEach() {
            @Override
            public Object forEach(int idx, Object item) throws Exception {
                Object o = null;
                if (item instanceof Map) {
                    o = toTargetBean((Map<String, Object>) item, finalGenericType1);
                }
                if (null != o) {
                    return o;
                }
                if (ReflectUtils.isPrimitiveOrWrapper(item.getClass())) {
                    return item;
                }
                o = ReflectUtils.parseObject(finalGenericType, item.toString());
                if (null != o) {
                    return o;
                }
                if (item instanceof List && Object.class.equals(finalGenericType)) {
                    return item;
                }
                return toTargetBean((Map<String, Object>) item, finalGenericType);
            }
        });
    }

    private <T> T checkExpectedType(Object obj, Class<T> clazz) {
        if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        if (obj == null && clazz == Object.class) {
            return null;
        }
        throw new ClassCastException("Cannot case parsed result from: " + obj.getClass().getName() + " to expected type: " + clazz.getName());
    }

    public Object parse() throws IOException {
        stack = new Stack();
        status = STATUS_EXPECT_SINGLE_VALUE | STATUS_EXPECT_BEGIN_OBJECT | STATUS_EXPECT_BEGIN_ARRAY;
        for (; ; ) {
            Token currentToken = tokenizer.nextToken();
            switch (currentToken.getTokenType()) {
                case BOOLEAN:
                    dealBooleanToken(currentToken);
                    continue;
                case NULL:
                    dealNullToken();
                    continue;

                case NUMBER:
                    dealNumberToken(currentToken);
                    continue;

                case STRING:
                    dealStringToken(currentToken);
                    continue;

                case SEP_COLON:
                    if (status == STATUS_EXPECT_COLON) {
                        status = STATUS_EXPECT_OBJECT_VALUE | STATUS_EXPECT_BEGIN_OBJECT | STATUS_EXPECT_BEGIN_ARRAY;
                        continue;
                    }
                    throw new JsonParseException("Unexpected char \':\'.");

                case SEP_COMMA:
                    dealCommaToken();
                    continue;

                case END_ARRAY:
                    if (hasStatus(STATUS_EXPECT_END_ARRAY)) {
                        StackValue array = stack.pop(StackValue.TYPE_ARRAY);
                        if (endOfArrayOrObject(array)) {
                            continue;
                        }
                    }
                    throw new JsonParseException("Unexpected char: \']\'.");

                case END_OBJECT:
                    if (hasStatus(STATUS_EXPECT_END_OBJECT)) {
                        StackValue object = stack.pop(StackValue.TYPE_OBJECT);
                        if (endOfArrayOrObject(object)) {
                            continue;
                        }
                    }
                    throw new JsonParseException("Unexpected char: \'}\'.");

                case END_DOCUMENT:
                    if (hasStatus(STATUS_EXPECT_END_DOCUMENT)) {
                        StackValue v = stack.pop();
                        if (stack.isEmpty()) {
                            return v.getValue();
                        }
                    }
                    throw new JsonParseException("Unexpected EOF.");

                case BEGIN_ARRAY:
                    if (hasStatus(STATUS_EXPECT_BEGIN_ARRAY)) {
                        stack.push(StackValue.newJsonArray(new ArrayList<Object>()));
                        status = STATUS_EXPECT_ARRAY_VALUE | STATUS_EXPECT_BEGIN_OBJECT | STATUS_EXPECT_BEGIN_ARRAY | STATUS_EXPECT_END_ARRAY;
                        continue;
                    }
                    throw new JsonParseException("Unexpected char: \'[\'.");

                case BEGIN_OBJECT:
                    if (hasStatus(STATUS_EXPECT_BEGIN_OBJECT)) {
                        stack.push(StackValue.newJsonObject(new HashMap<String, Object>()));
                        status = STATUS_EXPECT_OBJECT_KEY | STATUS_EXPECT_BEGIN_OBJECT | STATUS_EXPECT_END_OBJECT;
                        continue;
                    }
                    throw new JsonParseException("Unexpected char: \'{\'.");
                default:
                    break;
            }
        }
    }

    private void dealCommaToken() {
        if (hasStatus(STATUS_EXPECT_COMMA)) {
            if (hasStatus(STATUS_EXPECT_END_OBJECT)) {
                status = STATUS_EXPECT_OBJECT_KEY;
                return;
            }
            if (hasStatus(STATUS_EXPECT_END_ARRAY)) {
                status = STATUS_EXPECT_ARRAY_VALUE | STATUS_EXPECT_BEGIN_ARRAY | STATUS_EXPECT_BEGIN_OBJECT;
                return;
            }
        }
        throw new JsonParseException("Unexpected char \',\'.");
    }

    private void dealStringToken(Token currentToken) {
        if (hasStatus(STATUS_EXPECT_SINGLE_VALUE)) {
            // single string:
            String str = (String) currentToken.getValue();
            str = str.replace(ObjectJsonWriter.D_QUOTA_REPLACEMENT, "\"");
            str = str.replace(ObjectJsonWriter.S_QUOTA_REPLACEMENT, "'");
            stack.push(StackValue.newJsonSingle(str));
            status = STATUS_EXPECT_END_DOCUMENT;
            return;
        }
        if (hasStatus(STATUS_EXPECT_OBJECT_KEY)) {
            String str = (String) currentToken.getValue();
            str = str.replace(ObjectJsonWriter.D_QUOTA_REPLACEMENT, "\"");
            str = str.replace(ObjectJsonWriter.S_QUOTA_REPLACEMENT, "'");
            stack.push(StackValue.newJsonObjectKey(str));
            status = STATUS_EXPECT_COLON;
            return;
        }
        if (hasStatus(STATUS_EXPECT_OBJECT_VALUE)) {
            String str = (String) currentToken.getValue();
            str = str.replace(ObjectJsonWriter.D_QUOTA_REPLACEMENT, "\"");
            str = str.replace(ObjectJsonWriter.S_QUOTA_REPLACEMENT, "'");
            dealObjectValue(str);
            return;
        }
        if (hasStatus(STATUS_EXPECT_ARRAY_VALUE)) {
            String str = (String) currentToken.getValue();
            str = str.replace(ObjectJsonWriter.D_QUOTA_REPLACEMENT, "\"");
            str = str.replace(ObjectJsonWriter.S_QUOTA_REPLACEMENT, "'");
            stack.peek(StackValue.TYPE_ARRAY).valueAsArray().add(str);
            status = STATUS_EXPECT_COMMA | STATUS_EXPECT_END_ARRAY;
            return;
        }
        throw new JsonParseException("Unexpected char \'\"\'.");
    }

    private void dealNumberToken(Token currentToken) {
        if (hasStatus(STATUS_EXPECT_SINGLE_VALUE)) {
            // single number:
            Number number = (Number) currentToken.getValue();
            stack.push(StackValue.newJsonSingle(number));
            status = STATUS_EXPECT_END_DOCUMENT;
            return;
        }
        if (hasStatus(STATUS_EXPECT_OBJECT_VALUE)) {
            Number number = (Number) currentToken.getValue();
            dealObjectValue(number);
            return;
        }
        if (hasStatus(STATUS_EXPECT_ARRAY_VALUE)) {
            Number number = (Number) currentToken.getValue();
            stack.peek(StackValue.TYPE_ARRAY).valueAsArray().add(number);
            status = STATUS_EXPECT_COMMA | STATUS_EXPECT_END_ARRAY;
            return;
        }
        throw new JsonParseException("Unexpected number.");
    }

    private void dealNullToken() {
        if (hasStatus(STATUS_EXPECT_SINGLE_VALUE)) {
            // single null:
            stack.push(StackValue.newJsonSingle(null));
            status = STATUS_EXPECT_END_DOCUMENT;
            return;
        }
        if (hasStatus(STATUS_EXPECT_OBJECT_VALUE)) {
            dealObjectValue(null);
        }
        if (hasStatus(STATUS_EXPECT_ARRAY_VALUE)) {
            stack.peek(StackValue.TYPE_ARRAY).valueAsArray().add(null);
            status = STATUS_EXPECT_COMMA | STATUS_EXPECT_END_ARRAY;
            return;
        }
        throw new JsonParseException("Unexpected null.");
    }

    private void dealBooleanToken(Token currentToken) {
        if (hasStatus(STATUS_EXPECT_SINGLE_VALUE)) {
            // single boolean:
            Boolean bool = (Boolean) currentToken.getValue();
            stack.push(StackValue.newJsonSingle(bool));
            status = STATUS_EXPECT_END_DOCUMENT;
            return;
        }
        if (hasStatus(STATUS_EXPECT_OBJECT_VALUE)) {
            Boolean bool = (Boolean) currentToken.getValue();
            dealObjectValue(bool);
            return;
        }
        if (hasStatus(STATUS_EXPECT_ARRAY_VALUE)) {
            Boolean bool = (Boolean) currentToken.getValue();
            stack.peek(StackValue.TYPE_ARRAY).valueAsArray().add(bool);
            status = STATUS_EXPECT_COMMA | STATUS_EXPECT_END_ARRAY;
            return;
        }
        throw new JsonParseException("Unexpected boolean.");
    }

    private void dealObjectValue(Object bool) {
        String key = stack.pop(StackValue.TYPE_OBJECT_KEY).valueAsKey();
        stack.peek(StackValue.TYPE_OBJECT).valueAsObject().put(key, bool);
        status = STATUS_EXPECT_COMMA | STATUS_EXPECT_END_OBJECT;
    }

    private boolean endOfArrayOrObject(StackValue array) {
        if (stack.isEmpty()) {
            stack.push(array);
            status = STATUS_EXPECT_END_DOCUMENT;
            return true;
        }
        int type = stack.getTopValueType();
        if (type == StackValue.TYPE_OBJECT_KEY) {
            // key: [ CURRENT ] ,}
            String key = stack.pop(StackValue.TYPE_OBJECT_KEY).valueAsKey();
            stack.peek(StackValue.TYPE_OBJECT).valueAsObject().put(key, array.getValue());
            status = STATUS_EXPECT_COMMA | STATUS_EXPECT_END_OBJECT;
            return true;
        }
        if (type == StackValue.TYPE_ARRAY) {
            // xx, xx, [CURRENT] ,]
            stack.peek(StackValue.TYPE_ARRAY).valueAsArray().add(array.getValue());
            status = STATUS_EXPECT_COMMA | STATUS_EXPECT_END_ARRAY;
            return true;
        }
        return false;
    }

    private boolean hasStatus(int expectedStatus) {
        return ((status & expectedStatus) > 0);
    }
}

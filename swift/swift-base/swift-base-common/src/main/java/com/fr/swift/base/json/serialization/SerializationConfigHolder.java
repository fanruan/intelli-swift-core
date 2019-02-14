package com.fr.swift.base.json.serialization;

import com.fr.swift.base.json.annotation.JsonIgnore;
import com.fr.swift.base.json.annotation.JsonIgnoreProperties;
import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
import com.fr.swift.util.Strings;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018-12-12
 */
public class SerializationConfigHolder {
    private static final SerializationConfigHolder INSTANCE = new SerializationConfigHolder();
    private Map<Class, SerializationConfig> configMap = new ConcurrentHashMap<Class, SerializationConfig>();
    private Map<Class, Map<String, SerializationConfig.BeanGetter>> getters = new ConcurrentHashMap<Class, Map<String, SerializationConfig.BeanGetter>>();
    private Map<Class, Map<String, SerializationConfig.BeanSetter>> setters = new ConcurrentHashMap<Class, Map<String, SerializationConfig.BeanSetter>>();

    private SerializationConfigHolder() {
    }

    public static SerializationConfigHolder getInstance() {
        return INSTANCE;
    }

    public void registerConfig(Class clazz, SerializationConfig config) {
        if (!configMap.containsKey(clazz)) {
            configMap.put(clazz, config);
        } else {
            Set<String> properties = config.ignoreProperties();
            if (!properties.isEmpty()) {
                configMap.get(clazz).addIgnore(properties.toArray(new String[properties.size()]));
            }
            Map<String, Class> instanceMap = config.getInstanceMap();
            for (Map.Entry<String, Class> entry : instanceMap.entrySet()) {
                configMap.get(clazz).addInstanceMap(entry.getKey(), entry.getValue());
            }
            if (null != config.defaultImpl()) {
                configMap.get(clazz).setDefaultImpl(config.defaultImpl());
            }
        }
    }

    public SerializationConfig getSerializationConfig(Class clazz) {
        if (!configMap.containsKey(clazz)) {
            initSerializationConfig(clazz);
        }
        SerializationConfig config = configMap.get(clazz);
        config.setGetters(getters.get(clazz));
        config.setSetters(setters.get(clazz));
        return config;
    }

    private void initSerializationConfig(Class<?> clazz) {
        final Set<String> fields = new HashSet<String>();
        final Map<String, SerializationConfig.BeanGetter> getters = new HashMap<String, SerializationConfig.BeanGetter>();
        final Map<String, SerializationConfig.BeanSetter> setters = new HashMap<String, SerializationConfig.BeanSetter>();
        SerializationConfig config = new SerializationConfigImpl();
        while (null != clazz && !clazz.equals(Object.class)) {
            initSerializationConfig(config, clazz);
            this.getters.put(clazz, getters);
            this.setters.put(clazz, setters);
            for (Class anInterface : clazz.getInterfaces()) {
                if (Serializable.class.equals(anInterface)) {
                    continue;
                }
                initSerializationConfig(config, anInterface);
            }
            for (final Field f : clazz.getDeclaredFields()) {
                String propertyName = f.getName();
                if (f.isAnnotationPresent(JsonIgnore.class) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                Method method = null;
                Class fieldClass = null;
                try {
                    fieldClass = getRawType(f.getGenericType());
                } catch (Exception e) {
                    fieldClass = getRawType(f.getType());
                }
                if (f.isAnnotationPresent(JsonProperty.class)) {
                    JsonProperty property = f.getAnnotation(JsonProperty.class);
                    if (Strings.isNotEmpty(property.value())) {
                        propertyName = property.value();
                    }
                    if (Strings.isNotEmpty(property.serializeMethod())) {
                        try {
                            method = fieldClass.getDeclaredMethod(property.serializeMethod());
                            method.setAccessible(true);
                        } catch (NoSuchMethodException e) {
                            method = null;
                        }
                    }
                }
                if (!fields.contains(propertyName)) {
                    fields.add(propertyName);
                    final Method finalMethod = method;
                    getters.put(propertyName, new SerializationConfig.BeanGetter() {
                        @Override
                        public Object get(Object o) throws Exception {
                            f.setAccessible(true);
                            Object result = f.get(o);
                            if (null != finalMethod) {
                                return finalMethod.invoke(result);
                            }
                            return result;
                        }
                    });

                    setters.put(propertyName, new SerializationConfig.BeanSetter() {
                        @Override
                        public void set(Object object, Object value) throws Exception {
                            f.setAccessible(true);
                            f.set(object, value);
                        }

                        @Override
                        public Class<?> genericType() {
                            return getRawType(getGenericType(f.getGenericType()));
                        }

                        @Override
                        public Class<?> genericType(Type clazz) {
                            return getRawType(getGenericType(getGenericType(clazz)));
                        }

                        @Override
                        public Class<?> propertyType() {
                            return getRawType(f.getGenericType());
                        }

                        @Override
                        public Class<?> propertyType(Type clazz) {
                            return getRawType(getGenericType(clazz));
                        }

                        @Override
                        public String getField() {
                            return f.getName();
                        }
                    });
                }
            }
            clazz = clazz.getSuperclass();
        }
        for (String ignoreProperty : config.ignoreProperties()) {
            getters.remove(ignoreProperty);
            setters.remove(ignoreProperty);
        }
    }

    private void initSerializationConfig(SerializationConfig config, Class<?> clazz) {
        if (clazz.isAnnotationPresent(JsonIgnoreProperties.class)) {
            JsonIgnoreProperties properties = clazz.getAnnotation(JsonIgnoreProperties.class);
            config.addIgnore(properties.value());
        }
        if (clazz.isAnnotationPresent(JsonTypeInfo.class)) {
            JsonTypeInfo typeInfo = clazz.getAnnotation(JsonTypeInfo.class);
            config.setPropertyName(typeInfo.property());
            config.setDefaultImpl(typeInfo.defaultImpl());
        }
        if (clazz.isAnnotationPresent(JsonSubTypes.class)) {
            JsonSubTypes subTypes = clazz.getAnnotation(JsonSubTypes.class);
            for (JsonSubTypes.Type type : subTypes.value()) {
                config.addInstanceMap(type.name(), type.value());
            }
        }
        SerializationConfigHolder.getInstance().registerConfig(clazz, config);
    }

    private Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return (Class<?>) pt.getRawType();
        }
        return (Class<?>) type;
    }

    private Type getGenericType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] ts = pt.getActualTypeArguments();
            if (ts.length == 1) {
                return ts[0];
            }
        }
        try {
            Class<?> clazz = null;
            clazz = (Class<?>) type;
            if (clazz.isArray()) {
                return clazz.getComponentType();
            }
        } catch (Exception ignore) {
        }
        return Object.class;
    }
}

package com.fr.swift.base.json.serialization;

import com.fr.swift.base.json.exception.JsonException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018-12-12
 */
public class SerializationConfigImpl implements SerializationConfig {
    private Set<String> ignore = new HashSet<String>();
    private Map<String, Class> instanceReference = new ConcurrentHashMap<String, Class>();
    private Class defaultImpl;
    private String propertyName;
    private Map<String, BeanGetter> getters;
    private Map<String, BeanSetter> setters;

    @Override
    public Set<String> ignoreProperties() {
        return Collections.unmodifiableSet(ignore);
    }

    @Override
    public Class instanceClass(String name) {
        return instanceReference.get(name);
    }

    @Override
    public Class defaultImpl() {
        return defaultImpl;
    }

    public Set<String> getIgnore() {
        return ignore;
    }

    @Override
    public void addIgnore(String... ignoreProperties) {
        ignore.addAll(Arrays.asList(ignoreProperties));
    }

    @Override
    public void addInstanceMap(String name, Class instanceClass) {
        Class instance = instanceReference.get(name);
        if (null != instance) {
            if (!instance.equals(instanceClass)) {
                throw new JsonException(String.format("Instance class named \"%s\" is already exists. Class is %s", name, instance.getName()));
            }
            return;
        }
        instanceReference.put(name, instanceClass);
    }

    @Override
    public Map<String, Class> getInstanceMap() {
        return instanceReference;
    }

    @Override
    public void setDefaultImpl(Class defaultImpl) {
        if (null != this.defaultImpl) {
            if (!this.defaultImpl.equals(defaultImpl)) {
                throw new JsonException(String.format("Default class is already exists. Class is %s", this.defaultImpl.getName()));
            }
            return;
        }
        this.defaultImpl = defaultImpl;
    }


    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Map<String, BeanGetter> getters() {
        return getters;
    }

    @Override
    public Map<String, BeanSetter> setters() {
        return setters;
    }

    @Override
    public void setGetters(Map<String, BeanGetter> getters) {
        this.getters = getters;
    }

    @Override
    public void setSetters(Map<String, BeanSetter> setters) {
        this.setters = setters;
    }

    @Override
    public SerializationConfig copy() {
        return (SerializationConfig) this.clone();
    }

    @Override
    protected Object clone() {
        SerializationConfigImpl clone = new SerializationConfigImpl();
        clone.defaultImpl = this.defaultImpl;
        clone.ignore = new HashSet<String>(this.ignore);
        clone.instanceReference = new ConcurrentHashMap<String, Class>(this.instanceReference);
        clone.propertyName = this.propertyName;
        return clone;
    }
}

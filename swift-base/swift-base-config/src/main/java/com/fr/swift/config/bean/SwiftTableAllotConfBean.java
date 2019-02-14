package com.fr.swift.config.bean;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.alloter.AllotRule;

import java.lang.reflect.Constructor;

/**
 * @author yee
 * @date 2018-11-26
 */
public class SwiftTableAllotConfBean implements ObjectConverter {
    public static final Class TYPE = entityType();
    private String tableKey;
    private AllotRule allotRule;

    public SwiftTableAllotConfBean() {
    }

    public SwiftTableAllotConfBean(String id, AllotRule allotRule) {
        this.tableKey = id;
        this.allotRule = allotRule;
    }

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.config.entity.SwiftTableAllotConf");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getTableKey() {
        return tableKey;
    }

    public void setTableKey(String tableKey) {
        this.tableKey = tableKey;
    }

    public AllotRule getAllotRule() {
        return allotRule;
    }

    public void setAllotRule(AllotRule allotRule) {
        this.allotRule = allotRule;
    }

    @Override
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(SwiftTableAllotConfBean.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}

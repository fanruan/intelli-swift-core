package com.fr.swift.config.oper.impl;

import com.fr.swift.config.oper.RestrictionFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.fr.swift.config.oper.impl.ConfigOrder.getObject;

/**
 * @author yee
 * @date 2018-11-27
 */
public enum RestrictionFactoryImpl implements RestrictionFactory {
    //
    INSTANCE;
    private static final Map<String, Method> nameToMethod = new HashMap<String, Method>();

    static {
        try {
            Method eq = VersionConfigProperty.get().getRestrictions().getDeclaredMethod("eq", String.class, Object.class);
            Method in = VersionConfigProperty.get().getRestrictions().getDeclaredMethod("in", String.class, Collection.class);
            Method like = VersionConfigProperty.get().getRestrictions().getDeclaredMethod("like", String.class, String.class, VersionConfigProperty.get().getMatchMode());
            Method valueOf = VersionConfigProperty.get().getMatchMode().getDeclaredMethod("valueOf", String.class);
            nameToMethod.put("eq", eq);
            nameToMethod.put("in", in);
            nameToMethod.put("like", like);
            nameToMethod.put("valueOf", valueOf);
        } catch (NoSuchMethodException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static Object invoke(String methodName, Object... args) throws Exception {
        return getObject(methodName, nameToMethod, args);
    }

    @Override
    public Object eq(String columnName, Object serializable) {
        try {
            return invoke("eq", columnName, serializable);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public Object in(String columnName, Collection collection) {
        try {
            return invoke("in", columnName, collection);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public Object like(String columnName, String value, MatchMode matchMode) {
        try {
            return invoke("like", columnName, value, invoke("valueOf", matchMode.name()));
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}

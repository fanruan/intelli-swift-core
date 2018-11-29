package com.fr.swift.config.convert.base;

import com.fr.swift.config.annotation.ConfigField;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/16
 */
public abstract class AbstractObjectConfigConvert<T> extends BaseConfigConvert<T> {
    private Class<T> tClass;

    public AbstractObjectConfigConvert(Class<T> tClass) {
        checkClass(tClass);
        this.tClass = tClass;
    }

    @Override
    public T toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) throws SQLException {
        SwiftConfigBean entity = dao.select(session, getKey("class"));
        if (null == entity) {
            throw new SQLException("Cannot find Rule");
        }
        String className = transferClassName(entity.getConfigValue());
        try {
            Class<? extends T> clazz = (Class<? extends T>) this.getClass().getClassLoader().loadClass(className);
            if (!ReflectUtils.isAssignable(clazz, tClass)) {
                throw new Exception("Not Match");
            }
            T rule = ReflectUtils.newInstance(clazz);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ConfigField.class)) {
                    SwiftConfigBean tmp = dao.select(session, getKey(field.getName()));
                    if (null != tmp) {
                        ReflectUtils.set(field, rule, tmp.getConfigValue());
                    }
                }
            }
            return rule;
        } catch (Exception e) {
            throw new SQLException("Cannot instance " + className, e);
        }
    }

    @Override
    public List<SwiftConfigBean> toEntity(T dataSyncRule, Object... args) {
        try {
            String syncClass = dataSyncRule.getClass().getName();
            List<SwiftConfigBean> result = new ArrayList<SwiftConfigBean>();
            SwiftConfigBean entity = new SwiftConfigBean(getKey("class"), syncClass);
            result.add(entity);
            Field[] fields = dataSyncRule.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ConfigField.class)) {
                    ConfigField configField = field.getAnnotation(ConfigField.class);
                    String value = ReflectUtils.getString(field, dataSyncRule);
                    if (Strings.isEmpty(value) && configField.ignoreNull()) {
                        continue;
                    }
                    result.add(new SwiftConfigBean(getKey(field.getName()), ReflectUtils.getString(field, dataSyncRule)));
                }
            }
            return result;
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return null;
    }

    private void checkClass(Class tClass) {
        if (ReflectUtils.isPrimitiveOrWrapper(tClass) || ReflectUtils.isAssignable(tClass, String.class)) {
            throw new RuntimeException("Object " + tClass + " is simple. Please use SimpleConfigConvert");
        }
    }

    protected abstract String transferClassName(String className);
}

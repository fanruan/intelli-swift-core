package com.fr.swift.config.convert.swift;

import com.fr.stable.StringUtils;
import com.fr.swift.config.annotation.ConfigField;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.ReflectUtils;
import com.fr.third.org.hibernate.Session;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/16
 */
public abstract class AbstractObjectConfigConvert<T> extends BaseConfigConvert<T> {

    @Override
    public T toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) throws SQLException {
        SwiftConfigEntity entity = dao.select(session, getKey("class"));
        if (null == entity) {
            throw new SQLException("Cannot find Rule");
        }
        String className = entity.getConfigValue();
        try {
            Class<? extends T> clazz = (Class<? extends T>) Class.forName(className);
            T rule = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ConfigField.class)) {
                    SwiftConfigEntity tmp = dao.select(session, getKey(field.getName()));
                    if (null != tmp) {
                        ReflectUtils.set(field, rule, tmp.getConfigValue());
                    }
                }
            }
            return rule;
        } catch (Exception e) {
            throw new SQLException("Cannot find Rule");
        }
    }

    @Override
    public List<SwiftConfigEntity> toEntity(T dataSyncRule, Object... args) {
        try {
            String syncClass = dataSyncRule.getClass().getName();
            List<SwiftConfigEntity> result = new ArrayList<SwiftConfigEntity>();
            SwiftConfigEntity entity = new SwiftConfigEntity(getKey("class"), syncClass);
            result.add(entity);
            Field[] fields = dataSyncRule.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ConfigField.class)) {
                    ConfigField configField = field.getAnnotation(ConfigField.class);
                    String value = ReflectUtils.getString(field, dataSyncRule);
                    if (StringUtils.isEmpty(value) && configField.ignoreNull()) {
                        continue;
                    }
                    result.add(new SwiftConfigEntity(getKey(field.getName()), ReflectUtils.getString(field, dataSyncRule)));
                }
            }
            return result;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}

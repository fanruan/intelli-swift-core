package com.fr.swift.config.command.impl;

import com.fr.swift.annotation.persistence.Embeddable;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.config.annotation.DeleteCascade;
import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigDelete;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.util.ReflectUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2019-08-09
 */
public class DeleteCascadeCommand<T> implements SwiftConfigCommand<Integer> {
    private Class<T> tClass;
    private SwiftConfigCondition condition;
    private Map<String, DeleteCascade> deleteCascadeMap = new HashMap<>();


    DeleteCascadeCommand(Class<T> tClass, SwiftConfigCondition condition) {
        this.tClass = tClass;
        this.condition = condition;
        initDeleteCascade();
    }

    private void initDeleteCascade() {
        final List<Field> fields = ReflectUtils.getFields(tClass, ReflectUtils.AnnotatedElementsMatcher.of(DeleteCascade.class));
        final List<Field> id = ReflectUtils.getFields(tClass, ReflectUtils.AnnotatedElementsMatcher.of(Id.class));
        initIdDeleteCascade(id);
        for (Field field : fields) {
            DeleteCascade annotation = field.getAnnotation(DeleteCascade.class);
            // TODO 2019/08/09 先用fieldName 目前字段名都是和fieldName一样的
            deleteCascadeMap.put(field.getName(), annotation);
        }
    }

    private void initIdDeleteCascade(List<Field> fields) {
        for (Field id : fields) {
            final Class<?> type = id.getType();
            if (id.isAnnotationPresent(DeleteCascade.class)) {
                DeleteCascade annotation = id.getAnnotation(DeleteCascade.class);
                // ID名字和field名字是一样的
                deleteCascadeMap.put(id.getName(), annotation);
            }
            if (type.isAnnotationPresent(Embeddable.class)) {
                final List<Field> ids = ReflectUtils.getFields(type, ReflectUtils.AnnotatedElementsMatcher.of(DeleteCascade.class));
                for (Field field : ids) {
                    DeleteCascade annotation = field.getAnnotation(DeleteCascade.class);
                    deleteCascadeMap.put(id.getName() + "." + field.getName(), annotation);
                }
            }
        }

    }

    @Override
    public Integer apply(ConfigSession p) {
        ConfigDelete<T> entityQuery = p.createEntityDelete(tClass);
        final ConfigWhere[] wheres = condition.getWheres().toArray(new ConfigWhere[0]);
        entityQuery.where(wheres);
        return entityQuery.delete() + deleteCascade(p, condition.getWheres());
    }

    private int deleteCascade(ConfigSession session, List<ConfigWhere> wheres) {
        int result = 0;
        for (ConfigWhere where : wheres) {
            if (where.type() == ConfigWhere.Type.AND || where.type() == ConfigWhere.Type.OR) {
                result += deleteCascade(session, (List<ConfigWhere>) where.getValue());
            } else if (deleteCascadeMap.containsKey(where.getColumn())) {
                DeleteCascade deleteCascade = deleteCascadeMap.get(where.getColumn());
                ConfigDelete<?> entityDelete = session.createEntityDelete(deleteCascade.target());
                entityDelete.where(where.rename(deleteCascade.column()));
                result += entityDelete.delete();
            }
        }
        return result;
    }
}

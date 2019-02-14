package com.fr.swift.config.convert.base;

import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.util.ReflectUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/17
 */
public abstract class AbstractSimpleConfigConvert<T> extends BaseConfigConvert<T> {

    private Class<T> tClass;

    public AbstractSimpleConfigConvert(Class<T> tClass) {
        checkClass(tClass);
        this.tClass = tClass;
    }

    @Override
    public T toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) throws SQLException {
        SwiftConfigBean entity = dao.select(session, getNameSpace());
        if (null != entity) {
            try {
                String path = entity.getConfigValue();
                return (T) ReflectUtils.parseObject(tClass, path);
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
        throw new SQLException("Cannot find config! ");
    }

    @Override
    public List<SwiftConfigBean> toEntity(T t, Object... args) {
        SwiftConfigBean entity = new SwiftConfigBean();
        entity.setConfigKey(getNameSpace());
        entity.setConfigValue(t.toString());
        return Collections.singletonList(entity);
    }

    private void checkClass(Class tClass) {
        if (!ReflectUtils.isPrimitiveOrWrapper(tClass) && !ReflectUtils.isAssignable(tClass, String.class)) {
            throw new RuntimeException("Object " + tClass + " is not simple");
        }
    }
}

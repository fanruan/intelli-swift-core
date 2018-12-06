package com.fr.swift.config.oper.proxy;

import com.fr.swift.config.oper.exception.SwiftConstraintViolationException;
import com.fr.swift.config.oper.exception.SwiftEntityExistsException;
import com.fr.swift.config.oper.exception.SwiftNonUniqueObjectException;
import com.fr.swift.config.oper.impl.VersionConfigProperty;
import com.fr.swift.util.ReflectUtils;

/**
 * @author yee
 * @date 2018-12-05
 */
public class HibernateThrowableHelper {
    public static Throwable throwThrowable(Throwable throwable, Throwable start) {
        if (ReflectUtils.isAssignable(start.getClass(), VersionConfigProperty.get().getEntityExistsException())) {
            return new SwiftEntityExistsException(throwable);
        }
        if (ReflectUtils.isAssignable(start.getClass(), VersionConfigProperty.get().getConstraintViolationException())) {
            return new SwiftConstraintViolationException(throwable);
        }
        if (ReflectUtils.isAssignable(start.getClass(), VersionConfigProperty.get().getNonUniqueObjectException())) {
            return new SwiftNonUniqueObjectException(throwable);
        }
        if (null != throwable.getCause()) {
            return throwThrowable(throwable, start.getCause());
        } else {
            return throwable;
        }
    }
}

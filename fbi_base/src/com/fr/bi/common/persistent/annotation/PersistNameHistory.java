package com.fr.bi.common.persistent.annotation;

import java.lang.annotation.*;

/**
 * Created by neil on 17-3-16.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PersistNameHistory {
     String [] historyNames();
}

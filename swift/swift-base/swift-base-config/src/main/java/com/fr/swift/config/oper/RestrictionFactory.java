package com.fr.swift.config.oper;

import java.util.Collection;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface RestrictionFactory {
    Object eq(String columnName, Object serializable);

    Object in(String columnName, Collection collection);

    Object like(String columnName, String value, MatchMode matchMode);

    enum MatchMode {
        //
        EXACT {
            @Override
            public String toMatchString(String pattern) {
                return pattern;
            }
        },
        START {
            @Override
            public String toMatchString(String pattern) {
                return pattern + '%';
            }
        },
        END {
            @Override
            public String toMatchString(String pattern) {
                return '%' + pattern;
            }
        },
        ANYWHERE {
            @Override
            public String toMatchString(String pattern) {
                return '%' + pattern + '%';
            }
        };

        MatchMode() {
        }

        public abstract String toMatchString(String var1);
    }
}

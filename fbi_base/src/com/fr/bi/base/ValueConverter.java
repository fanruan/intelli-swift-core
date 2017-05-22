package com.fr.bi.base;


import java.util.Calendar;

public interface ValueConverter<F, T> {

    public static ValueConverter<Object, Object> DEFAULT = new ValueConverter<Object, Object>() {
        @Override
        public Object result2Value(Object res) {
            return res;
        }

        @Override
        public Object result2Value(Object o, Calendar calendar) {
            return o;
        }
    };

    public T result2Value(F f);

    public T result2Value(F f, Calendar calendar);

}
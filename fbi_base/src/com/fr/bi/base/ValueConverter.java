package com.fr.bi.base;


public interface ValueConverter<F, T> {

    public static ValueConverter<Object, Object> DEFAULT = new ValueConverter<Object, Object>() {
        @Override
        public Object result2Value(Object res) {
            return res;
        }
    };

    public T result2Value(F f);

}
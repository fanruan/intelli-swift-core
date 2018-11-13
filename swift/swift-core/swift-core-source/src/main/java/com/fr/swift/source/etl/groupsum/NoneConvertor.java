package com.fr.swift.source.etl.groupsum;

import com.fr.swift.util.function.Function;

/**
 * Created by pony on 2018/4/24.
 */
public class NoneConvertor implements Function {
    @Override
    public Object apply(Object p) {
        return p;
    }
}

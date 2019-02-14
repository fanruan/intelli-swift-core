package com.fr.swift.base.json;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-04
 */
public class BaseType implements TestInf {
    @JsonProperty("type")
    TestInf.Type type;

    public BaseType(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }


}

package com.fr.swift.source.alloter.impl.hash.function;

import com.fr.swift.base.json.annotation.JsonIgnoreProperties;
import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;

/**
 * @author lucifer
 * @date 2019-06-24
 * @description
 * @since advanced swift 1.0
 */
@JsonIgnoreProperties("type")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = JdkHashFunction.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JdkHashFunction.class),

})
public interface HashFunction {

    int indexOf(Object key);

    HashType getType();

}

package com.fr.swift.source.alloter.impl.hash.function;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author lucifer
 * @date 2019-06-24
 * @description
 * @since advanced swift 1.0
 */
public class JdkHashFunction implements HashFunction {

    @JsonProperty("partitions")
    private int partitions;

    private JdkHashFunction() {
    }

    public JdkHashFunction(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public int indexOf(Object key) {
        try {
            int h;
            int hashCode = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
            int index = (partitions - 1) & hashCode;
            return index;
        } catch (Exception warn) {
            SwiftLoggers.getLogger().warn(warn);
            return 0;
        }
    }

    @Override
    public HashType getType() {
        return HashType.JDK;
    }
}

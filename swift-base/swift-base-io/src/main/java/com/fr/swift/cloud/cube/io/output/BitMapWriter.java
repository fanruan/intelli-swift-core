package com.fr.swift.cloud.cube.io.output;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;

/**
 * @author anchore
 */
public interface BitMapWriter extends ObjectWriter<ImmutableBitMap> {

    /**
     * todo 要去掉的，这个接口不好
     */
    void resetContentPosition();
}

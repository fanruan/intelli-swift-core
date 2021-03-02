package com.fr.swift.cloud.source.alloter.impl.hash;

import com.fr.swift.cloud.source.alloter.impl.hash.function.DateAppIdHashFunction;
import com.fr.swift.cloud.source.alloter.impl.hash.function.HashType;
import com.fr.swift.cloud.structure.Range;

/**
 * @author Heng.J
 * @date 2020/11/16
 * @description
 * @since swift-1.2.0
 */
public interface HashIndexRange extends Range<Integer> {

    static HashIndexRange getRange(HashType hashType) {
        if (HashType.APPID_YEARMONTH.equals(hashType)) {
            return new DateAppIdHashFunction.DateAppIdHashRange();
        } else {
            // for other hash function
            return null;
        }
    }

    HashIndexRange ofKey(Object key);
}

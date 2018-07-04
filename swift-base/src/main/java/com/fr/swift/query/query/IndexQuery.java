package com.fr.swift.query.query;

import com.fr.swift.bitmap.ImmutableBitMap;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface IndexQuery<T extends ImmutableBitMap> {

    T getQueryIndex() throws Exception;
}

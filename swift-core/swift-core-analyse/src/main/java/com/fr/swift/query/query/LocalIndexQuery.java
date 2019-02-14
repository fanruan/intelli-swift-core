package com.fr.swift.query.query;

import com.fr.swift.bitmap.ImmutableBitMap;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalIndexQuery implements IndexQuery {

    private ImmutableBitMap bitMap;

    public LocalIndexQuery(ImmutableBitMap bitMap) {
        this.bitMap = bitMap;
    }

    @Override
    public ImmutableBitMap getQueryIndex() throws Exception {
        return bitMap;
    }
}

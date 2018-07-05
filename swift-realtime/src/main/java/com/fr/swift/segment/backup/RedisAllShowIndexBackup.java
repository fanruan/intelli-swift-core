package com.fr.swift.segment.backup;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Segment;
import com.fr.swift.structure.array.IntArray;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

/**
 * This class created on 2018/7/5
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisAllShowIndexBackup implements AllShowIndexBackup {

    private Segment segment;

    private static final String REDIS_ALLSHOWINDEX_KEY = "/redis_allshowindex";

    @Autowired
    protected RedisClient redisClient;

    public RedisAllShowIndexBackup(Segment segment) {
        this.segment = segment;
    }

    @Override
    public void backupAllShowIndex(ImmutableBitMap allShowIndex) {
        IntArray intArray = BitMaps.traversal2Array(allShowIndex);
        StringBuilder indexStr = new StringBuilder();
        for (int i = 0; i < intArray.size(); i++) {
            indexStr.append(intArray.get(i)).append(",");
        }
        redisClient.set(segment.getLocation().getPath() + REDIS_ALLSHOWINDEX_KEY, indexStr.toString());
    }
}

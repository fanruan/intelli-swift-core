package com.fr.swift.segment.backup;

import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Segment;

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

    protected RedisClient redisClient = SwiftContext.get().getBean(RedisClient.class);

    public RedisAllShowIndexBackup(Segment segment) {
        this.segment = segment;
    }

    @Override
    public void backupAllShowIndex(final ImmutableBitMap allShowIndex) {
        final StringBuilder indexStr = new StringBuilder();
        allShowIndex.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                indexStr.append(row).append(",");
            }
        });
        redisClient.set(segment.getLocation().getPath() + REDIS_ALLSHOWINDEX_KEY, indexStr.toString());
    }
}

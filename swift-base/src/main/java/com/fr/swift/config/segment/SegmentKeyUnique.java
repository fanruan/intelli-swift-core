package com.fr.swift.config.segment;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.net.URI;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentKeyUnique extends UniqueKey implements SegmentKey {
    private Conf<String> table = Holders.simple(StringUtils.EMPTY);

    private Conf<String> name = Holders.simple(StringUtils.EMPTY);

    private Conf<String> uri = Holders.simple(StringUtils.EMPTY);

    private Conf<Integer> order = Holders.simple(0);

    private Conf<String> storeType = Holders.simple(StringUtils.EMPTY);

    public SegmentKeyUnique(SourceKey table, String name, URI uri, int order, StoreType storeType) {
        this.table.set(table.getId());
        this.name.set(name);
        this.uri.set(uri.getPath());
        this.order.set(order);
        this.storeType.set(storeType.name());
    }

    public SegmentKeyUnique() {
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.valueOf(storeType.get());
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public URI getUri() {
        return URI.create(uri.get());
    }

    @Override
    public int getOrder() {
        return order.get();
    }

    @Override
    public SourceKey getTable() {
        return new SourceKey(table.get());
    }
}
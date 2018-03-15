package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.ISegmentKey;
import com.fr.swift.cube.io.Types;

import java.net.URI;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentKeyUnique extends UniqueKey implements ISegmentKey {
    private Conf<String> name = Holders.simple(StringUtils.EMPTY);
    private Conf<String> uri = Holders.simple(StringUtils.EMPTY);
    private Conf<Integer> segmentOrder = Holders.simple(0);
    private Conf<String> sourceId = Holders.simple(StringUtils.EMPTY);
    private Conf<String> storeType = Holders.simple(StringUtils.EMPTY);

    public SegmentKeyUnique() {
    }

    public SegmentKeyUnique(String name, URI uri, int segmentOrder, Types.StoreType storeType) {
        setName(name);
        setUri(uri.getPath());
        setSegmentOrder(segmentOrder);
        setStoreType(storeType.name());
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getStoreType() {
        return storeType.get();
    }

    public void setStoreType(String storeType) {
        this.storeType.set(storeType);
    }

    public String getName() {
        return name.get();
    }

    public String getUri() {
        return uri.get();
    }

    public void setUri(String uri) {
        this.uri.set(uri);
    }

    public int getSegmentOrder() {
        return segmentOrder.get();
    }

    public void setSegmentOrder(int segmentOrder) {
        this.segmentOrder.set(segmentOrder);
    }

    public String getSourceId() {
        return sourceId.get();
    }

    public void setSourceId(String sourceId) {
        this.sourceId.set(sourceId);
    }
}

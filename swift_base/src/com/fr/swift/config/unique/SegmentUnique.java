package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectColConf;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.ISegment;
import com.fr.swift.config.ISegmentKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentUnique extends UniqueKey implements ISegment {

    private Conf<String> sourceKey = Holders.simple(StringUtils.EMPTY);

    private ObjectColConf<Collection<ISegmentKey>> segments = Holders.objCollection(new ArrayList<ISegmentKey>(), ISegmentKey.class);

    public List<ISegmentKey> getSegments() {
        return (List<ISegmentKey>) segments.get();
    }

    public void setSegments(List<ISegmentKey> segments) {
        this.segments.set(segments);
    }

    public String getSourceKey() {
        return sourceKey.get();
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey.set(sourceKey);
    }
}

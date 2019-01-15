package com.fr.swift.event.global;

import com.fr.swift.db.Where;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/9/13
 */
public class TruncateEvent extends AbstractGlobalRpcEvent<Pair<SourceKey, Where>> implements Serializable {
    private static final long serialVersionUID = -7776848856352325564L;
    private Pair<SourceKey, Where> content;

    public TruncateEvent(String sourceKey, Where where) {
        this.content = Pair.of(new SourceKey(sourceKey), where);
    }

    @Override
    public Event subEvent() {
        return Event.TRUNCATE;
    }

    @Override
    public Pair<SourceKey, Where> getContent() {
        return content;
    }
}

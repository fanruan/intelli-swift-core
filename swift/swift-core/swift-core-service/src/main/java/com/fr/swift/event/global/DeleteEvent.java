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
public class DeleteEvent extends AbstractGlobalRpcEvent<Pair<SourceKey, Where>> implements Serializable {

    private static final long serialVersionUID = -186984049574011968L;
    private Pair<SourceKey, Where> content;

    public DeleteEvent(Pair<SourceKey, Where> content) {
        this.content = content;
    }

    @Override
    public Event subEvent() {
        return Event.DELETE;
    }

    @Override
    public Pair<SourceKey, Where> getContent() {
        return content;
    }
}

package com.fr.swift.event.indexing;

import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.stuff.IndexingStuff;

/**
 * @author yee
 * @date 2018/6/11
 */
public class IndexRpcEvent extends AbstractIndexingRpcEvent<IndexingStuff> {

    private static final long serialVersionUID = -3778606145652475091L;
    private IndexingStuff indexingStuff;

    public IndexRpcEvent(IndexingStuff indexingStuff) {
        this.indexingStuff = indexingStuff;
    }

    @Override
    public Event subEvent() {
        return Event.INDEX;
    }

    @Override
    public IndexingStuff getContent() {
        return indexingStuff;
    }
}

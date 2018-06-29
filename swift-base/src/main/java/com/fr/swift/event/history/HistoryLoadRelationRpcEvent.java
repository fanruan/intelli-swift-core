package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.structure.Pair;

import java.net.URI;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/29
 */
public class HistoryLoadRelationRpcEvent extends AbstractHistoryRpcEvent<Pair<String, List<URI>>> {

    private static final long serialVersionUID = -6256005102486640777L;
    private Pair<String, List<URI>> relation;

    public HistoryLoadRelationRpcEvent(Pair<String, List<URI>> relation) {
        this.relation = relation;
    }

    @Override
    public Event subEvent() {
        return Event.LOAD_RELATION;
    }

    @Override
    public Pair<String, List<URI>> getContent() {
        return relation;
    }
}

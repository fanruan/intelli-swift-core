package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/29
 */
public class HistoryCommonLoadRpcEvent extends AbstractHistoryRpcEvent<Pair<String, List<String>>> {

    private static final long serialVersionUID = -6256005102486640777L;
    private Pair<String, List<String>> relation;

    public HistoryCommonLoadRpcEvent(Pair<String, List<String>> relation) {
        this.relation = relation;
    }

    @Override
    public Event subEvent() {
        return Event.COMMON_LOAD;
    }

    @Override
    public Pair<String, List<String>> getContent() {
        return relation;
    }
}

package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/29
 */
public class HistoryCommonLoadRpcEvent extends AbstractHistoryRpcEvent<Pair<String, Map<String, List<String>>>> {

    private static final long serialVersionUID = -6256005102486640777L;
    private Pair<String, Map<String, List<String>>> relation;

    public HistoryCommonLoadRpcEvent(Pair<String, Map<String, List<String>>> relation) {
        this.relation = relation;
    }

    @Override
    public Event subEvent() {
        return Event.COMMON_LOAD;
    }

    @Override
    public Pair<String, Map<String, List<String>>> getContent() {
        return relation;
    }
}

package com.fr.swift.event.history;

import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/29
 */
public class HistoryCommonLoadRpcEvent extends CommonLoadRpcEvent {

    private static final long serialVersionUID = -6256005102486640777L;

    public HistoryCommonLoadRpcEvent(Pair<String, Map<String, List<String>>> relation, String sourceClusterId) {
        super(relation, sourceClusterId);
    }

    @Override
    public Event subEvent() {
        return Event.COMMON_LOAD;
    }
}

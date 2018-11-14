package com.fr.swift.event.history;

import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/9/12
 */
public class ModifyLoadRpcEvent extends CommonLoadRpcEvent {
    public ModifyLoadRpcEvent(Pair<String, Map<String, List<String>>> content, String sourceClusterId) {
        super(content, sourceClusterId);
    }

    @Override
    public Event subEvent() {
        return Event.MODIFY_LOAD;
    }
}

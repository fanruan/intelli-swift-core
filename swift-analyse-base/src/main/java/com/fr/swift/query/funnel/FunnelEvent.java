package com.fr.swift.query.funnel;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelEvent {
    private int[] eventIndex;

    public FunnelEvent(int[] eventIndex) {
        this.eventIndex = eventIndex;
    }

    public int[] getEventIndex() {
        return eventIndex;
    }

    public void setEventIndex(int[] eventIndex) {
        this.eventIndex = eventIndex;
    }
}

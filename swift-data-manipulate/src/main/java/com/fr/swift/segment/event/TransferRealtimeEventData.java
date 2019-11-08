package com.fr.swift.segment.event;

import com.fr.swift.segment.SegmentKey;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 11/7/2019
 */
public class TransferRealtimeEventData {
    private SegmentKey segKey;
    private boolean passive;

    private TransferRealtimeEventData(SegmentKey segKey, boolean passive) {
        this.segKey = segKey;
        this.passive = passive;
    }

    public static TransferRealtimeEventData ofPassive(SegmentKey segKey) {
        return new TransferRealtimeEventData(segKey, true);
    }

    public static TransferRealtimeEventData ofActive(SegmentKey segKey) {
        return new TransferRealtimeEventData(segKey, false);
    }

    public SegmentKey getSegKey() {
        return segKey;
    }

    public boolean isPassive() {
        return passive;
    }
}

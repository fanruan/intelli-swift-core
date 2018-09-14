package com.fr.swift.segment.event;

import com.fr.event.Event;
import com.fr.swift.segment.SegmentKey;

/**
 * @author anchore
 * @date 2018/9/11
 */
public class SegmentEvent<V> implements Event<V> {

    /**
     * 将realtime块转为history块
     */
    public static final Event<SegmentKey> TRANSFER_REALTIME = new SegmentEvent<SegmentKey>();

    /**
     * 上传history块到repo
     */
    public static final Event<SegmentKey> UPLOAD_HISTORY = new SegmentEvent<SegmentKey>();

    /**
     * 删repo的history块
     */
    public static final Event<SegmentKey> UNLOAD_HISTORY = new SegmentEvent<SegmentKey>();

    /**
     * 上传history块的all show到repo
     */
    public static final Event<SegmentKey> MASK_HISTORY = new SegmentEvent<SegmentKey>();
}
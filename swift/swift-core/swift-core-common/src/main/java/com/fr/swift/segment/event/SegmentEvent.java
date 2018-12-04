package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEvent;

/**
 * @author anchore
 * @date 2018/9/11
 */
public enum SegmentEvent implements SwiftEvent {

    /**
     * 将realtime块转为history块
     */
    TRANSFER_REALTIME,

    /**
     * 上传history块到repo
     */
    UPLOAD_HISTORY,

    /**
     * 删repo的history块
     */
    REMOVE_HISTORY,

    /**
     * 上传history块的all show到repo
     */
    MASK_HISTORY
}
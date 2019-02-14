package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.basics.handler.SyncDataProcessHandler;
import com.fr.swift.segment.SegmentKey;

import java.util.Set;

/**
 * @author anchore
 * @date 2019/1/22
 */
public interface UploadService extends SwiftService {

    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    void upload(Set<SegmentKey> segKeys) throws Exception;

    @InvokeMethod(value = SyncDataProcessHandler.class, target = Target.HISTORY)
    void download(Set<SegmentKey> segKeys, boolean replace) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    void uploadAllShow(Set<SegmentKey> segKeys) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    void downloadAllShow(Set<SegmentKey> segKeys) throws Exception;
}
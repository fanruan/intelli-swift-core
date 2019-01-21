package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonLoadProcessHandler;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.basics.handler.DeleteSegmentProcessHandler;
import com.fr.swift.basics.handler.SyncDataProcessHandler;
import com.fr.swift.db.Where;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/5
 */
public interface HistoryService extends SwiftService, DeleteService {

    @InvokeMethod(value = SyncDataProcessHandler.class, target = Target.HISTORY)
    void load(Set<SegmentKey> sourceSegKeys, boolean replace) throws Exception;

    /**
     * 从共享存储加载
     *
     * @param remoteUris
     * @throws IOException
     */
    void load(Map<SourceKey, Set<String>> remoteUris, boolean replace) throws Exception;

    @InvokeMethod(value = CommonLoadProcessHandler.class, target = Target.HISTORY)
    void commonLoad(SourceKey sourceKey, Map<SegmentKey, List<String>> needLoad) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.HISTORY)
    void truncate(SourceKey sourceKey);

    @Override
    @InvokeMethod(value = DeleteSegmentProcessHandler.class, target = Target.HISTORY)
    boolean delete(SourceKey sourceKey, Where where, List<String> segKeys) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.HISTORY)
    void removeHistory(List<SegmentKey> needRemoveList);
}

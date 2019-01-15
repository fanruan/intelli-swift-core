package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.basics.handler.DeleteSegmentProcessHandler;
import com.fr.swift.basics.handler.InsertSegmentProcessHandler;
import com.fr.swift.db.Where;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/5/28
 */
public interface RealtimeService extends SwiftService, DeleteService {
    /**
     * 增量导入
     *
     * @param tableKey  表
     * @param resultSet 数据
     */
    @InvokeMethod(value = InsertSegmentProcessHandler.class, target = Target.REAL_TIME)
    void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception;

    /**
     * 恢复增量数据
     *
     * @param segKeys seg key
     */
    @InvokeMethod(value = CommonProcessHandler.class, target = Target.REAL_TIME)
    void recover(List<SegmentKey> segKeys) throws Exception;


    @Override
    @InvokeMethod(value = DeleteSegmentProcessHandler.class, target = Target.REAL_TIME)
    boolean delete(SourceKey sourceKey, Where where, List<String> segKeys) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.REAL_TIME)
    void truncate(SourceKey sourceKey);
}
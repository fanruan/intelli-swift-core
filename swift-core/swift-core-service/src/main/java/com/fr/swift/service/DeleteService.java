package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.db.Where;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/11/8
 */
public interface DeleteService extends SwiftService {
    /**
     * delete
     *
     * @param tableKey table key
     * @param where    where
     * @return 是否提交成功
     * @throws Exception 异常
     */
    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    boolean delete(SourceKey tableKey, Where where) throws Exception;
}
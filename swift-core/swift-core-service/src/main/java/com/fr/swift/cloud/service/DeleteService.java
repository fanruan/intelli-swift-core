package com.fr.swift.cloud.service;

import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.source.SourceKey;

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
    boolean delete(SourceKey tableKey, Where where) throws Exception;
}
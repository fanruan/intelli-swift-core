package com.fr.swift.exception.service;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionInfo;

/**
 * @author Marvin
 * @date 8/16/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
public class SwiftExceptionInfoService implements ExceptionInfoService {
    @Override
    public ExceptionInfo getExceptionInfo(String sourceNodeId, String operateNodeId, ExceptionInfo.State state) {
        return null;
    }

    @Override
    public boolean deleteExceptionInfo(String id) {
        return false;
    }

    @Override
    public boolean maintain(ExceptionInfo info) {
        return false;
    }
}

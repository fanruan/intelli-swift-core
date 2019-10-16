package com.fr.swift.exception.handler;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.UploadExceptionContext;
import com.fr.swift.exception.inspect.ComponentHealthCheck;
import com.fr.swift.exception.inspect.SwiftRepositoryHealthInspector;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.SwiftUploadService;

import java.util.Collections;

/**
 * @author Marvin
 * @date 8/22/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
@RegisterExceptionHandler
public class UploadSegExceptionHandler implements ExceptionHandler {

    @SwiftAutoWired
    private SwiftUploadService uploadService;

    private ComponentHealthCheck repositoryChecker = new ComponentHealthCheck(SwiftRepositoryHealthInspector.getInstance(), 30000);

    @Override
    public boolean handleException(ExceptionInfo info) {

        if (repositoryChecker.isHealthy()) {
            SegmentKey key = ((UploadExceptionContext) info.getContext()).getSegmentKey();
            if (!((UploadExceptionContext) info.getContext()).isAllShow()) {
                uploadService.upload(Collections.singleton(key));
                return true;
            } else {
                uploadService.uploadAllShow(Collections.singleton(key));
                return true;
            }
        }
        //需要上传的seg只有本节点有，通知master也无法处理。因此考虑直接过掉异常，手动去数据库里查找FAILED的task重新上传
        return true;
    }

    @Override
    public ExceptionInfo.Type getExceptionInfoType() {
        return ExceptionInfoType.UPLOAD_SEGMENT;
    }

}

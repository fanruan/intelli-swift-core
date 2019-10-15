package com.fr.swift.exception;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.exception.inspect.executor.retry.RetryTask;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.SwiftUploadService;

import java.util.Collections;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 10/14/2019
 */
public class UploadSegRetryTask implements RetryTask {

    ExceptionInfo info;
    @SwiftAutoWired
    private SwiftUploadService uploadService;

    public UploadSegRetryTask(ExceptionInfo info) {
        this.info = info;
    }

    @Override
    public void retry() {
        SegmentKey key = ((UploadExceptionContext) info.getContext()).getSegmentKey();
        if (!((UploadExceptionContext) info.getContext()).isAllShow()) {
            uploadService.upload(Collections.singleton(key));
        } else {
            uploadService.uploadAllShow(Collections.singleton(key));

        }
    }
}

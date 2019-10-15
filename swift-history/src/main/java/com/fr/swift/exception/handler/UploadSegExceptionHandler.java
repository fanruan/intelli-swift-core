package com.fr.swift.exception.handler;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.UploadSegRetryTask;
import com.fr.swift.exception.inspect.executor.retry.RepositoryRetryTaskQueue;
import com.fr.swift.exception.inspect.executor.retry.RetryTask;

/**
 * @author Marvin
 * @date 8/22/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
@RegisterExceptionHandler
public class UploadSegExceptionHandler implements ExceptionHandler {

    @Override
    public boolean handleException(final ExceptionInfo info) {

        RetryTask task = new UploadSegRetryTask(info);
        RepositoryRetryTaskQueue.getInstance().offer(task);

        //需要上传的seg只有本节点有，通知master也无法处理。因此考虑直接过掉异常，手动去数据库里查找FAILED的task重新上传
        return true;
    }

    @Override
    public ExceptionInfo.Type getExceptionInfoType() {
        return ExceptionInfoType.UPLOAD_SEGMENT;
    }

}

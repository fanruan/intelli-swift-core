package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonProcessHandler;

/**
 * This class created on 2018/7/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface BaseService {
    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ALL)
    void cleanMetaCache(String[] sourceKeys);

}

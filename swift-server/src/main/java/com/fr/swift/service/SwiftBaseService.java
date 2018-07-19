package com.fr.swift.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/7/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("baseService")
public class SwiftBaseService implements BaseService {

    @Override
    @RpcMethod(methodName = "cleanMetaCache")
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.getInstance().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }

}

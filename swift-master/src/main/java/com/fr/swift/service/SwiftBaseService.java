package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftMetaDataService;

/**
 * This class created on 2018/7/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "baseService")
public class SwiftBaseService implements BaseService {

    @Override
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.get().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }

}

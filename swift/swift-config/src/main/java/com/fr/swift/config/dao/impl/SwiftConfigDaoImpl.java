package com.fr.swift.config.dao.impl;

import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/7/6
 */
@Service("swiftConfigDao")
public class SwiftConfigDaoImpl extends BasicDao<SwiftConfigEntity> implements SwiftConfigDao<SwiftConfigEntity> {
    public SwiftConfigDaoImpl() {
        super(SwiftConfigEntity.class);
    }


}

package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;

/**
 * @author yee
 * @date 2018/7/6
 */
@SwiftBean
public class SwiftConfigDaoImpl extends BasicDao<SwiftConfigEntity> implements SwiftConfigDao<SwiftConfigEntity> {
    public SwiftConfigDaoImpl() {
        super(SwiftConfigEntity.class);
    }
}

package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftTablePathDao;
import com.fr.swift.config.entity.SwiftTablePathEntity;

/**
 * @author yee
 * @date 2018-11-28
 */
@SwiftBean
public class SwiftTablePathDaoImpl extends BasicDao<SwiftTablePathEntity> implements SwiftTablePathDao {
    public SwiftTablePathDaoImpl() {
        super(SwiftTablePathEntity.class);
    }
}

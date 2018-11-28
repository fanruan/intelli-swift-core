package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftColumnIdxConfBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;
import com.fr.swift.config.service.SwiftColumnIdxConfDao;

/**
 * @author yee
 * @date 2018-11-27
 */
public class SwiftColumnIdxConfDaoImpl extends BasicDao<SwiftColumnIdxConfBean> implements SwiftColumnIdxConfDao {
    public SwiftColumnIdxConfDaoImpl() {
        super(SwiftColumnIdxConfBean.TYPE, RestrictionFactoryImpl.INSTANCE);
    }
}

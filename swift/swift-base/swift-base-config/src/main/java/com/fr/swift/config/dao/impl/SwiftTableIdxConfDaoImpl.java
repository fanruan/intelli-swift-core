package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftTableIdxConfBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftTableIdxConfDao;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;

/**
 * @author yee
 * @date 2018-11-27
 */
public class SwiftTableIdxConfDaoImpl extends BasicDao<SwiftTableIdxConfBean> implements SwiftTableIdxConfDao {
    public SwiftTableIdxConfDaoImpl() {
        super(SwiftTableIdxConfBean.TYPE, RestrictionFactoryImpl.INSTANCE);
    }
}

package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.impl.RestrictionFactoryImpl;

/**
 * @author yee
 * @date 2018/7/6
 */
public class SwiftConfigDaoImpl extends BasicDao<SwiftConfigBean> implements SwiftConfigDao<SwiftConfigBean> {
    public SwiftConfigDaoImpl() {
        super(SwiftConfigBean.TYPE, RestrictionFactoryImpl.INSTANCE);
    }
}

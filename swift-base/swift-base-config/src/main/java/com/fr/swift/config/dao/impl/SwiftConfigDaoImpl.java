package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;

/**
 * @author yee
 * @date 2018/7/6
 */
@SwiftBean
public class SwiftConfigDaoImpl extends BasicDao<SwiftConfigBean> implements SwiftConfigDao<SwiftConfigBean> {
    public SwiftConfigDaoImpl() {
        super(SwiftConfigBean.TYPE);
    }
}

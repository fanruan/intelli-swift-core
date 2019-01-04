package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftTablePathDao;

/**
 * @author yee
 * @date 2018-11-28
 */
@SwiftBean
public class SwiftTablePathDaoImpl extends BasicDao<SwiftTablePathBean> implements SwiftTablePathDao {
    public SwiftTablePathDaoImpl() {
        super(SwiftTablePathBean.TYPE);
    }
}

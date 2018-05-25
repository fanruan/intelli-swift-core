package com.fr.swift.config.conf.controller;


import com.fr.decision.authority.controller.BaseController;
import com.fr.decision.base.annotation.DataOperatorAction;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public interface MetaDataController extends BaseController<SwiftMetaDataBean> {
    /**
     * 根据TableName查询行权限MetaData
     *
     * @param tableName tableName
     * @return
     * @throws Exception 异常
     */
    @DataOperatorAction(actionType = DataOperatorAction.ActionType.QUERY)
    SwiftMetaDataBean findByTableName(String tableName) throws Exception;

    @DataOperatorAction(actionType = DataOperatorAction.ActionType.QUERY)
    SwiftMetaDataBean findBySourceKey(String sourceKey) throws Exception;
}

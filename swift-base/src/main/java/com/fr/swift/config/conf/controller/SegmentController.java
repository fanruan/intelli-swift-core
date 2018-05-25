package com.fr.swift.config.conf.controller;

import com.fr.decision.authority.controller.BaseController;
import com.fr.decision.base.annotation.DataOperatorAction;
import com.fr.swift.config.conf.bean.SegmentBean;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/24
 */
public interface SegmentController extends BaseController<SegmentBean> {
    /**
     * 根据sourceKsy查询行权限过滤器
     *
     * @param sourceKey sourceKey
     * @return
     * @throws Exception 异常
     */
    @DataOperatorAction(actionType = DataOperatorAction.ActionType.QUERY)
    List<SegmentBean> findBySourceKey(String sourceKey) throws Exception;

    @DataOperatorAction(actionType = DataOperatorAction.ActionType.QUERY)
    void removeBySourceKey(String sourceKey) throws Exception;
}

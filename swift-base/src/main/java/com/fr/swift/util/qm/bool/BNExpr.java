package com.fr.swift.util.qm.bool;

import java.util.List;

/**
 * Created by Lyon on 2018/7/6.
 */
public interface BNExpr extends BExpr {

    /**
     * 获取子表达式
     */
    List<? extends BExpr> getChildrenExpr();
}

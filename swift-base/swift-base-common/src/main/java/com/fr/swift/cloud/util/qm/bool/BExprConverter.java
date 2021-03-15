package com.fr.swift.cloud.util.qm.bool;

import java.util.List;

/**
 * Created by Lyon on 2018/7/10.
 */
public interface BExprConverter {

    BExpr convertAndExpr(List<? extends BExpr> items);

    BExpr convertOrExpr(List<? extends BExpr> items);

    BExpr convertNotExpr(BVar var);
}

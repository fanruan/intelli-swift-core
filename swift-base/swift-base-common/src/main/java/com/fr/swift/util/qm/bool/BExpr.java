package com.fr.swift.util.qm.bool;

/**
 * Created by Lyon on 2018/7/6.
 */
public interface BExpr {

    BExpr TRUE = new BExpr() {
        @Override
        public BExprType type() {
            return BExprType.TRUE;
        }
    };

    BExpr FALSE = new BExpr() {
        @Override
        public BExprType type() {
            return BExprType.FALSE;
        }
    };

    /**
     * 布尔表达式类型
     */
    BExprType type();
}

package com.fr.swift.bitmap.traversal;

/**
 * @author pony
 */
public interface BreakTraversalAction {

    /**
     * 处理序号，可中断
     *
     * @param row 序号
     */
    boolean actionPerformed(int row);

}
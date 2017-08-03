package com.fr.bi.cal.analyze.executor;

import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.json.JSONObject;

import java.awt.*;


/**
 * 定义executor接口的目的就应该是操作组件从不同角度来获取组件各种
 * 包括:
 * 前端显示的值 getCubeNode,
 * 所以能够进行显示的gvi
 * 点击区域代表的节点的gvi
 */
public interface BIEngineExecutor<T> {

    static final String NONEVALUE = "--";

    /**
     * 创建excel单元格迭代器
     *
     * @return detailCellIterator
     * @throws Exception
     */
    public TableCellIterator createCellIterator4Excel() throws Exception;

    public Rectangle getSouthEastRectangle();

    /**
     * 获取node
     *
     * @return 获取的node
     */
    public T getCubeNode() throws Exception;

    public JSONObject createJSONObject() throws Exception;

}
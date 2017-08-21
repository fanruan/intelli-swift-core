package com.fr.bi.cal.analyze.executor;

import com.fr.bi.export.iterator.TableCellIterator;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.awt.*;


public interface BIEngineExecutor<T> {
    static final String NONEVALUE = StringUtils.EMPTY;

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
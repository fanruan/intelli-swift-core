package com.fr.bi.cal.analyze.executor;

import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.json.JSONObject;

import java.awt.*;
import java.util.List;


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

    public List<MetricGroupInfo> getLinkedWidgetFilterGVIList() throws Exception;
}
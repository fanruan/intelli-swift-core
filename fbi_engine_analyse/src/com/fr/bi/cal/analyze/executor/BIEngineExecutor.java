package com.fr.bi.cal.analyze.executor;

import com.fr.bi.export.iterator.TableCellIterator;
import com.fr.bi.report.result.BIResult;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.awt.*;


public interface BIEngineExecutor<T> {

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

    /**
     * 获取导出的数据结构
     * 不在直接从cube里面拿了，这里做统一的结构，应该尽量让计算得出的数据结构和cube于直连这些计算逻辑相分离
     *
     * @return
     */
    BIResult getResult() throws Exception;
}
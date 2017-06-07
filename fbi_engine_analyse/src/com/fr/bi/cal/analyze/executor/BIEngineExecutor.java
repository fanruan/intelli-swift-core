package com.fr.bi.cal.analyze.executor;

import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.result.NewCrossRoot;
import com.fr.bi.cal.analyze.cal.result.Node;
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

    /**
     * 获取到某一行就停止构建的Node节点树
     * @param rowData
     * @return
     * @throws Exception
     */
    public Node getStopOnRowNode(Object[] rowData) throws Exception;

    /**
     *
     * @param rowData       行值
     * @param colData       列值
     * @return
     * @throws Exception
     */
    public NewCrossRoot getStopOnRowNode(Object[] rowData, Object[] colData) throws Exception;
}
package com.fr.bi.cal.analyze.executor;

import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.detail.DetailCellIterator;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.json.JSONObject;

import java.awt.*;

public interface BIEngineExecutor<T> {
    static final String NONEVALUE = "--";

    public DetailCellIterator createCellIterator4Excel() throws Exception;

    /**
     * 构建cells
     *
     * @return 构建的cells
     * @throws NoneAccessablePrivilegeException
     */
    public CBCell[][] createCellElement() throws Exception;

    public Rectangle getSouthEastRectangle();

    /**
     * 获取node
     *
     * @return 获取的node
     */
    public T getCubeNode() throws Exception;

    public JSONObject createJSONObject() throws Exception;
}
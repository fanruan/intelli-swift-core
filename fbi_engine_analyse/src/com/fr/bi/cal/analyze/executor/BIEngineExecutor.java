package com.fr.bi.cal.analyze.executor;

import com.fr.bi.report.result.BIResult;
import com.fr.json.JSONObject;

public interface BIEngineExecutor<T> {
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
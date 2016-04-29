package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/9/29.
 */
public abstract class AbstractComplexNodeExecutor extends BIComplexExecutor<Node>{
    protected BIComplexExecutData rowData;
    protected AbstractComplexNodeExecutor(TableWidget widget, Paging page, BISession session, ComplexExpander expander) {
        super(widget, page, session, expander);
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        Iterator<Map.Entry<Integer, Node>> it = getCubeNodes().entrySet().iterator();
        JSONObject jo = new JSONObject();
        while (it.hasNext()){
            Map.Entry<Integer, Node> entry = it.next();
            jo.put(String.valueOf(entry.getKey()), entry.getValue().toJSONObject(rowData.getDimensionArray(entry.getKey()), widget.getTargetsKey(), -1));
        }
        return jo;
    }
}
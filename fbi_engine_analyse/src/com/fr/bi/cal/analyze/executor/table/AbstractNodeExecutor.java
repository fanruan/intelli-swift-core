package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2015/9/29.
 */
public abstract class AbstractNodeExecutor extends BITableExecutor<Node>{
    protected AbstractNodeExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
    }

    @Override
    public JSONObject createJSONObject() throws JSONException {
        return getCubeNode().toJSONObject(usedDimensions, widget.getTargetsKey(), -1);
    }
}
package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2015/9/29.
 */
public abstract class AbstractNodeExecutor extends BITableExecutor<Node>{
    protected AbstractNodeExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        return getCubeNode().toJSONObject(usedDimensions, widget.getTargetsKey(), -1);
    }

    protected static String getDimensionJSONString(BIDimension[] rowColumn, int dimensionIndex, Node node) {
        JSONArray ja = JSONArray.create();
        while (dimensionIndex != -1 && node != null) {
            try {
                ja.put(JSONObject.create().put(rowColumn[dimensionIndex].getValue(), rowColumn[dimensionIndex].toFilterObject(node.getData())));
            } catch (Exception e) {
            }
            node = node.getParent();
            dimensionIndex--;
        }
        return ja.toString();
    }
}
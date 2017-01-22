package com.fr.bi.cal.analyze.executor.treelabel;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeLabelWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.awt.*;

public class TreeLabelExecutor extends BIAbstractExecutor<JSONObject> {
    protected transient BusinessTable target;
    protected transient BIDimension[] viewDimension;
    protected transient long userId;
    protected TreeLabelWidget widget;

    public TreeLabelExecutor(TreeLabelWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
        this.target = widget.getTargetTable();
        this.widget = widget;
        this.session = session;

        this.viewDimension = widget.getViewDimensions();
        this.userId = session.getUserId();

    }


    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        return new CBCell[0][];
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return null;
    }

    @Override
    public JSONObject getCubeNode() throws JSONException {
        return null;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        return null;
    }
}

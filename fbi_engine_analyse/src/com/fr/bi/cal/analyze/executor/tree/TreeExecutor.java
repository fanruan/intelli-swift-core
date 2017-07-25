package com.fr.bi.cal.analyze.executor.tree;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.imp.TreeWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.json.JSONObject;

import java.awt.*;
/**
 * Created by roy on 16/4/29.
 */
public class TreeExecutor extends BIAbstractExecutor<JSONObject> {
    protected transient BusinessTable target;
    protected transient BIDimension[] viewDimension;
    protected transient long userId;
    protected TreeWidget widget;

    public TreeExecutor(TreeWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
        this.target = widget.getTargetTable();
        this.widget = widget;
        this.session = session;

        this.viewDimension = widget.getViewDimensions();
        this.userId = session.getUserId();

    }


    @Override
    public TableCellIterator createCellIterator4Excel() throws Exception {
        return null;
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return null;
    }

    @Override
    public JSONObject getCubeNode() throws Exception {
        return null;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        return null;
    }
}

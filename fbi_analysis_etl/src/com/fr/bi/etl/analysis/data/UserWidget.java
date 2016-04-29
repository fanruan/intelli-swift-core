package com.fr.bi.etl.analysis.data;

import com.fr.bi.cal.analyze.report.report.widget.BIAbstractWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.fr.json.JSONObject;
import com.fr.report.poly.TemplateBlock;

import java.util.List;

/**
 * Created by 小灰灰 on 2016/4/12.
 */
public class UserWidget extends BIAbstractWidget {

    private BIWidget widget;

    private long userId;

    public UserWidget(BIWidget widget, long userId) {
        this.widget = widget;
        this.userId = userId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return null;
    }

    @Override
    public <T extends BITargetAndDimension> T[] getDimensions() {
        return widget.getDimensions();
    }

    @Override
    public <T extends BITargetAndDimension> T[] getViewDimensions() {
        return widget.getViewDimensions();
    }

    @Override
    public <T extends BITargetAndDimension> T[] getTargets() {
        return widget.getTargets();
    }

    @Override
    public <T extends BITargetAndDimension> T[] getViewTargets() {
        return widget.getViewTargets();
    }

    @Override
    public List<Table> getUsedTableDefine() {
        return widget.getUsedTableDefine();
    }

    @Override
    public List<BIField> getUsedFieldDefine() {
        return widget.getUsedFieldDefine();
    }

    @Override
    public int isOrder() {
        return widget.isOrder();
    }

    @Override
    public JSONObject createDataJSON(BISessionProvider session) throws Exception {
        return widget.createDataJSON(session);
    }
}

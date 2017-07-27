package com.fr.bi.cal.analyze.report.report.widget.imp;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.group.group.date.SeasonGroup;
import com.fr.bi.stable.operation.group.group.date.YearGroup;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class QuarterControlWidget extends TableWidget {

    @Override
    public int isOrder() {
        return 0;
    }

    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {
        BIDimension dimension = getDimensions()[0];

        JSONObject jo = JSONObject.create();

        //算年份
        YearGroup group = new YearGroup();
        group.setType(BIReportConstant.GROUP.Y);
        dimension.setGroup(group);
        jo.put("year", createYearOrQuarterData(dimension, session));

        //算季度
        SeasonGroup season = new SeasonGroup();
        season.setType(BIReportConstant.GROUP.S);
        dimension.setGroup(season);
        jo.put("quarter", createYearOrQuarterData(dimension, session));
        return jo;
    }

    private List<Integer> createYearOrQuarterData(BIDimension dimension, BISessionProvider session) {
        DimensionCalculator calculator = dimension.createCalculator(dimension.getStatisticElement(), new ArrayList<BITableSourceRelation>());

        ICubeColumnIndexReader reader = calculator.createNoneSortGroupValueMapGetter(dimension.getStatisticElement().getTableBelongTo(), session.getLoader());
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0, len = reader.sizeOfGroup(); i < len; i++) {
            list.add((Integer) reader.getGroupValue(i));
        }
        return list;
    }

    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyECBlock();
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
    }

    @Override
    public WidgetType getType() {
        return WidgetType.QUARTER;
    }

}
package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.Widget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;

import java.util.*;

/**
 * Created by zcf on 2017/1/20.
 */
public class SingleSliderWidget extends TableWidget {
    private Widget type;
    private double minMin;
    private double maxMax;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("type")) {
            type = Widget.parse(jo.getInt("type"));
        }
    }

    public JSONObject createDataJSON(BISessionProvider session) throws JSONException {
        BIDimension[] dimensions = getDimensions();
        for (int i = 0; i < dimensions.length; i++) {
            BIDimension dimension = dimensions[i];
            DimensionCalculator calculator = dimension.createCalculator(dimension.getStatisticElement(), new ArrayList<BITableSourceRelation>());
            GroupValueIndex gvi = createFilterGVI(new DimensionCalculator[]{calculator}, dimension.getStatisticElement().getTableBelongTo(), session.getLoader(), session.getUserId());
            ICubeColumnIndexReader reader = calculator.createNoneSortGroupValueMapGetter(dimension.getStatisticElement().getTableBelongTo(), session.getLoader());
            ICubeTableService ti = session.getLoader().getTableIndex(dimension.getStatisticElement().getTableBelongTo().getTableSource());
            ICubeValueEntryGetter getter = ti.getValueEntryGetter(dimension.createKey(dimension.getStatisticElement()), new ArrayList<BITableSourceRelation>());
            MaxAndMin maxAndMin = createIDGroupIndex(gvi, reader, getter, calculator.getComparator());
            updateMaxAndMin(i, maxAndMin);
        }
        JSONObject jo = JSONObject.create();
        jo.put("max", this.maxMax);
        jo.put("min", this.minMin);
        return jo;
    }

    private void updateMaxAndMin(int i, MaxAndMin maxAndMin) {
        if (i == 0) {
            minMin = maxAndMin.getMin();
            maxMax = maxAndMin.getMax();
        } else {
            if (maxAndMin.getMin() < minMin) {
                minMin = maxAndMin.getMin();
            }
            if (maxAndMin.getMax() > maxMax) {
                maxMax = maxAndMin.getMax();
            }
        }
    }

    private MaxAndMin createIDGroupIndex(GroupValueIndex gvi, ICubeColumnIndexReader reader, final ICubeValueEntryGetter getter, Comparator comparator) throws JSONException {
        int start = 0, end = getter.getGroupSize();
        SimpleIntArray groupArray = this.createGroupArray(start, end, new int[0], new int[0], getter, gvi);

        Object min = reader.getGroupValue(groupArray.get(groupArray.get(0)));
        Object max = reader.getGroupValue(groupArray.get(groupArray.size() - 1));
        return new MaxAndMin(Double.valueOf(max.toString()), Double.valueOf(min.toString()));
    }

    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyECBlock();
    }

    @Override
    public int isOrder() {
        return 0;
    }

    @Override
    public Widget getType() {
        return this.type;
    }

    private class MaxAndMin {
        double min;
        double max;

        MaxAndMin(double max, double min) {
            this.max = max;
            this.min = min;
        }

        public double getMin() {
            return this.min;
        }

        public double getMax() {
            return this.max;
        }
    }

}

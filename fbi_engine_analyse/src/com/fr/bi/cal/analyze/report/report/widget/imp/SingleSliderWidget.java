package com.fr.bi.cal.analyze.report.report.widget.imp;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by zcf on 2017/1/20.
 */
public class SingleSliderWidget extends TableWidget {

    private WidgetType type;

    private double minMin;

    private double maxMax;

    private double filterMinMin;

    private double filterMaxMax;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

        super.parseJSON(jo, userId);
        if (jo.has("type")) {
            type = WidgetType.parse(jo.getInt("type"));
        }
    }

    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws JSONException {

        updateMinMax(true, session);
        updateMinMax(false, session);
        JSONObject jo = JSONObject.create();
        jo.put("max", this.maxMax);
        jo.put("min", this.minMin);
        jo.put("filterMax", this.filterMaxMax);
        jo.put("filterMin", this.filterMinMin);
        return jo;
    }

    private void updateMinMax(boolean needFilter, BISessionProvider session) throws JSONException {

        if (needFilter == false) {
            this.setFilter(null);
        }
        BIDimension[] dimensions = getDimensions();
        for (int i = 0; i < dimensions.length; i++) {
            BIDimension dimension = dimensions[i];
            DimensionCalculator calculator = dimension.createCalculator(dimension.getStatisticElement(), new ArrayList<BITableSourceRelation>());
            GroupValueIndex gvi = createFilterGVI(new DimensionCalculator[]{calculator}, dimension.getStatisticElement().getTableBelongTo(), session.getLoader(), session.getUserId());
            ICubeColumnIndexReader reader = calculator.createNoneSortGroupValueMapGetter(dimension.getStatisticElement().getTableBelongTo(), session.getLoader());
            ICubeTableService ti = session.getLoader().getTableIndex(dimension.getStatisticElement().getTableBelongTo().getTableSource());
            ICubeValueEntryGetter getter = ti.getValueEntryGetter(dimension.createKey(dimension.getStatisticElement()), new ArrayList<BITableSourceRelation>());
            MaxAndMin maxAndMin = createIDGroupIndex(gvi, reader, getter, calculator.getComparator());
            if (needFilter) {
                updateMaxAndMinWithFilter(i, maxAndMin);
            } else {
                updateMaxAndMin(i, maxAndMin);
            }
        }
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

    private void updateMaxAndMinWithFilter(int i, MaxAndMin maxAndMin) {

        if (i == 0) {
            filterMinMin = maxAndMin.getMin();
            filterMaxMax = maxAndMin.getMax();
        } else {
            if (maxAndMin.getMin() < filterMinMin) {
                filterMinMin = maxAndMin.getMin();
            }
            if (maxAndMin.getMax() > filterMaxMax) {
                filterMaxMax = maxAndMin.getMax();
            }
        }
    }

    private MaxAndMin createIDGroupIndex(GroupValueIndex gvi, ICubeColumnIndexReader reader, final ICubeValueEntryGetter getter, Comparator comparator) throws JSONException {

        int start = 0, end = getter.getGroupSize();
        SimpleIntArray groupArray = this.createGroupArray(start, end, new int[0], new int[0], getter, gvi);
        // 因为存在过滤gvi,会导致分组大小并不是groupSize,所以end需要返回的分组的大小
        end = groupArray.size();
        Object min = null;
        Object max = null;
        for (int i = 0; i < end; i++) {
            Object tmin = reader.getGroupValue(groupArray.get(i));
            if (BICollectionUtils.isNotCubeNullKey(tmin)) {
                min = tmin;
                break;
            }
        }
        for (int i = end - 1; i >= 0; i--) {
            Object tmax = reader.getGroupValue(groupArray.get(i));
            if (BICollectionUtils.isNotCubeNullKey(tmax)) {
                max = tmax;
                break;
            }
        }
        if (min == null && max == null) {
            return new MaxAndMin(0, 0);
        }
        return new MaxAndMin(Double.valueOf(max.toString()), Double.valueOf(min.toString()));


    }

    @Override
    public int isOrder() {

        return 0;
    }

    @Override
    public WidgetType getType() {

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

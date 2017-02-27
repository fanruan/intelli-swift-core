package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.gvi.AllShowRoaringGroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;
import com.fr.stable.collections.array.IntArray;

import java.util.*;

/**
 * Created by zcf on 2017/1/20.
 */
public class SingleSliderWidget extends TableWidget {
    private int type;
    private double minMin;
    private double maxMax;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("type")) {
            type = jo.getInt("type");
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
        SingleSliderWidget.SimpleIntArray groupArray;
        if (gvi instanceof AllShowRoaringGroupValueIndex) {
            final int fstart = start, size = start == -1 ? 0 : end - start;
            groupArray = new SingleSliderWidget.SimpleIntArray() {
                @Override
                public int get(int index) {
                    return index + fstart;
                }

                @Override
                public int size() {
                    return size;
                }
            };
        } else {
            groupArray = createGroupArray(start, end, getter, gvi);
        }
        Object min = reader.getGroupValue(groupArray.get(groupArray.get(0)));
        Object max = reader.getGroupValue(groupArray.get(groupArray.get(groupArray.size() - 1)));
        return new MaxAndMin(Double.valueOf(max.toString()), Double.valueOf(min.toString()));
    }

    private SimpleIntArray createGroupArray(int start, int end, final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
        final int[] groupIndex = new int[getter.getGroupSize()];
        Arrays.fill(groupIndex, NIOConstant.INTEGER.NULL_VALUE);
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                    groupIndex[groupRow] = groupRow;
                }
            }
        });
        final IntArray array = new IntArray();
        if (start != -1) {
            for (int i = start; i < end; i++) {
                if (groupIndex[i] != NIOConstant.INTEGER.NULL_VALUE) {
                    array.add(i);
                }
            }
        }
        return new SimpleIntArray() {
            @Override
            public int get(int index) {
                return array.get(index);
            }

            @Override
            public int size() {
                return array.size;
            }
        };
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
    public WidgetType getType() {
        return WidgetType.SINGLE_SLIDER;
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

    private abstract class SimpleIntArray {
        public abstract int get(int index);

        public abstract int size();
    }
}

BI.ChartCombineFormatItemFactory = {
    combineItems: function(types, items){
        var id = BI.UUID();
        var result=[];
        BI.each(items, function(idx, item){
            var it = BI.ChartCombineFormatItemFactory.formatItems(types[idx], item);
            if(it.stack === true){
                it.stack = id;
            }else {
                delete it.stack;
            }
            result.push(it);
        });
        return result;
    },
    formatItems: function(type, items){
        switch (type) {
            case BICst.WIDGET.BAR:
                return BI.extend({"type": "bar"}, BI.BarChart.formatItems(items));
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                return BI.extend({"type": "area"}, BI.PercentAccumulateAreaChart.formatItems(items));
            case BICst.WIDGET.BUBBLE:
                return BI.extend({"type": "bubble"}, BI.BubbleChart.formatItems(items));
            case BICst.WIDGET.SCATTER:
                return BI.extend({"type": "scatter"}, BI.ScatterChart.formatItems(items));
            case BICst.WIDGET.AXIS:
                return BI.extend({"type": "column"}, BI.AxisChart.formatItems(items));
            case BICst.WIDGET.ACCUMULATE_AXIS:
                return BI.extend({"type": "column"}, BI.AccumulateAxisChart.formatItems(items));
            case BICst.WIDGET.COMPARE_BAR:
                return BI.extend({"type": "bar"}, BI.LineChart.formatItems(items));
            case BICst.WIDGET.COMPARE_AXIS:
                return BI.extend({"type": "column"}, BI.LineChart.formatItems(items));
            case BICst.WIDGET.COMPARE_AREA:
                return BI.extend({"type": "area"}, BI.LineChart.formatItems(items));
            case BICst.WIDGET.RANGE_AREA:
                return BI.extend({"type": "area"}, BI.LineChart.formatItems(items));
            case BICst.WIDGET.LINE:
                return BI.extend({"type": "line"}, BI.LineChart.formatItems(items));
            case BICst.WIDGET.AREA:
                return BI.extend({"type": "area"}, BI.AreaChart.formatItems(items));
            case BICst.WIDGET.ACCUMULATE_AREA:
                return BI.extend({"type": "area"}, BI.AccumulateAreaChart.formatItems(items));
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                return BI.extend({"type": "column"}, BI.PercentAccumulateAxisChart.formatItems(items));
            case BICst.WIDGET.ACCUMULATE_BAR:
                return BI.extend({"type": "bar"}, BI.AccumulateBarChart.formatItems(items));
            case BICst.WIDGET.FALL_AXIS:
                return BI.extend({"type": "column"}, BI.LineChart.formatItems(items));
            case BICst.WIDGET.DONUT:
                return BI.extend({"type": "donut"}, BI.DonutChart.formatItems(items));
            case BICst.WIDGET.RADAR:
                return BI.extend({"type": "radar"}, BI.RadarChart.formatItems(items));
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return BI.extend({"type": "radar"}, BI.AccumulateRadarChart.formatItems(items));
            case BICst.WIDGET.PIE:
                return BI.extend({"type": "pie"}, BI.PieChart.formatItems(items));
            case BICst.WIDGET.DASHBOARD:
                return BI.extend({"type": "dashboard"}, BI.DashboardChart.formatItems(items));
            case BICst.WIDGET.FORCE_BUBBLE:
                return BI.extend({"type": "bubble"}, BI.ForceBubbleChart.formatItems(items));
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return {};
            default:
                return BI.extend({"type": "column"}, BI.AxisChart.formatItems(items));
        }
    },


    combineConfig: function(types){
        var id = BI.UUID();
        var result;
        if(types.length > 1){
            return BI.ChartCombineFormatItemFactory.formatConfig(BICst.WIDGET.AXIS);
        }
        BI.each(types, function(idx, item){
            result = BI.ChartCombineFormatItemFactory.formatConfig(types[idx]);
        });
        return result;
    },
    formatConfig: function(type, items){
        switch (type) {
            case BICst.WIDGET.BAR:
                return BI.BarChart.formatConfig();
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                return BI.PercentAccumulateAreaChart.formatConfig();
            case BICst.WIDGET.BUBBLE:
                return BI.BubbleChart.formatConfig();
            case BICst.WIDGET.SCATTER:
                return BI.ScatterChart.formatConfig(items);
            case BICst.WIDGET.AXIS:
                return BI.AxisChart.formatConfig(items);
            case BICst.WIDGET.ACCUMULATE_AXIS:
                return BI.AccumulateAxisChart.formatConfig(items);
            case BICst.WIDGET.COMPARE_BAR:
                return BI.LineChart.formatConfig(items);
            case BICst.WIDGET.COMPARE_AXIS:
                return BI.LineChart.formatConfig(items);
            case BICst.WIDGET.COMPARE_AREA:
                return BI.LineChart.formatConfig(items);
            case BICst.WIDGET.RANGE_AREA:
                return BI.LineChart.formatConfig(items);
            case BICst.WIDGET.LINE:
                return BI.LineChart.formatConfig(items);
            case BICst.WIDGET.AREA:
                return BI.AreaChart.formatConfig(items);
            case BICst.WIDGET.ACCUMULATE_AREA:
                return BI.AccumulateAreaChart.formatConfig(items);
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                return BI.PercentAccumulateAxisChart.formatConfig(items);
            case BICst.WIDGET.ACCUMULATE_BAR:
                return BI.AccumulateBarChart.formatConfig(items);
            case BICst.WIDGET.FALL_AXIS:
                return BI.LineChart.formatConfig(items);
            case BICst.WIDGET.DONUT:
                return BI.DonutChart.formatConfig(items);
            case BICst.WIDGET.RADAR:
                return BI.RadarChart.formatConfig(items);
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return BI.AccumulateRadarChart.formatConfig(items);
            case BICst.WIDGET.PIE:
                return BI.PieChart.formatConfig(items);
            case BICst.WIDGET.DASHBOARD:
                return BI.DashboardChart.formatConfig(items);
            case BICst.WIDGET.FORCE_BUBBLE:
                return BI.ForceBubbleChart.formatConfig(items);
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return {};
            default:
                return BI.AxisChart.formatConfig(items);
        }
    },
};

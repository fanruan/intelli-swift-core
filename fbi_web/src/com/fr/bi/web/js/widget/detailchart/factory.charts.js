BI.ChartCombineFormatItemFactory = {
    formatItems: function(type, items){
        switch (type) {
            case BICst.WIDGET.BAR:
                return BI.extend({"type": "bar"}, BI.BarChart.formatItems(items));
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                return BI.extend({"type": "bar"}, BI.PercentAccumulateAreaChart.formatItems(items));
            case BICst.WIDGET.BUBBLE:
                return BI.extend({"type": "bubble"}, BI.Bubble.formatItems(items));
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
    }
};

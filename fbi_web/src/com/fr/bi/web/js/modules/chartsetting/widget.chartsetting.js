/**
 * created by young
 * 图表属性
 */
BI.ChartSetting = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-setting"
        })
    },

    _init: function () {
        BI.ChartSetting.superclass._init.apply(this, arguments);
        this.wrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
    },

    populate: function () {
        var self = this, wId = this.options.wId;
        this.wrapper.empty();
        if (BI.isNotNull(this.chartSetting)) {
            this.chartSetting.destroy();
        }
        var chartType = BI.Utils.getWidgetTypeByID(wId);
        switch (chartType) {
            case BICst.WIDGET.TABLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.group_table_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.GroupTableSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.cross_table_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.CrossTableSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                break;
            case BICst.WIDGET.DETAIL:
                this.chartSetting = BI.createWidget({
                    type: "bi.detail_table_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.DetailTable.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                this.chartSetting = BI.createWidget({
                    type: "bi.charts_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.ChartsSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                this.chartSetting = BI.createWidget({
                    type: "bi.percent_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.PercentChartsSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                this.chartSetting = BI.createWidget({
                    type: "bi.multi_axis_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.MultiAxisChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
                this.chartSetting = BI.createWidget({
                    type: "bi.line_area_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.LineAreaChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.RANGE_AREA:
                this.chartSetting = BI.createWidget({
                    type: "bi.range_area_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.RangeAreaChartsSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.DONUT:
                this.chartSetting = BI.createWidget({
                    type: "bi.donut_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.DonutChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                this.chartSetting = BI.createWidget({
                    type: "bi.bar_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.BarChartsSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                this.chartSetting = BI.createWidget({
                    type: "bi.compare_column_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.CompareColumnChartsSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.COMPARE_AREA:
                this.chartSetting = BI.createWidget({
                    type: "bi.compare_area_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.CompareAreaChartsSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.PIE:
                this.chartSetting = BI.createWidget({
                    type: "bi.pie_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.PieChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.FALL_AXIS:
                this.chartSetting = BI.createWidget({
                    type: "bi.fall_axis_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.FallAxisChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                this.chartSetting = BI.createWidget({
                    type: "bi.radar_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.RadarChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.BUBBLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.scatter_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.ScatterChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.DASHBOARD:
                this.chartSetting = BI.createWidget({
                    type: "bi.dashboard_chart_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.DashboardChartSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.FORCE_BUBBLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.force_bubble_setting",
                    wId: wId
                });
                this.chartSetting.on(BI.ForceBubbleSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
        }
        this.chartSetting.populate();
        this.wrapper.addItem({
            el: this.chartSetting,
            top: 0,
            left: 0,
            bottom: 0,
            right: 0
        });
    }
});
BI.ChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_setting", BI.ChartSetting);
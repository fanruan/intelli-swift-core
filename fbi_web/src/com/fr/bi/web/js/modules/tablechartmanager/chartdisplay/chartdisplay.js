/**
 * 图表控件
 * @class BI.ChartDisplay
 * @extends BI.Widget
 */
BI.ChartDisplay = BI.inherit(BI.Widget, {

    constants: {
        SCATTER_REGION_COUNT: 3,
        BUBBLE_REGION_COUNT: 4
    },

    _defaultConfig: function () {
        return BI.extend(BI.ChartDisplay.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-display",
            wId: ""
        })
    },

    _init: function () {
        BI.ChartDisplay.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.ChartDisplayModel({
            wId: o.wId
        });
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createTabs, this)
        });
        this.errorPane = BI.createWidget({
            type: "bi.table_chart_error_pane",
            invisible: true
        });
        this.errorPane.element.css("z-index", 1);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.errorPane,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        })
    },

    _doChartItemClick: function (obj) {
        var self = this, o = this.options;
        var linkageInfo = this.model.getLinkageInfo(obj);
        var dId = linkageInfo.dId, clicked = linkageInfo.clicked;
        BI.each(BI.Utils.getWidgetLinkageByID(o.wId), function (i, link) {
            if (BI.contains(dId, link.from)) {
                BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, link.from, clicked);
                self._send2AllChildLinkWidget(link.to, link.from, clicked);
            }
        });
        this.fireEvent(BI.ChartDisplay.EVENT_CHANGE, obj);
    },

    _send2AllChildLinkWidget: function (wid, dId, clicked) {
        var self = this;
        var linkage = BI.Utils.getWidgetLinkageByID(wid);
        BI.each(linkage, function (i, link) {
            BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, dId, clicked);
            self._send2AllChildLinkWidget(link.to, dId, clicked);
        });
    },

    _createTabs: function(v){
        var self = this;
        switch (v) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.COMBINE_CHART:
                var chart = BI.createWidget({type: "bi.axis_chart"});
                chart.on(BI.AxisChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                var chart = BI.createWidget({type: "bi.multi_axis_chart"});
                chart.on(BI.MultiAxisChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                var chart = BI.createWidget({type: "bi.accumulate_axis_chart"});
                chart.on(BI.AccumulateAxisChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.LINE:
                var chart = BI.createWidget({type: "bi.line_chart"});
                chart.on(BI.LineChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.AREA:
                var chart = BI.createWidget({type: "bi.area_chart"});
                chart.on(BI.AreaChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_AREA:
                var chart = BI.createWidget({type: "bi.accumulate_area_chart"});
                chart.on(BI.AccumulateAreaChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                var chart = BI.createWidget({type: "bi.percent_accumulate_axis_chart"});
                chart.on(BI.PercentAccumulateAxisChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_BAR:
                var chart = BI.createWidget({type: "bi.accumulate_bar_chart"});
                chart.on(BI.AccumulateBarChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.DONUT:
                var chart = BI.createWidget({type: "bi.donut_chart"});
                chart.on(BI.DonutChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.RADAR:
                var chart = BI.createWidget({type: "bi.radar_chart"});
                chart.on(BI.RadarChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                var chart = BI.createWidget({type: "bi.accumulate_radar_chart"});
                chart.on(BI.AccumulateRadarChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.PIE:
                var chart = BI.createWidget({type: "bi.pie_chart"});
                chart.on(BI.PieChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.DASHBOARD:
                var chart = BI.createWidget({type: "bi.dashboard_chart"});
                chart.on(BI.DashboardChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.BAR:
                var chart = BI.createWidget({type: "bi.bar_chart"});
                chart.on(BI.BarChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                var chart = BI.createWidget({type: "bi.percent_accumulate_area_chart"});
                chart.on(BI.PercentAccumulateAreaChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.COMPARE_BAR:
                var chart = BI.createWidget({type: "bi.compare_bar_chart"});
                chart.on(BI.CompareBarChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.COMPARE_AXIS:
                var chart = BI.createWidget({type: "bi.compare_axis_chart"});
                chart.on(BI.CompareAxisChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.COMPARE_AREA:
                var chart = BI.createWidget({type: "bi.compare_area_chart"});
                chart.on(BI.CompareAreaChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.RANGE_AREA:
                var chart = BI.createWidget({type: "bi.range_area_chart"});
                chart.on(BI.RangeAreaChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.FALL_AXIS:
                var chart = BI.createWidget({type: "bi.fall_axis_chart"});
                chart.on(BI.FallAxisChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.BUBBLE:
                var chart = BI.createWidget({type: "bi.bubble_chart"});
                chart.on(BI.BubbleChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.FORCE_BUBBLE:
                var chart = BI.createWidget({type: "bi.force_bubble_chart"});
                chart.on(BI.ForceBubbleChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.SCATTER:
                var chart = BI.createWidget({type: "bi.scatter_chart"});
                chart.on(BI.ScatterChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.MAP:
                var chart = BI.createWidget({type: "bi.map_chart"});
                chart.on(BI.MapChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.GIS_MAP:
                var chart = BI.createWidget({type: "bi.gis_map_chart"});
                chart.on(BI.GISMapChart.EVENT_CHANGE, function(obj){
                    self._doChartItemClick(obj);
                });
                return chart;
        }
    },

    populate: function () {
        var self = this, o = this.options;
        var type = BI.Utils.getWidgetTypeByID(o.wId);
        this.errorPane.setVisible(false);
        this.tab.setSelect(type);
        var selectedTab = this.tab.getSelectedTab();
        this.model.getWidgetData(type, function (types, data, options) {
            if (BI.isNotNull(types.error)) {
                self.errorPane.setErrorInfo(types.error);
                self.errorPane.setVisible(true);
                return;
            }
            //try {
            //self.chart.setOptions(BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {
            //    tooltipFormatter: self.model.getToolTip(type),
            //    cordon: self.model.getCordon(),
            //    mapType: {type: BI.Utils.getWidgetTypeByID(o.wId), subType: BI.Utils.getWidgetSubTypeByID(o.wId)}
            //}));
                selectedTab.populate(data, BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), options, {
                    cordon: self.model.getCordon(),
                    tooltip: self.model.getToolTip(type)
                }), types);
            //} catch (e) {
            //    self.errorPane.setErrorInfo("error happens during populate chart: " + e);
            //    self.errorPane.setVisible(true);
            //}
        });
    },

    resize: function () {
        this.tab.getSelectedTab().resize();
    },

    magnify: function () {
        this.tab.getSelectedTab().magnify();
    }
});
BI.ChartDisplay.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.chart_display', BI.ChartDisplay);
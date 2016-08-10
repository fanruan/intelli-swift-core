/**
 * 图表控件
 * @class BI.ChartDisplay
 * @extends BI.Pane
 */
BI.ChartDisplay = BI.inherit(BI.Pane, {

    constants: {
        SCATTER_REGION_COUNT: 3,
        BUBBLE_REGION_COUNT: 4
    },

    _defaultConfig: function () {
        return BI.extend(BI.ChartDisplay.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-display",
            overlap: false,
            wId: ""
        })
    },

    _init: function () {
        BI.ChartDisplay.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.ChartDisplayModel({
            wId: o.wId,
            status: o.status
        });

        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.tab.element.css("z-index", 1);

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

    _onClickDrill: function (dId, value, drillId) {
        var wId = this.options.wId;
        var drillMap = BI.Utils.getDrillByID(wId);
        var drilledIds = [];
        BI.each(drillMap, function (drId, ds) {
            BI.each(ds, function (i, drs) {
                drilledIds.push(drs.dId);
            });
        });
        if(BI.contains(drilledIds, dId)){
            return;
        }
        if(BI.isNull(dId)){
            this.fireEvent(BI.ChartDisplay.EVENT_CHANGE, {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), {})});
            return;
        }
        //value 存当前的过滤条件——因为每一次钻取都要带上所有父节点的值
        //当前钻取的根节点
        var rootId = dId;
        BI.each(drillMap, function (drId, ds) {
            if (dId === drId || (ds.length > 0 && ds[ds.length - 1].dId === dId)) {
                rootId = drId;
            }
        });

        var drillOperators = drillMap[rootId] || [];
        //上钻
        if (BI.isNull(drillId)) {
            if(drillOperators.length !== 0){
                var val = drillOperators[drillOperators.length - 1].values[0].value[0];
                while (val !== value) {
                    if(drillOperators.length === 0){
                        break;
                    }
                    var obj = drillOperators.pop();
                    val = obj.values[0].value[0];
                }
            }
        } else {
            drillOperators.push({
                dId: drillId,
                values: [{
                    dId: dId,
                    value: [value]
                }]
            });
        }
        drillMap[rootId] = drillOperators;
        this.fireEvent(BI.ChartDisplay.EVENT_CHANGE, {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), drillMap)});
    },

    _send2AllChildLinkWidget: function (wid, dId, clicked) {
        var self = this;
        var linkage = BI.Utils.getWidgetLinkageByID(wid);
        BI.each(linkage, function (i, link) {
            BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, dId, clicked);
            self._send2AllChildLinkWidget(link.to, dId, clicked);
        });
    },

    _createTabs: function (v) {
        var self = this;
        switch (v) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.COMBINE_CHART:
                var chart = BI.createWidget({type: "bi.axis_chart"});
                chart.on(BI.AxisChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                var chart = BI.createWidget({type: "bi.multi_axis_chart"});
                chart.on(BI.MultiAxisChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                var chart = BI.createWidget({type: "bi.accumulate_axis_chart"});
                chart.on(BI.AccumulateAxisChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.LINE:
                var chart = BI.createWidget({type: "bi.line_chart"});
                chart.on(BI.LineChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.AREA:
                var chart = BI.createWidget({type: "bi.area_chart"});
                chart.on(BI.AreaChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_AREA:
                var chart = BI.createWidget({type: "bi.accumulate_area_chart"});
                chart.on(BI.AccumulateAreaChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                var chart = BI.createWidget({type: "bi.percent_accumulate_axis_chart"});
                chart.on(BI.PercentAccumulateAxisChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_BAR:
                var chart = BI.createWidget({type: "bi.accumulate_bar_chart"});
                chart.on(BI.AccumulateBarChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.DONUT:
                var chart = BI.createWidget({type: "bi.donut_chart"});
                chart.on(BI.DonutChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.RADAR:
                var chart = BI.createWidget({type: "bi.radar_chart"});
                chart.on(BI.RadarChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                var chart = BI.createWidget({type: "bi.accumulate_radar_chart"});
                chart.on(BI.AccumulateRadarChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.PIE:
                var chart = BI.createWidget({type: "bi.pie_chart"});
                chart.on(BI.PieChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.DASHBOARD:
                var chart = BI.createWidget({type: "bi.dashboard_chart"});
                chart.on(BI.DashboardChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.BAR:
                var chart = BI.createWidget({type: "bi.bar_chart"});
                chart.on(BI.BarChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                var chart = BI.createWidget({type: "bi.percent_accumulate_area_chart"});
                chart.on(BI.PercentAccumulateAreaChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.COMPARE_BAR:
                var chart = BI.createWidget({type: "bi.compare_bar_chart"});
                chart.on(BI.CompareBarChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.COMPARE_AXIS:
                var chart = BI.createWidget({type: "bi.compare_axis_chart"});
                chart.on(BI.CompareAxisChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.COMPARE_AREA:
                var chart = BI.createWidget({type: "bi.compare_area_chart"});
                chart.on(BI.CompareAreaChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.RANGE_AREA:
                var chart = BI.createWidget({type: "bi.range_area_chart"});
                chart.on(BI.RangeAreaChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.FALL_AXIS:
                var chart = BI.createWidget({type: "bi.fall_axis_chart"});
                chart.on(BI.FallAxisChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.BUBBLE:
                var chart = BI.createWidget({type: "bi.bubble_chart"});
                chart.on(BI.BubbleChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.FORCE_BUBBLE:
                var chart = BI.createWidget({type: "bi.force_bubble_chart"});
                chart.on(BI.ForceBubbleChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.SCATTER:
                var chart = BI.createWidget({type: "bi.scatter_chart"});
                chart.on(BI.ScatterChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                });
                return chart;
            case BICst.WIDGET.MAP:
                var chart = BI.createWidget({type: "bi.map_chart"});
                chart.on(BI.MapChart.EVENT_CHANGE, function (obj) {
                    self._doChartItemClick(obj);
                    self._onClickDrill(obj.dId, obj.x, obj.drillDid);
                });
                chart.on(BI.MapChart.EVENT_CLICK_DTOOL, function(obj){
                    self._onClickDrill(obj.dId, obj.x);
                });
                return chart;
            case BICst.WIDGET.GIS_MAP:
                var chart = BI.createWidget({type: "bi.gis_map_chart"});
                chart.on(BI.GISMapChart.EVENT_CHANGE, function (obj) {
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
        this.loading();
        this.model.getWidgetData(type, function (types, data, options) {
            self.loaded();
            if (BI.isNotNull(types.error)) {
                self.errorPane.setErrorInfo(types.error);
                self.errorPane.setVisible(true);
                return;
            }
            try {
                var dimensionIds = BI.Utils.getAllDimDimensionIDs(o.wId);
                var lnglat = BI.Utils.getDimensionPositionByID(dimensionIds[0]);

                var op = BI.extend(options, {
                    chart_color: BI.Utils.getWSChartColorByID(o.wId),
                    chart_style: BI.Utils.getWSChartStyleByID(o.wId),
                    chart_line_type: BI.Utils.getWSChartLineTypeByID(o.wId),
                    chart_pie_type: BI.Utils.getWSChartPieTypeByID(o.wId),
                    chart_radar_type: BI.Utils.getWSChartRadarTypeByID(o.wId),
                    chart_dashboard_type: BI.Utils.getWSChartDashboardTypeByID(o.wId),
                    chart_inner_radius: BI.Utils.getWSChartInnerRadiusByID(o.wId),
                    chart_total_angle: BI.Utils.getWSChartTotalAngleByID(o.wId),
                    left_y_axis_style: BI.Utils.getWSLeftYAxisStyleByID(o.wId),
                    x_axis_style: BI.Utils.getWSXAxisStyleByID(o.wId),
                    right_y_axis_style: BI.Utils.getWSRightYAxisStyleByID(o.wId),
                    right_y_axis_second_style: BI.Utils.getWSRightYAxis2StyleByID(o.wId),
                    left_y_axis_number_level: BI.Utils.getWSLeftYAxisNumLevelByID(o.wId),
                    number_of_pointer: BI.Utils.getWSNumberOfPointerByID(o.wId),
                    dashboard_number_level: BI.Utils.getWSDashboardNumLevelByID(o.wId),
                    x_axis_number_level: BI.Utils.getWSXAxisNumLevelByID(o.wId),
                    right_y_axis_number_level: BI.Utils.getWSRightYAxisNumLevelByID(o.wId),
                    right_y_axis_second_number_level: BI.Utils.getWSRightYAxis2NumLevelByID(o.wId),
                    left_y_axis_unit: BI.Utils.getWSLeftYAxisUnitByID(o.wId),
                    dashboard_unit: BI.Utils.getWSDashboardUnitByID(o.wId),
                    x_axis_unit: BI.Utils.getWSXAxisUnitByID(o.wId),
                    right_y_axis_unit: BI.Utils.getWSRightYAxisUnitByID(o.wId),
                    right_y_axis_second_unit: BI.Utils.getWSRightYAxis2UnitByID(o.wId),
                    show_left_y_axis_title: BI.Utils.getWSShowLeftYAxisTitleByID(o.wId),
                    show_right_y_axis_title: BI.Utils.getWSShowRightYAxisTitleByID(o.wId),
                    show_right_y_axis_second_title: BI.Utils.getWSShowRightYAxis2TitleByID(o.wId),
                    left_y_axis_title: BI.Utils.getWSLeftYAxisTitleByID(o.wId),
                    right_y_axis_title: BI.Utils.getWSRightYAxisTitleByID(o.wId),
                    right_y_axis_second_title: BI.Utils.getWSRightYAxis2TitleByID(o.wId),
                    left_y_axis_reversed: BI.Utils.getWSLeftYAxisReversedByID(o.wId),
                    right_y_axis_reversed: BI.Utils.getWSRightYAxisReversedByID(o.wId),
                    right_y_axis_second_reversed: BI.Utils.getWSRightYAxis2ReversedByID(o.wId),
                    show_x_axis_title: BI.Utils.getWSShowXAxisTitleByID(o.wId),
                    x_axis_title: BI.Utils.getWSXAxisTitleByID(o.wId),
                    text_direction: BI.Utils.getWSTextDirectionByID(o.wId),
                    chart_legend: BI.Utils.getWSChartLegendByID(o.wId),
                    show_data_label: BI.Utils.getWSShowDataLabelByID(o.wId),
                    show_data_table: BI.Utils.getWSShowDataTableByID(o.wId),
                    show_grid_line: BI.Utils.getWSShowGridLineByID(o.wId),
                    show_zoom: BI.Utils.getWSShowZoomByID(o.wId),
                    style_conditions: BI.Utils.getWSDashboardStylesByID(o.wId),
                    auto_custom: BI.Utils.getWSScaleByID(o.wId),
                    theme_color: BI.Utils.getWSThemeColorByID(o.wId),
                    map_styles: BI.Utils.getWSMapStylesByID(o.wId),
                    transfer_filter: BI.Utils.getWSTransferFilterByID(o.wId),
                    rules_display: BI.Utils.getWSShowRulesByID(o.wId),
                    bubble_style: BI.Utils.getWSBubbleStyleByID(o.wId),
                    max_scale: BI.Utils.getWSMaxScaleByID(o.wId) ,
                    min_scale: BI.Utils.getWSMinScaleByID(o.wId),
                    show_percentage: BI.Utils.getWSShowPercentageByID(o.wId)
                }, {
                    cordon: self.model.getCordon(),
                    tooltip: self.model.getToolTip(type),
                    lnglat: BI.isNotNull(lnglat) ? lnglat.type : lnglat
                });
                selectedTab.populate(data, op, types);
             } catch (e) {
                 self.errorPane.setErrorInfo("error happens during populate chart: " + e);
                 self.errorPane.setVisible(true);
             }
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
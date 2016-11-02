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
        if (BI.Utils.getDimensionTypeByID(dId) === BICst.TARGET_TYPE.FORMULA) {
            self._createMultiLinkage({
                dId: dId,
                clicked: clicked
            }, {});
        } else {
            BI.each(BI.Utils.getWidgetLinkageByID(o.wId), function (i, link) {
                if (BI.contains(dId, link.from) && BI.isEmptyArray(link.cids)) {
                    BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, link.from, clicked);
                    self._send2AllChildLinkWidget(link.to, link.from, clicked);
                }
            });
        }
        this.fireEvent(BI.ChartDisplay.EVENT_CHANGE, obj);
    },

    //点击计算指标时出现的选择框  暂时先放在这里
    _createMultiLinkage: function (obj, place) {
        var self =this, o = this.options;
        if (this.linkageList) {
            this.linkageList.empty();
        }
        var dId = obj.dId[0], clicked = obj.clicked;
        var expression = BI.Utils.getDimensionSrcByID(dId).expression;
        if(!expression) {
            return;
        }
        var linkages = [];
        BI.each(BI.Utils.getWidgetLinkageByID(o.wId), function (i, link) {
            if(dId === link.cids[0]) {
                linkages.push(link);
            }
        });

        if (linkages.length < 1) {
            return;
        }

        if(linkages.length === 1) {
            BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + linkages[0].to, linkages[0].from, clicked);
            self._send2AllChildLinkWidget(linkages[0].to, linkages[0].from, clicked);
        } else {
            var items = [];
            BI.each(linkages, function (idx, link) {
                var name = "";
                BI.each(link.cids, function (idx, cId) {
                    name += BI.Utils.getDimensionNameByID(cId) + "-";
                });
                name += BI.Utils.getDimensionNameByID(link.from) + "-" + BI.i18nText("BI-Link");
                items.push({
                    text: name,
                    title: name,
                    to: link.to,
                    from: link.from
                })
            });
            this._doDestroy = true;
            this.linkageList = BI.createWidget({
                type: "bi.button_group",
                items: BI.createItems(items, {
                    type: "bi.text_button",
                    width: 100,
                    height: 30,
                    handler: function () {
                        var link = this.options;
                        BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, link.from, clicked);
                        self._send2AllChildLinkWidget(link.to, link.from, clicked);
                        this.fireEvent(BI.TextButton.EVENT_CHANGE, arguments);
                    }
                }),
                width: 120,
                height: 30* items.length +10,
                layouts: [{
                    type: "bi.vertical"
                }]
            });
            this.linkageList.on(BI.TextButton.EVENT_CHANGE, function () {
                self.linkageList.empty();
            });
            var destroyList = BI.debounce(BI.bind(function () {
                if(self._doDestroy) {
                    this.empty();
                }
            }, this.linkageList), 3000);

            this.linkageList.element.hover(function () {
                self._doDestroy = false;
            }, function () {
                self._doDestroy = true;
                destroyList();
            });
            destroyList();
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this.linkageList,
                    left:0,
                    right:0,
                    top:0,
                    bottom:0
                }]
            });
        }
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
        if (BI.contains(drilledIds, dId)) {
            return;
        }
        if (BI.isNull(dId)) {
            this.fireEvent(BI.ChartDisplay.EVENT_SET, {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), {})});
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
            if (drillOperators.length !== 0) {
                var val = drillOperators[drillOperators.length - 1].values[0].dId;
                while (val !== dId) {
                    if (drillOperators.length === 0) {
                        break;
                    }
                    var obj = drillOperators.pop();
                    val = obj.values[0].dId;
                }
                if(val === dId && drillOperators.length !== 0){
                    drillOperators.pop();
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
        this.fireEvent(BI.ChartDisplay.EVENT_SET, {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), drillMap)});
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
                chart.on(BI.MapChart.EVENT_CLICK_DTOOL, function (obj) {
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
                var op;
                if (BI.Utils.getWSMinimalistByID(o.wId) && BI.ChartDisplay.MINIMALIST_WIDGET.contains(type)) {
                    op = BI.extend(options, {
                        chart_color: BI.Utils.getWSChartColorByID(o.wId),
                        chart_style: BI.Utils.getWSChartStyleByID(o.wId),
                        chart_line_type: BI.Utils.getWSChartLineTypeByID(o.wId),
                        transfer_filter: BI.Utils.getWSTransferFilterByID(o.wId),
                        left_y_axis_reversed: BI.Utils.getWSLeftYAxisReversedByID(o.wId),
                        right_y_axis_reversed: BI.Utils.getWSRightYAxisReversedByID(o.wId),
                        chart_font: BI.extend(BI.Utils.getGSChartFont(o.wId), {
                            fontSize: BI.Utils.getGSChartFont(o.wId).fontSize + "px"
                        }),
                        null_continue: BI.Utils.getWSNullContinueByID(o.wId),
                        show_left_label: false,
                        show_right_label: false,
                        show_right2_label: false,
                        cat_label_style: BI.Utils.getWSCatLabelStyleByID(o.wId),
                        line_width: BICst.DEFAULT_CHART_SETTING.mini_line_width,
                        enable_tick: BICst.DEFAULT_CHART_SETTING.mini_enable_tick,
                        enable_minor_tick: BICst.DEFAULT_CHART_SETTING.mini_enable_minor_tick,
                        left_y_axis_unit: BICst.DEFAULT_CHART_SETTING.left_y_axis_unit,
                        show_x_axis_title: BICst.DEFAULT_CHART_SETTING.show_x_axis_title,
                        show_left_y_axis_title: BICst.DEFAULT_CHART_SETTING.show_left_y_axis_title,
                        chart_legend: BICst.DEFAULT_CHART_SETTING.mini_chart_legend,
                        show_data_label: BICst.DEFAULT_CHART_SETTING.mini_show_data_label,
                        show_grid_line: BICst.DEFAULT_CHART_SETTING.mini_show_grid_line
                    }, {
                        tooltip: self.model.getToolTip(type),
                        lnglat: BI.isNotNull(lnglat) ? lnglat.type : lnglat
                    })
                } else {
                    op = BI.extend(options, {
                        chart_color: BI.Utils.getWSChartColorByID(o.wId),
                        chart_style: BI.Utils.getWSChartStyleByID(o.wId),
                        chart_line_type: BI.Utils.getWSChartLineTypeByID(o.wId),
                        chart_pie_type: BI.Utils.getWSChartPieTypeByID(o.wId),
                        chart_radar_type: BI.Utils.getWSChartRadarTypeByID(o.wId),
                        chart_dashboard_type: BI.Utils.getWSChartDashboardTypeByID(o.wId),
                        chart_inner_radius: BI.Utils.getWSChartInnerRadiusByID(o.wId),
                        chart_total_angle: BI.Utils.getWSChartTotalAngleByID(o.wId),
                        left_y_axis_style: BI.Utils.getWSLeftYAxisStyleByID(o.wId),
                        right_y_axis_style: BI.Utils.getWSRightYAxisStyleByID(o.wId),
                        right_y_axis_second_style: BI.Utils.getWSRightYAxis2StyleByID(o.wId),
                        left_y_axis_number_level: BI.Utils.getWSLeftYAxisNumLevelByID(o.wId),
                        number_of_pointer: BI.Utils.getWSNumberOfPointerByID(o.wId),
                        dashboard_number_level: BI.Utils.getWSDashboardNumLevelByID(o.wId),
                        right_y_axis_number_level: BI.Utils.getWSRightYAxisNumLevelByID(o.wId),
                        right_y_axis_second_number_level: BI.Utils.getWSRightYAxis2NumLevelByID(o.wId),
                        left_y_axis_unit: BI.Utils.getWSLeftYAxisUnitByID(o.wId),
                        dashboard_unit: BI.Utils.getWSDashboardUnitByID(o.wId),
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
                        max_scale: BI.Utils.getWSMaxScaleByID(o.wId),
                        min_scale: BI.Utils.getWSMinScaleByID(o.wId),
                        show_percentage: BI.Utils.getWSShowPercentageByID(o.wId),
                        big_data_mode: BI.Utils.getWSBigDataModelByID(o.wId),
                        bubble_min_size: BI.Utils.getWSMinBubbleSizeByID(o.wId),
                        bubble_max_size: BI.Utils.getWSMaxBubbleSizeByID(o.wId),
                        gradient_colors: BI.Utils.getWSBubbleGradientsByID(o.wId),
                        fixed_colors: BI.Utils.getWSBubbleFixedColorsByID(o.wId),
                        custom_y_scale: BI.Utils.getWSCustomYScale(o.wId),
                        custom_x_scale: BI.Utils.getWSCustomXScale(o.wId),
                        show_background_layer: BI.Utils.getWSShowBackgroundByID(o.wId),
                        num_separators: BI.Utils.getWSNumberSeparatorsByID(o.wId),
                        right_num_separators: BI.Utils.getWSRightNumberSeparatorsByID(o.wId),
                        right2_num_separators: BI.Utils.getWSRight2NumberSeparatorsByID(o.wId),
                        show_left_label: BI.Utils.getWSShowLValueAxisLabelByID(o.wId),
                        left_label_style: BI.Utils.getWSLValueAxisLabelSettingByID(o.wId),
                        left_line_color: BI.Utils.getWSLValueAxisLineColorByID(o.wId),
                        show_right_label: BI.Utils.getWSShowRValueAxisLabelByID(o.wId),
                        right_label_style: BI.Utils.getWSRValueAxisLabelSettingByID(o.wId),
                        right_line_color: BI.Utils.getWSRValueAxisLineColorByID(o.wId),
                        show_right2_label: BI.Utils.getWSShowR2ValueAxisLabelByID(o.wId),
                        right2_label_style: BI.Utils.getWSR2ValueAxisLabelSettingByID(o.wId),
                        right2_line_color: BI.Utils.getWSR2ValueAxisLineColorByID(o.wId),
                        chart_legend_setting: BI.Utils.getWSLegendSettingByID(o.wId),
                        show_h_grid_line: BI.Utils.getWSShowHGridLineByID(o.wId),
                        h_grid_line_color: BI.Utils.getWSHGridLineColorByID(o.wId),
                        show_v_grid_line: BI.Utils.getWSShowVGridLineByID(o.wId),
                        v_grid_line_color: BI.Utils.getWSVGridLineColorByID(o.wId),
                        tooltip_setting: BI.Utils.getWSToolTipSettingByID(o.wId),
                        chart_font: BI.extend(BI.Utils.getGSChartFont(o.wId), {
                            fontSize: BI.Utils.getGSChartFont(o.wId).fontSize + "px"
                        }),
                        null_continue: BI.Utils.getWSNullContinueByID(o.wId),
                        show_cat_label: BI.Utils.getWSShowCatLabelByID(o.wId),
                        cat_label_style: BI.Utils.getWSCatLabelStyleByID(o.wId),
                        cat_line_color: BI.Utils.getWSCatLineColorByID(o.wId),
                        background_layer_info: MapConst.WMS_INFO[BI.Utils.getWSBackgroundLayerInfoByID(o.wId)]
                    }, {
                        cordon: self.model.getCordon(),
                        tooltip: self.model.getToolTip(type),
                        lnglat: BI.isNotNull(lnglat) ? lnglat.type : lnglat
                    });
                }
                selectedTab.populate(data, op, types);
            } catch (e) {
                self.errorPane.setErrorInfo("error happens during populate chart: " + e);
                console.error(e);
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
BI.extend(BI.ChartDisplay, {
    MINIMALIST_WIDGET: [
        BICst.WIDGET.AXIS,
        BICst.WIDGET.ACCUMULATE_AXIS,
        BICst.WIDGET.PERCENT_ACCUMULATE_AXIS,
        BICst.WIDGET.COMPARE_AXIS,
        BICst.WIDGET.FALL_AXIS,
        BICst.WIDGET.BAR,
        BICst.WIDGET.ACCUMULATE_BAR,
        BICst.WIDGET.COMPARE_BAR,
        BICst.WIDGET.LINE,
        BICst.WIDGET.AREA,
        BICst.WIDGET.ACCUMULATE_AREA,
        BICst.WIDGET.PERCENT_ACCUMULATE_AREA,
        BICst.WIDGET.COMPARE_AREA,
        BICst.WIDGET.RANGE_AREA,
        BICst.WIDGET.COMBINE_CHART,
        BICst.WIDGET.MULTI_AXIS_COMBINE_CHART
    ]
});
BI.ChartDisplay.EVENT_CHANGE = "EVENT_CHANGE";
BI.ChartDisplay.EVENT_SET = "EVENT_SET";
$.shortcut('bi.chart_display', BI.ChartDisplay);

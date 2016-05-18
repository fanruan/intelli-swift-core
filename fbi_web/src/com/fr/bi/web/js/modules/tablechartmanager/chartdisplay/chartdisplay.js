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
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createTabs, this)
        });
    },

    _createTabs: function(v){
        var self = this;
        switch (v) {
            case BICst.WIDGET.BAR:
                return BI.createWidget({
                    type: "bi.bar_chart"
                });
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                return BI.createWidget({
                    type: "bi.percent_accumulate_area_chart"
                });
            case BICst.WIDGET.BUBBLE:
                return BI.createWidget({
                    type: "bi.bubble_chart"
                });
            case BICst.WIDGET.SCATTER:
                return BI.createWidget({
                    type: "bi.scatter_chart"
                });
            case BICst.WIDGET.AXIS:
                return BI.createWidget({
                    type: "bi.axis_chart"
                });
            case BICst.WIDGET.ACCUMULATE_AXIS:
                return BI.createWidget({
                    type: "bi.accumulate_axis_chart"
                });
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.LINE:
                return BI.createWidget({
                    type: "bi.line_chart"
                });
            case BICst.WIDGET.AREA:
                return BI.createWidget({
                    type: "bi.area_chart"
                });
            case BICst.WIDGET.ACCUMULATE_AREA:
                return BI.createWidget({
                    type: "bi.accumulate_area_chart"
                });
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                return BI.createWidget({
                    type: "bi.percent_accumulate_axis_chart"
                });
            case BICst.WIDGET.ACCUMULATE_BAR:
                return BI.createWidget({
                    type: "bi.accumulate_bar_chart"
                });
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.DONUT:
                return BI.createWidget({
                    type: "bi.donut_chart"
                });
            case BICst.WIDGET.RADAR:
                return BI.createWidget({
                    type: "bi.radar_chart"
                });
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return BI.createWidget({
                    type: "bi.accumulate_radar_chart"
                });
            case BICst.WIDGET.PIE:
                return BI.createWidget({
                    type: "bi.pie_chart"
                });
            case BICst.WIDGET.DASHBOARD:
                return BI.createWidget({
                    type: "bi.dashboard_chart"
                });
            case BICst.WIDGET.FORCE_BUBBLE:
                return BI.createWidget({
                    type: "bi.force_bubble_chart"
                });
            case BICst.WIDGET.FUNNEL:
                return BI.createWidget({
                    type: "bi.funnel_chart"
                });
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return BI.createWidget();
        }
    },

    _getShowTarget: function () {
        var self = this, o = this.options;
        var view = BI.Utils.getWidgetViewByID(o.wId);
        this.seriesDid = BI.find(view[BICst.REGION.DIMENSION1], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.cataDid = BI.find(view[BICst.REGION.DIMENSION2], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.targetIds = [];
        BI.each(view, function (regionType, arr) {
            if (regionType >= BICst.REGION.TARGET1) {
                self.targetIds = BI.concat(self.targetIds, arr);
            }
        });
        return this.targetIds = BI.filter(this.targetIds, function (idx, tId) {
            return BI.Utils.isDimensionUsable(tId);
        });

    },

    _formatDataForBubble: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var result = BI.find(view, function (region, arr) {
            return BI.isEmptyArray(arr);
        });
        if (BI.isNotNull(result) || BI.size(view) < this.constants.BUBBLE_REGION_COUNT) {
            return [];
        }
        return BI.map(data.c, function (idx, item) {
            return {
                data: [{
                    x: item.s[1],
                    y: item.s[0],
                    size: item.s[2]
                }],
                name: item.n
            };
        });
    },

    _formatDataForScatter: function (data) {
        var self = this, o = this.options;
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var result = BI.find(view, function (region, arr) {
            return BI.isEmptyArray(arr);
        });
        if (BI.isNotNull(result) || BI.size(view) < this.constants.SCATTER_REGION_COUNT) {
            return [];
        }
        return BI.map(data.c, function (idx, item) {
            return {
                data: [{
                    x: item.s[1],
                    y: item.s[0]
                }],
                name: item.n
            };
        });
    },

    _formatDataForCommon: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        if (BI.has(data, "t")) {
            var top = data.t, left = data.l;
            return BI.map(top.c, function (id, tObj) {
                var data = BI.map(left.c, function (idx, obj) {
                    var wType = BI.Utils.getWidgetTypeByID(o.wId);
                    if (wType === BICst.WIDGET.ACCUMULATE_BAR || wType === BICst.WIDGET.BAR) {
                        return {
                            "y": obj.n,
                            "x": obj.s.c[id].s
                        };
                    }
                    return {
                        "x": obj.n,
                        "y": obj.s.c[id].s
                    };
                });
                return {
                    name: tObj.n,
                    data: data
                }
            });
        }
        if (BI.has(data, "c")) {
            var obj = (data.c)[0];
            var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
            return BI.map(columnSizeArray, function (idx, value) {
                var wType = BI.Utils.getWidgetTypeByID(o.wId);
                var adjustData = BI.map(data.c, function (id, item) {
                    if (wType === BICst.WIDGET.ACCUMULATE_BAR || wType === BICst.WIDGET.BAR) {
                        return {
                            y: item.n,
                            x: item.s[idx]
                        };
                    }
                    return {
                        x: item.n,
                        y: item.s[idx]
                    };
                });
                return {
                    data: adjustData,
                    name: BI.Utils.getDimensionNameByID(targetIds[idx])
                };
            });
        }
        return [];
    },

    _createDataByData: function (data) {
        var self = this, o = this.options;
        switch (BI.Utils.getWidgetTypeByID(o.wId)) {
            case BICst.WIDGET.BUBBLE:
                return self._formatDataForBubble(data);
            case BICst.WIDGET.SCATTER:
                return self._formatDataForScatter(data);
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return self._formatDataForCommon(data);

        }
    },

    populate: function () {
        var self = this, o = this.options;
        var type = BI.Utils.getWidgetTypeByID(o.wId);
        this.tab.setSelect(type);
        var selectedTab = this.tab.getSelectedTab();
        selectedTab.loading();
        BI.Utils.getWidgetDataByID(o.wId, function (jsonData) {
            selectedTab.populate(self._createDataByData(jsonData.data));
            selectedTab.loaded();
        });
    },

    resize: function () {
        this.tab.getSelectedTab().resize();
    }
});
$.shortcut('bi.chart_display', BI.ChartDisplay);
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
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
                var chart = BI.createWidget({
                    type: "bi.combine_chart"
                });
                chart.on(BI.CombineChart.EVENT_CHANGE, function(obj){

                });
                return chart;
            case BICst.WIDGET.COMPARE_BAR:
                var chart = BI.createWidget({
                    type: "bi.compare_bar_chart"
                });
                chart.on(BI.CombineChart.EVENT_CHANGE, function(obj){

                });
                return chart;
            case BICst.WIDGET.COMPARE_AXIS:
                var chart = BI.createWidget({
                    type: "bi.combine_axis_chart"
                });
                chart.on(BI.CombineChart.EVENT_CHANGE, function(obj){

                });
                return chart;
            case BICst.WIDGET.COMPARE_AREA:
                var chart = BI.createWidget({
                    type: "bi.combine_area_chart"
                });
                chart.on(BI.CombineChart.EVENT_CHANGE, function(obj){

                });
                return chart;
            case BICst.WIDGET.RANGE_AREA:
                var chart = BI.createWidget({
                    type: "bi.range_area_chart"
                });
                chart.on(BI.CombineChart.EVENT_CHANGE, function(obj){

                });
                return chart;
            case BICst.WIDGET.FALL_AXIS:
                var chart = BI.createWidget({
                    type: "bi.fall_axis_chart"
                });
                chart.on(BI.CombineChart.EVENT_CHANGE, function(obj){

                });
                return chart;
            case BICst.WIDGET.FUNNEL:
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

    _formatDataForAxis: function (da) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var data = this._formatDataForCommon(da);
        if (BI.isEmptyArray(data)) {
            return [];
        }
        var view = BI.Utils.getWidgetViewByID(o.wId);
        BI.each(this.targetIds, function (idx, tId) {
            if (BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], tId)) {
                data[idx].yAxis = 1;
                data[idx].stack = "stackColumnOne";
            } else {
                data[idx].yAxis = 0;
                data[idx].stack = "stackColumnTwo";
            }
        });
        return data;
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
            var obj = {};
            obj[item.n] = [{
                x: item.s[1],
                y: item.s[0],
                z: item.s[2]
            }];
            return obj;
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
            var obj = {};
            obj[item.n] = [{
                x: item.s[1],
                y: item.s[0]
            }];
            return obj;
        });
    },

    _formatDataForCommon: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        if (BI.has(data, "t")) {
            var top = data.t, left = data.l;
            return BI.map(top.c, function (id, tObj) {
                var data = BI.map(left.c, function (idx, obj) {
                    return {
                        "x": obj.n,
                        "y": obj.s.c[id].s
                    };
                });
                var obj = {};
                obj[tObj.n] = data;
                return obj;
            });
        }
        if (BI.has(data, "c")) {
            var obj = (data.c)[0];
            var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
            return BI.map(columnSizeArray, function (idx, value) {
                var adjustData = BI.map(data.c, function (id, item) {
                    return {
                        x: item.n,
                        y: item.s[idx]
                    };
                });
                var obj = {};
                obj[BI.Utils.getDimensionNameByID(targetIds[idx])] = adjustData;
                return obj;
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
/**
 * 图表控件
 * @class BI.ChartDisplay
 * @extends BI.Widget
 */
BI.ChartDisplay = BI.inherit(BI.Widget, {

    constants: {
        BUBBLE_REGION_COUNT: 4,
        SCATTER_REGION_COUNT: 3
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
        this.tarIdMap = {};
        this.chartDisplay = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.chartDisplay.on(BI.Chart.EVENT_CHANGE, function (obj) {
            var dId = [], clicked = [];
            BI.Msg.toast("category: " + obj.category + " seriesName: " + obj.seriesName + " value: " + obj.value + " size: " + obj.size);
            switch (BI.Utils.getWidgetTypeByID(o.wId)) {
                case BICst.WIDGET.BUBBLE:
                case BICst.WIDGET.FORCE_BUBBLE:
                    dId = [self.tarIdMap[obj.value], self.tarIdMap[obj.category], self.tarIdMap[obj.size]];
                    clicked = [{
                        dId: self.seriesDid,
                        value: [obj.seriesName]
                    }];
                    break;
                case BICst.WIDGET.SCATTER:
                    dId = [self.tarIdMap[obj.value], self.tarIdMap[obj.category]];
                    clicked = [{
                        dId: self.seriesDid,
                        value: [obj.seriesName]
                    }];
                    break;
                default:
                    dId = [self.tarIdMap[obj.value]];
                    clicked = [{
                        dId: self.seriesDid,
                        value: [obj.category]
                    }];
                    if (BI.isNotNull(self.cataDid)) {
                        clicked.push({
                            dId: self.cataDid,
                            value: [obj.seriesName]
                        })
                    }
                    break;

            }
            BI.each(BI.Utils.getWidgetLinkageByID(o.wId), function (i, link) {
                if (BI.contains(dId, link.from)) {
                    BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, link.from, clicked);
                    self._send2AllChildLinkWidget(link.to, link.from, clicked);
                }
            });
        })
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

    //todo 为组合图服务，以前这套不对了
    _getChartTypeByTargetId: function (tId) {
        var forAxis = function () {
            var chartType = BI.Utils.getDimensionStyleOfChartByID(tId);
            if (BI.isNotNull(chartType)) {
                type = chartType.type;
            }
            switch (type) {
                case BICst.WIDGET.COLUMN:
                case BICst.WIDGET.ACCUMULATE_COLUMN:
                    return "column";
                case BICst.WIDGET.AREA:
                    return "area";
                case BICst.WIDGET.LINE:
                    return "line";
                default:
                    return "column";
            }
        };

        var type = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(tId));
        switch (type) {
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
                return "bar";
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DONUT:
                return "pie";
            case BICst.WIDGET.RADAR:
                return "radar";
            case BICst.WIDGET.MAP:
                return "map";
            case BICst.WIDGET.DASHBOARD:
                return "gauge";
            case BICst.WIDGET.BUBBLE:
                return "bubble";
            case BICst.WIDGET.SCATTER:
                return "scatter";
            case BICst.WIDGET.AXIS:
                return forAxis();
        }
    },

    _formatDataForCommon: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        if (BI.has(data, "t")) {
            var top = data.t, left = data.l;
            return BI.map(top.c, function (id, tObj) {
                var data = BI.map(left.c, function (idx, obj) {
                    self.tarIdMap[obj.s.c[id].s] = targetIds[0];
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
                    //type: self._getChartTypeByTargetId(targetIds[0]),
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
                    self.tarIdMap[item.s[idx]] = targetIds[idx];
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
                var res = {
                    data: adjustData,
                    //type: self._getChartTypeByTargetId(targetIds[idx]),
                    name: BI.Utils.getDimensionNameByID(targetIds[idx])
                };
                var chart = BI.Utils.getDimensionStyleOfChartByID(targetIds[idx]);
                if (BI.has(chart, "type") && chart.type === BICst.WIDGET.ACCUMULATE_COLUMN) {
                    res.stack = "stackChart";
                }
                return res;
            });
        }
        return [];
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
            self.tarIdMap[item.s[0]] = targetIds[0];
            self.tarIdMap[item.s[1]] = targetIds[1];
            self.tarIdMap[item.s[2]] = targetIds[2];
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
        var targetIds = this._getShowTarget();
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var result = BI.find(view, function (region, arr) {
            return BI.isEmptyArray(arr);
        });
        if (BI.isNotNull(result) || BI.size(view) < this.constants.SCATTER_REGION_COUNT) {
            return [];
        }
        return BI.map(data.c, function (idx, item) {
            self.tarIdMap[item.s[0]] = targetIds[0];
            self.tarIdMap[item.s[1]] = targetIds[1];
            return {
                data: [{
                    x: item.s[1],
                    y: item.s[0]
                }],
                name: item.n
            };
        });
    },

    _formatDataForAxis: function (da) {
        var self = this, o = this.options;
        var data = this._formatDataForCommon(da);
        if (BI.isEmptyArray(data)) {
            return [];
        }
        var view = BI.Utils.getWidgetViewByID(o.wId);
        BI.each(this.targetIds, function (idx, tId) {
            if (BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], tId)) {
                data[idx].yAxis = 1;
            } else {
                data[idx].yAxis = 0;
            }
        });
        return data;
    },

    _createDataByData: function (data) {
        var self = this, o = this.options;
        switch (BI.Utils.getWidgetTypeByID(o.wId)) {
            case BICst.WIDGET.BUBBLE:
                return self._formatDataForBubble(data);
            case BICst.WIDGET.SCATTER:
                return self._formatDataForScatter(data);
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
                return self._formatDataForAxis(data);
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
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
        this.tarIdMap = {};
        this.chartDisplay.loading();
        BI.Utils.getWidgetDataByID(o.wId, function (jsonData) {
            self.chartDisplay.setChartType(BI.Utils.getWidgetTypeByID(o.wId));
            var view = BI.Utils.getWidgetViewByID(o.wId);
            if (BI.has(view, BICst.REGION.TARGET2)
                && BI.isNotEmptyArray(view[BICst.REGION.TARGET2])
                && BI.Utils.getWidgetTypeByID(o.wId) === BICst.WIDGET.AXIS) {
                self.chartDisplay.showTheOtherYAxis();
            } else {
                self.chartDisplay.hideTheOtherYAxis();
            }
            self.chartDisplay.resize();
            self.chartDisplay.populate(self._createDataByData(jsonData.data));
            self.chartDisplay.loaded();
        });
    },

    resize: function () {
        this.chartDisplay.resize();
    },

    _send2AllChildLinkWidget: function (wid, dId, clicked) {
        var self = this;
        var linkage = BI.Utils.getWidgetLinkageByID(wid);
        BI.each(linkage, function (i, link) {
            BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, dId, clicked);
            self._send2AllChildLinkWidget(link.to);
        });
    }
});
$.shortcut('bi.chart_display', BI.ChartDisplay);
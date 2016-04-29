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
        this.chartDisplay.on(BI.Chart.EVENT_CHANGE, function(obj){
            var dId = [], clicked = [];
            BI.Msg.toast("category: " + obj.category + " seriesName: " + obj.seriesName + " value: " + obj.value + " size: " + obj.size);
            switch (BI.Utils.getWidgetTypeByID(o.wId)){
                case BICst.Widget.BUBBLE:
                    dId = [self.tarIdMap[obj.value], self.tarIdMap[obj.category], self.tarIdMap[obj.size]];
                    clicked = [{
                        dId: self.seriesDid,
                        value: [obj.seriesName]
                    }];
                    break;
                case BICst.Widget.SCATTER:
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
                    break;

            }
            BI.each(BI.Utils.getWidgetLinkageByID(o.wId), function(i, link){
                if(BI.contains(dId, link.from)) {
                    BI.Broadcasts.send(link.to, link.from, clicked);
                    self._send2AllChildLinkWidget(link.to, link.from, clicked);
                }
            });
        })
    },

    _getShowTarget: function(){
        var self = this, o = this.options;
        var view = BI.Utils.getWidgetViewByID(o.wId);
        this.seriesDid = BI.find(view[BICst.REGION.DIMENSION1], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.cataDid = BI.find(view[BICst.REGION.DIMENSION2], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.targetIds = [];
        BI.each(view, function(regionType, arr){
            if(regionType >= BICst.REGION.TARGET1){
                self.targetIds = BI.concat(self.targetIds, arr);
            }
        });
        return this.targetIds = BI.filter(this.targetIds, function(idx, tId){
            return BI.Utils.isDimensionUsable(tId);
        });

    },

    _getChartTypeByTargetId: function (tId) {
        var forAxis = function () {
            var chartType = BI.Utils.getDimensionStyleOfChartByID(tId);
            if(BI.isNotNull(chartType)){
                type = chartType.type;
            }
            switch (type) {
                case BICst.Widget.COLUMN:
                case BICst.Widget.ACCUMULATE_COLUMN:
                    return "column";
                case BICst.Widget.AREA:
                    return "area";
                case BICst.Widget.LINE:
                    return "line";
                default:
                    return "column";
            }
        };

        var type = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(tId));
        switch (type) {
            case BICst.Widget.BAR:
            case BICst.Widget.ACCUMULATE_BAR:
                return "bar";
            case BICst.Widget.PIE:
            case BICst.Widget.DOUGHNUT:
                return "pie";
            case BICst.Widget.RADAR:
                return "radar";
            case BICst.Widget.MAP:
                return "map";
            case BICst.Widget.DASHBOARD:
                return "gauge";
            case BICst.Widget.BUBBLE:
                return "bubble";
            case BICst.Widget.SCATTER:
                return "scatter";
            case BICst.Widget.AXIS:
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
                    var wType = BI.Utils.getWidgetTypeByID(o.wId);
                    if(wType === BICst.Widget.ACCUMULATE_BAR || wType === BICst.Widget.BAR){
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
                    type: self._getChartTypeByTargetId(targetIds[0]),
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
                    if(wType === BICst.Widget.ACCUMULATE_BAR || wType === BICst.Widget.BAR){
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
                    type: self._getChartTypeByTargetId(targetIds[idx]),
                    name: BI.Utils.getDimensionNameByID(targetIds[idx])
                };
                var chart = BI.Utils.getDimensionStyleOfChartByID(targetIds[idx]);
                if(BI.has(chart, "type") && chart.type === BICst.Widget.ACCUMULATE_COLUMN){
                    res.stack = "stackChart";
                }
                return res;
            });
        }
    },

    _formatDataForBubble: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var result = BI.find(view, function(region, arr){
            return BI.isEmptyArray(arr);
        });
        if(BI.isNotNull(result) || BI.size(view) < this.constants.BUBBLE_REGION_COUNT){
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
        var result = BI.find(view, function(region, arr){
            return BI.isEmptyArray(arr);
        });
        if(BI.isNotNull(result) || BI.size(view) < this.constants.SCATTER_REGION_COUNT){
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

    _formatDataForAxis: function(da){
        var self = this, o = this.options;
        var data = this._formatDataForCommon(da);
        var view = BI.Utils.getWidgetViewByID(o.wId);
        BI.each(this.targetIds, function(idx, tId){
            if(BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], tId)){
                data[idx].yAxis = 1;
            }else{
                data[idx].yAxis = 0;
            }
        });
        return data;
    },

    _createDataByData: function (data) {
        var self = this, o = this.options;
        switch (BI.Utils.getWidgetTypeByID(o.wId)) {
            case BICst.Widget.BUBBLE:
                return self._formatDataForBubble(data);
            case BICst.Widget.SCATTER:
                return self._formatDataForScatter(data);
            case BICst.Widget.AXIS:
                return self._formatDataForAxis(data);
            case BICst.Widget.BAR:
            case BICst.Widget.ACCUMULATE_BAR:
            case BICst.Widget.PIE:
            case BICst.Widget.DOUGHNUT:
            case BICst.Widget.RADAR:
            case BICst.Widget.MAP:
            case BICst.Widget.DASHBOARD:
                return self._formatDataForCommon(data);

        }
    },

    populate: function () {
        var self = this, o = this.options;
        this.tarIdMap = {};
        BI.Utils.getWidgetDataByID(o.wId, function(jsonData){
            self.chartDisplay.setChartType(BI.Utils.getWidgetTypeByID(o.wId));
            var view = BI.Utils.getWidgetViewByID(o.wId);
            if(BI.has(view, BICst.REGION.TARGET2)
                && BI.isNotEmptyArray(view[BICst.REGION.TARGET2])
                && BI.Utils.getWidgetTypeByID(o.wId) === BICst.Widget.AXIS){
                self.chartDisplay.showTheOtherYAxis();
            }else{
                self.chartDisplay.hideTheOtherYAxis();
            }
            self.chartDisplay.populate(self._createDataByData(jsonData.data));
        });
    },

    _send2AllChildLinkWidget: function(wid, dId, clicked) {
        var self = this;
        var linkage = BI.Utils.getWidgetLinkageByID(wid);
        BI.each(linkage, function(i, link) {
            BI.Broadcasts.send(link.to, dId, clicked);
            self._send2AllChildLinkWidget(link.to);
        });
    }
});
$.shortcut('bi.chart_display', BI.ChartDisplay);
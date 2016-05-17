/**
 * 图表控件
 * @class BI.AxisChart
 * @extends BI.Widget
 */
BI.AxisChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-axis-chart"
        })
    },

    _init: function () {
        BI.AxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AxisChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.AxisChart.setChartType(BICst.WIDGET.AXIS);
        this.AxisChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AxisChart.EVENT_CHANGE, obj);
        });
    },

    _formatDataForCommon: function (data) {
        var self = this, o = this.options;
        this.regions = [];
        if (BI.has(data, "t")) {
            var top = data.t, left = data.l;
            return BI.map(top.c, function (id, tObj) {
                var data = BI.map(left.c, function (idx, obj) {
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
                var adjustData = BI.map(data.c, function (id, item) {
                    return {
                        x: item.n,
                        y: item.s[idx]
                    };
                });
                
                return {
                    data: adjustData
                    //name: o.seriesNames[idx]
                };
            });
        }
        return [];
    },

    _createDataByData: function (da) {
        var self = this, o = this.options;
        var data = this._formatDataForCommon(da);
        if (BI.isEmptyArray(data)) {
            return [];
        }
        return data;
    },

    populate: function (items) {
        this.AxisChart.resize();
        this.AxisChart.populate(this._createDataByData(items));
    },

    loading: function(){
        this.AxisChart.loading();
    },

    loaded: function(){
        this.AxisChart.loaded();
    },

    resize: function () {
        this.AxisChart.resize();
    }
});
BI.AxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.axis_chart', BI.AxisChart);
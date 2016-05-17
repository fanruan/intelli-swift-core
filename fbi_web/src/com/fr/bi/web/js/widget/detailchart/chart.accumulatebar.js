/**
 * 图表控件
 * @class BI.AccumulateBarChart
 * @extends BI.Widget
 */
BI.AccumulateBarChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateBarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-bar-chart"
        })
    },

    _init: function () {
        BI.AccumulateBarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AccumulateBarChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.AccumulateBarChart.setChartType(BICst.WIDGET.ACCUMULATE_BAR);
        this.AccumulateBarChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateBarChart.EVENT_CHANGE, obj);
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
                        "y": obj.n,
                        "x": obj.s.c[id].s
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
                        y: item.n,
                        x: item.s[idx]
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
        this.AccumulateBarChart.resize();
        this.AccumulateBarChart.populate(this._createDataByData(items));
    },

    loading: function(){
        this.AccumulateBarChart.loading();
    },

    loaded: function(){
        this.AccumulateBarChart.loaded();
    },

    resize: function () {
        this.AccumulateBarChart.resize();
    }
});
BI.AccumulateBarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_bar_chart', BI.AccumulateBarChart);
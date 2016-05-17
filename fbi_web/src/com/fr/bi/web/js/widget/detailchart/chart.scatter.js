/**
 * 图表控件
 * @class BI.ScatterChart
 * @extends BI.Widget
 */
BI.ScatterChart = BI.inherit(BI.Widget, {

    constants:{
        SCATTER_ITEM_COUNT: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.ScatterChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scatter-chart"
        })
    },

    _init: function () {
        BI.ScatterChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.ScatterChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.ScatterChart.setChartType(BICst.WIDGET.SCATTER);
        this.ScatterChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.ScatterChart.EVENT_CHANGE, obj);
        });
    },

    _createDataByData: function (da) {
        var self = this, o = this.options;
        var result = BI.find(da.c, function(idx, item){
            return BI.size(item.s) < self.constants.SCATTER_ITEM_COUNT || !BI.has(item, "n");
        });
        if (BI.isEmptyArray(da.c) || BI.isNotNull(result)) {
            return [];
        }
        return BI.map(da.c, function (idx, item) {
            return {
                data: [{
                    x: item.s[1],
                    y: item.s[0]
                }],
                name: item.n
            };
        });
    },

    populate: function (items) {
        this.ScatterChart.resize();
        this.ScatterChart.populate(this._createDataByData(items));
    },

    loading: function(){
        this.ScatterChart.loading();
    },

    loaded: function(){
        this.ScatterChart.loaded();
    },

    resize: function () {
        this.ScatterChart.resize();
    }
});
BI.ScatterChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.scatter_chart', BI.ScatterChart);
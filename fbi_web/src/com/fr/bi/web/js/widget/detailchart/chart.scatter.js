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

    populate: function (items) {
        this.ScatterChart.resize();
        this.ScatterChart.populate(BI.ScatterChart.formatItems(items));
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
BI.extend(BI.ScatterChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name
        }
    }
});
BI.ScatterChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.scatter_chart', BI.ScatterChart);
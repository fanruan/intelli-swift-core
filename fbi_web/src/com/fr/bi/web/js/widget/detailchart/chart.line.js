/**
 * 图表控件
 * @class BI.LineChart
 * @extends BI.Widget
 */
BI.LineChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.LineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-line-chart"
        })
    },

    _init: function () {
        BI.LineChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.LineChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.LineChart.setChartType(BICst.WIDGET.LINE);
        this.LineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.LineChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.LineChart.resize();
        this.LineChart.populate(items);
    },

    loading: function(){
        this.LineChart.loading();
    },

    loaded: function(){
        this.LineChart.loaded();
    },

    resize: function () {
        this.LineChart.resize();
    }
});
BI.LineChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.line_chart', BI.LineChart);
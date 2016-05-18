/**
 * 图表控件
 * @class BI.PercentAccumulateAreaChart
 * @extends BI.Widget
 */
BI.PercentAccumulateAreaChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PercentAccumulateAreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-percent-accumulate-area-chart"
        })
    },

    _init: function () {
        BI.PercentAccumulateAreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.PercentAccumulateAreaChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.PercentAccumulateAreaChart.setChartType(BICst.WIDGET.PERCENT_ACCUMULATE_AREA);
        this.PercentAccumulateAreaChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.PercentAccumulateAreaChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.PercentAccumulateAreaChart.resize();
        this.PercentAccumulateAreaChart.populate(items);
    },

    loading: function(){
        this.PercentAccumulateAreaChart.loading();
    },

    loaded: function(){
        this.PercentAccumulateAreaChart.loaded();
    },

    resize: function () {
        this.PercentAccumulateAreaChart.resize();
    }
});
BI.PercentAccumulateAreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.percent_accumulate_area_chart', BI.PercentAccumulateAreaChart);
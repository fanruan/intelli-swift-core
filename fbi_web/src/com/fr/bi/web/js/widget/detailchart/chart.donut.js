/**
 * 图表控件
 * @class BI.DonutChart
 * @extends BI.Widget
 */
BI.DonutChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DonutChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-donut-chart"
        })
    },

    _init: function () {
        BI.DonutChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.DonutChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.DonutChart.setChartType(BICst.WIDGET.DONUT);
        this.DonutChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.DonutChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.DonutChart.resize();
        this.DonutChart.populate(items);
    },

    loading: function(){
        this.DonutChart.loading();
    },

    loaded: function(){
        this.DonutChart.loaded();
    },

    resize: function () {
        this.DonutChart.resize();
    }
});
BI.DonutChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.donut_chart', BI.DonutChart);
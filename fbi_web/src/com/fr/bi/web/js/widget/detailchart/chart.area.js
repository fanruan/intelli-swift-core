/**
 * 图表控件
 * @class BI.AreaChart
 * @extends BI.Widget
 */
BI.AreaChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-area-chart"
        })
    },

    _init: function () {
        BI.AreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AreaChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.AreaChart.setChartType(BICst.WIDGET.AREA);
        this.AreaChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AreaChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.AreaChart.resize();
        this.AreaChart.populate(items);
    },

    loading: function(){
        this.AreaChart.loading();
    },

    loaded: function(){
        this.AreaChart.loaded();
    },

    resize: function () {
        this.AreaChart.resize();
    }
});
BI.AreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.area_chart', BI.AreaChart);
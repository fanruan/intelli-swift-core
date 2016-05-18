/**
 * 图表控件
 * @class BI.FunnelChart
 * @extends BI.Widget
 */
BI.FunnelChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.FunnelChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-funnel-chart"
        })
    },

    _init: function () {
        BI.FunnelChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.FunnelChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.FunnelChart.setChartType(BICst.WIDGET.FUNNEL);
        this.FunnelChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.FunnelChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.FunnelChart.resize();
        this.FunnelChart.populate(items);
    },

    loading: function(){
        this.FunnelChart.loading();
    },

    loaded: function(){
        this.FunnelChart.loaded();
    },

    resize: function () {
        this.FunnelChart.resize();
    }
});
BI.FunnelChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.funnel_chart', BI.FunnelChart);
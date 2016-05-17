/**
 * 图表控件
 * @class BI.DashboardChart
 * @extends BI.Widget
 */
BI.DashboardChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DashboardChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-chart"
        })
    },

    _init: function () {
        BI.DashboardChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.DashboardChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.DashboardChart.setChartType(BICst.WIDGET.DASHBOARD);
        this.DashboardChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.DashboardChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.DashboardChart.resize();
        this.DashboardChart.populate(items);
    },

    loading: function(){
        this.DashboardChart.loading();
    },

    loaded: function(){
        this.DashboardChart.loaded();
    },

    resize: function () {
        this.DashboardChart.resize();
    }
});
BI.DashboardChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.dashboard_chart', BI.DashboardChart);
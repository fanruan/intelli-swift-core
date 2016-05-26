/**
 * 图表控件
 * @class BI.MapChart
 * @extends BI.Widget
 */
BI.MapChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.MapChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-map-chart"
        })
    },

    _init: function () {
        BI.MapChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.MapChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.MapChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.MapChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.MapChart.populate([BI.MapChart.formatItems(items)]);
    },

    resize: function () {
        this.MapChart.resize();
    }
});
BI.MapChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.map_chart', BI.MapChart);
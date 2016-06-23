/**
 * 图表控件
 * @class BI.AxisChart
 * @extends BI.Widget
 */
BI.AxisChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-axis-chart"
        })
    },

    _init: function () {
        BI.AxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AxisChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.AxisChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AxisChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.AxisChart.populate([BI.AxisChart.formatItems(items)]);
    },

    resize: function () {
        this.AxisChart.resize();
    }
});
BI.AxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.axis_chart', BI.AxisChart);
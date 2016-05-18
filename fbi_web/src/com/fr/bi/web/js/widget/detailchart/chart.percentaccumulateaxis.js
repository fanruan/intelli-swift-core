/**
 * 图表控件
 * @class BI.PercentAccumulateAxisChart
 * @extends BI.Widget
 */
BI.PercentAccumulateAxisChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PercentAccumulateAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-percent-accumulate-axis-chart"
        })
    },

    _init: function () {
        BI.PercentAccumulateAxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.PercentAccumulateAxisChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.PercentAccumulateAxisChart.setChartType(BICst.WIDGET.PERCENT_ACCUMULATE_AXIS);
        this.PercentAccumulateAxisChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.PercentAccumulateAxisChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.PercentAccumulateAxisChart.resize();
        this.PercentAccumulateAxisChart.populate(BI.PercentAccumulateAxisChart.formatItems(items));
    },

    resize: function () {
        this.PercentAccumulateAxisChart.resize();
    }
});
BI.extend(BI.PercentAccumulateAxisChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name
        }
    }
});
BI.PercentAccumulateAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.percent_accumulate_axis_chart', BI.PercentAccumulateAxisChart);
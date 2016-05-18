/**
 * 图表控件
 * @class BI.AccumulateAreaChart
 * @extends BI.Widget
 */
BI.AccumulateAreaChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateAreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-area-chart"
        })
    },

    _init: function () {
        BI.AccumulateAreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AccumulateAreaChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.AccumulateAreaChart.setChartType(BICst.WIDGET.ACCUMULATE_AREA);
        this.AccumulateAreaChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateAreaChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.AccumulateAreaChart.populate(BI.AccumulateAreaChart.formatItems(items));
    },

    resize: function () {
        this.AccumulateAreaChart.resize();
    }
});
BI.extend(BI.AccumulateAreaChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name
        }
    }
});
BI.AccumulateAreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_area_chart', BI.AccumulateAreaChart);
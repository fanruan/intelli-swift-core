/**
 * 图表控件
 * @class BI.AccumulateRadarChart
 * @extends BI.Widget
 */
BI.AccumulateRadarChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateRadarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-bar-chart"
        })
    },

    _init: function () {
        BI.AccumulateRadarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AccumulateRadarChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.AccumulateRadarChart.setChartType(BICst.WIDGET.ACCUMULATE_RADAR);
        this.AccumulateRadarChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateRadarChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.AccumulateRadarChart.resize();
        this.AccumulateRadarChart.populate(BI.AccumulateRadarChart.formatItems(items));
    },

    resize: function () {
        this.AccumulateRadarChart.resize();
    }
});
BI.extend(BI.AccumulateRadarChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name
        }
    }
});
BI.AccumulateRadarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_radar_chart', BI.AccumulateRadarChart);
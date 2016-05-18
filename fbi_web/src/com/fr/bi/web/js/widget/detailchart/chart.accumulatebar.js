/**
 * 图表控件
 * @class BI.AccumulateBarChart
 * @extends BI.Widget
 */
BI.AccumulateBarChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateBarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-bar-chart"
        })
    },

    _init: function () {
        BI.AccumulateBarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AccumulateBarChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.AccumulateBarChart.setChartType(BICst.WIDGET.ACCUMULATE_BAR);
        this.AccumulateBarChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateBarChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.AccumulateBarChart.resize();
        this.AccumulateBarChart.populate(BI.AccumulateBarChart.formatItems(items));
    },

    resize: function () {
        this.AccumulateBarChart.resize();
    }
});
BI.extend(BI.AccumulateBarChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": BI.map(items[name], function(idx, item){
                return {
                    y: item.x,
                    x: item.y
                };
            }),
            "name": name
        }
    }
});
BI.AccumulateBarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_bar_chart', BI.AccumulateBarChart);
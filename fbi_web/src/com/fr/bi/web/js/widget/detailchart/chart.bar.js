/**
 * 图表控件
 * @class BI.BarChart
 * @extends BI.Widget
 */
BI.BarChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.BarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-bar-chart"
        })
    },

    _init: function () {
        BI.BarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.BarChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.BarChart.setChartType(BICst.WIDGET.BAR);
        this.BarChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.BarChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.BarChart.resize();
        this.BarChart.populate(BI.BarChart.formatItems(items));
    },

    resize: function () {
        this.BarChart.resize();
    }
});
BI.extend(BI.BarChart, {
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
BI.BarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.bar_chart', BI.BarChart);
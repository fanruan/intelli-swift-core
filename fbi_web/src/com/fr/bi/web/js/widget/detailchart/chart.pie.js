/**
 * 图表控件
 * @class BI.PieChart
 * @extends BI.Widget
 */
BI.PieChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PieChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-pie-chart"
        })
    },

    _init: function () {
        BI.PieChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.PieChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.PieChart.setChartType(BICst.WIDGET.PIE);
        this.PieChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.PieChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.PieChart.resize();
        this.PieChart.populate(BI.PieChart.formatItems(items));
    },

    resize: function () {
        this.PieChart.resize();
    }
});
BI.extend(BI.PieChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name
        }
    }
});
BI.PieChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.pie_chart', BI.PieChart);
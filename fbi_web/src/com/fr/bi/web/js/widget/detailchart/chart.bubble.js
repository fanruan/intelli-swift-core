/**
 * 图表控件
 * @class BI.BubbleChart
 * @extends BI.Widget
 */
BI.BubbleChart = BI.inherit(BI.Widget, {

    constants:{
        BUBBLE_ITEM_COUNT: 3
    },

    _defaultConfig: function () {
        return BI.extend(BI.BubbleChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scatter-chart"
        })
    },

    _init: function () {
        BI.BubbleChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.BubbleChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.BubbleChart.setChartType(BICst.WIDGET.BUBBLE);
        this.BubbleChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.BubbleChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.BubbleChart.resize();
        this.BubbleChart.populate(BI.BubbleChart.formatItems(items));
    },

    resize: function () {
        this.BubbleChart.resize();
    }
});
BI.extend(BI.BubbleChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name
        }
    }
});
BI.BubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.bubble_chart', BI.BubbleChart);
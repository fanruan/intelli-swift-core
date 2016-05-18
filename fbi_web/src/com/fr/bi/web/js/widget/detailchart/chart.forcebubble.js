/**
 * 图表控件
 * @class BI.ForceBubbleChart
 * @extends BI.Widget
 */
BI.ForceBubbleChart = BI.inherit(BI.Widget, {

    constants:{
        BUBBLE_ITEM_COUNT: 3
    },

    _defaultConfig: function () {
        return BI.extend(BI.ForceBubbleChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-force-chart"
        })
    },

    _init: function () {
        BI.ForceBubbleChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.ForceBubbleChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.ForceBubbleChart.setChartType(BICst.WIDGET.FORCE_BUBBLE);
        this.ForceBubbleChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.ForceBubbleChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.ForceBubbleChart.resize();
        this.ForceBubbleChart.populate(BI.ForceBubbleChart.formatItems(items));
    },

    resize: function () {
        this.ForceBubbleChart.resize();
    }
});
BI.extend(BI.ForceBubbleChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name
        }
    }
});
BI.ForceBubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.force_bubble_chart', BI.ForceBubbleChart);
/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CombineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart",
            items:[["",[{},{},{}]],[[{},{},{}]]],
            types: [[],[]]
        })
    },

    _init: function () {
        BI.CombineChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.CombineChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.CombineChart.setChartType(BICst.WIDGET.AXIS);
        this.CombineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, obj);
        });
    },

    _formatItems: function(items){
        var result = [], o=this.options;
        BI.each(items, function(i, belongAxisItems){
            BI.each(belongAxisItems, function(j, axisItems){
                result.push(BI.extend(BI.ChartCombineFormatItemFactory.formatItems(o.types[i][j], axisItems), {"yAxis": i}));
            });
        });
        return result;
    },

    setTypes: function(types){
        this.options.types = types||[[]];
    },

    populate: function (items, types) {
        if(BI.isNotNull(types)){
            this.setTypes(types);
        }
        this.CombineChart.populate(this._formatItems(items));
    }
});
BI.CombineChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.combine_chart', BI.CombineChart);
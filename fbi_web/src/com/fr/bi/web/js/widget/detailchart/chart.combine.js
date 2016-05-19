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
        this.CombineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, obj);
        });
    },

    _formatItems: function(items){
        var result = [], self = this, o = this.options;
        BI.each(items, function(i, belongAxisItems){
            var combineItems = BI.ChartCombineFormatItemFactory.combineItems(o.types[i], belongAxisItems);
            BI.each(combineItems, function(j, axisItems){
                axisItems.click = function () {
                    self.fireEvent(BI.CombineChart.EVENT_CHANGE, "assa");
                };
                result.push(BI.extend(axisItems, {"yAxis": i}));
            });
        });
        var typess=[];
        BI.each(o.types, function(idx, types){
            typess = BI.concat(typess, types);
        });
        var config = BI.ChartCombineFormatItemFactory.combineConfig(BI.uniq(typess));
        var yAxis = config.yAxis[0] || {};
        BI.each(o.types, function(idx, type){
            var newYAxis = BI.deepClone(yAxis);
            newYAxis.position = "right";
            idx > 0 && config.yAxis.push(newYAxis);
        });
        return [result, config];
    },

    setTypes: function(types){
        this.options.types = types||[[]];
    },

    populate: function (items, types) {
        if(BI.isNotNull(types)){
            this.setTypes(types);
        }
        var opts = this._formatItems(items);
        this.CombineChart.populate(opts[0], opts[1]);
    }
});
BI.CombineChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.combine_chart', BI.CombineChart);
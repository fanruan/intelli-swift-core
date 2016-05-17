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

    _createDataByData: function (da) {
        var self = this, o = this.options;
        var result = BI.find(da.c, function(idx, item){
            return BI.size(item.s) < self.constants.BUBBLE_ITEM_COUNT || !BI.has(item, "n");
        });
        if (BI.isEmptyArray(da.c) || BI.isNotNull(result)) {
            return [];
        }
        return BI.map(da.c, function (idx, item) {
            return {
                data: [{
                    x: item.s[1],
                    y: item.s[0],
                    size: item.s[2]
                }],
                name: item.n
            };
        });
    },

    populate: function (items) {
        this.BubbleChart.resize();
        this.BubbleChart.populate(this._createDataByData(items));
    },

    loading: function(){
        this.BubbleChart.loading();
    },

    loaded: function(){
        this.BubbleChart.loaded();
    },

    resize: function () {
        this.BubbleChart.resize();
    }
});
BI.BubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.bubble_chart', BI.BubbleChart);
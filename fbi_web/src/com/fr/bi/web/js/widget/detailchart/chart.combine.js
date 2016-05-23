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

    _axisConfig: function(){
        return {
            "enableMinorTick": false,
            "gridLineColor": "rgb(196,196,196)",
            "minorTickColor": "rgb(176,176,176)",
            "tickColor": "rgb(176,176,176)",
            "showArrow": false,
            "lineColor": "rgb(176,176,176)",
            "plotLines": [],
            "type": "value",
            "lineWidth": 0,
            "showLabel": true,
            "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
            "enableTick": true,
            "gridLineWidth": 0,
            "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "11pt", "fontWeight": ""},
            "plotBands": [],
            "labelRotation": 0,
            "reversed": false
        };
    },

    _formatItems: function(items){
        var result = [], self = this, o = this.options;
        BI.each(items, function(i, belongAxisItems){
            var combineItems = BI.ChartCombineFormatItemFactory.combineItems(o.types[i], belongAxisItems);
            BI.each(combineItems, function(j, axisItems){
                if(BI.isArray(axisItems)){
                    result = BI.concat(result, axisItems);
                }else{
                    result.push(BI.extend(axisItems, {"yAxis": i}));
                }
            });
        });
        var typess=[];
        BI.each(o.types, function(idx, types){
            typess = BI.concat(typess, types);
        });
        var config = BI.ChartCombineFormatItemFactory.combineConfig(typess[0]);
        config.plotOptions.click = function(){
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, BI.extend(this.pointOption, {seriesName: this.seriesName}));
        };
        config.xAxis = [];
        var newxAxis  = this._axisConfig();
        newxAxis.position = "bottom";
        newxAxis.gridLineWidth = 0;
        newxAxis.type = "category";
        config.xAxis.push(newxAxis);
        if(typess[0] === BICst.WIDGET.AXIS || typess[0] === BICst.WIDGET.AREA ||
            typess[0] === BICst.WIDGET.RADAR || typess[0] === BICst.WIDGET.BUBBLE || typess[0] === BICst.WIDGET.SCATTER){
            config.yAxis = [];
            BI.each(o.types, function(idx, type){
                if(BI.isEmptyArray(type)){
                    return;
                }
                var newYAxis = self._axisConfig();
                newYAxis.position = idx > 0 ? "right" : "left";
                newYAxis.gridLineWidth = idx > 0 ? 0 : 1;
                newYAxis.reversed = items[idx][0].reversed || false;
                if(items[idx][0].name === ""){
                    config.legend.enabled = false;
                }
                config.yAxis.push(newYAxis);
            });
        }
        if(typess[0] === BICst.WIDGET.BAR){
            newxAxis.type = "value";
            newxAxis.formatter = "function(){if(this>0) return this; else return this*(-1); }";
        }
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
    },

    resize: function(){
        this.CombineChart.resize();
    }
});
BI.CombineChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.combine_chart', BI.CombineChart);
/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    constants: {
        NORMAL: 1,
        ZERO2POINT: 2,
        ONE2POINT: 3,
        TWO2POINT: 4
    },

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

        //图可配置属性
        this.colors = [];
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
            //"formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
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
        switch (typess[0]){
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.FUNNEL:
                config.xAxis = [];
                var newxAxis  = this._axisConfig();
                newxAxis.position = "bottom";
                newxAxis.gridLineWidth = 0;
                newxAxis.type = "category";
                config.xAxis.push(newxAxis);
                config.yAxis = [];
                BI.each(o.types, function(idx, type){
                    if(BI.isEmptyArray(type)){
                        return;
                    }
                    var newYAxis = self._axisConfig();
                    newYAxis.position = idx > 0 ? "right" : "left";
                    newYAxis.gridLineWidth = idx > 0 ? 0 : 1;
                    if(BI.isNotEmptyArray(items)){
                        newYAxis.reversed = items[idx][0].reversed || false;
                        if(items[idx][0].name === ""){
                            config.legend.enabled = false;
                        }
                    }
                    config.yAxis.push(newYAxis);
                });
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                config.xAxis = [];
                var newxAxis  = this._axisConfig();
                newxAxis.position = "bottom";
                newxAxis.gridLineWidth = 0;
                newxAxis.type = "category";
                config.xAxis.push(newxAxis);
                newxAxis.type = "value";
                newxAxis.formatter = "function(){if(this>0) return this; else return this*(-1); }";
                break;

        }
        addOptionsToConfig();
        return [result, config];

        function addOptionsToConfig(){
            if(BI.isNotEmptyArray(self.chart_color)){
                config.colors = self.chart_color;
            }
            if(BI.has(config, "yAxis") && config.yAxis.length === 1){
                config.yAxis[0].reversed = self.left_y_axis_reversed;
                config.yAxis[0].text = self.show_left_y_axis_title === true ? self.left_y_axis_title : "";
                config.yAxis[0].formatter = formatTickInYaxis(self.left_y_axis_style);
            }
            if(BI.has(config, "yAxis") && config.yAxis.length === 2){
                config.yAxis[1].text = self.show_right_y_axis_title === true ? self.right_y_axis_title : "";
                config.yAxis[1].formatter = formatTickInYaxis(self.right_y_axis_style);
                config.yAxis[1].reversed = self.right_y_axis_reversed;
            }
        }

        function formatTickInYaxis(type){
            switch (type) {
                case self.constants.NORMAL:
                    return "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}";
                case self.constants.ZERO2POINT:
                    return "function(){return window.FR ? FR.contentFormat(arguments[0], '#0') : arguments[0]}";
                case self.constants.ONE2POINT:
                    return "function(){return window.FR ? FR.contentFormat(arguments[0], '#0.0#') : arguments[0]}";
                case self.constants.TWO2POINT:
                    return "function(){return window.FR ? FR.contentFormat(arguments[0], '#0.00') : arguments[0]}";
            }
        }
    },

    setTypes: function(types){
        this.options.types = types||[[]];
    },

    setOptions: function(options){
        this.left_y_axis_title = options.left_y_axis_title || "";
        this.right_y_axis_title = options.right_y_axis_title || "";
        this.chart_color = options.chart_color || [];
        this.left_y_axis_style = options.left_y_axis_style;
        this.right_y_axis_style = options.right_y_axis_style;
        this.show_left_y_axis_title = options.show_left_y_axis_title;
        this.show_right_y_axis_title = options.show_right_y_axis_title;
        this.left_y_axis_reversed = options.left_y_axis_reversed;
        this.right_y_axis_reversed = options.right_y_axis_reversed;
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
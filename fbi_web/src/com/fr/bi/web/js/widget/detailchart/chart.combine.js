/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    constants: {
        LEFT_AXIS: 0,
        RIGHT_AXIS: 1,
        ROTATION: -90,

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
            title: {},
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
            if(BI.has(config, "yAxis") && config.yAxis.length > 0){
                config.yAxis[0].reversed = self.left_y_axis_reversed ? !config.yAxis[0].reversed : config.yAxis[0].reversed;
                config.yAxis[0].formatter = formatTickInYaxis(self.left_y_axis_style, self.constants.LEFT_AXIS);
                formatNumberLevelInYaxis(self.left_y_axis_number_level, self.constants.LEFT_AXIS);
                config.yAxis[0].title.text = self.left_y_axis_title + getYAxisUnit(self.left_y_axis_number_level, self.constants.LEFT_AXIS);
                config.yAxis[0].title.text = self.show_left_y_axis_title === true ? config.yAxis[0].title.text : "";
                //config.yAxis[0].title.rotation = self.constants.ROTATION;
            }
            if(BI.has(config, "yAxis") && config.yAxis.length === 2){
                config.yAxis[1].formatter = formatTickInYaxis(self.right_y_axis_style, self.constants.RIGHT_AXIS);
                config.yAxis[1].reversed = self.right_y_axis_reversed ? !config.yAxis[1].reversed : config.yAxis[1].reversed;
                formatNumberLevelInYaxis(self.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                config.yAxis[1].title.text = self.right_y_axis_title + getYAxisUnit(self.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                config.yAxis[1].title.text = self.show_right_y_axis_title === true ? config.yAxis[1].title.text : "";
                //config.yAxis[1].title.rotation = self.constants.ROTATION;
            }
            if(BI.has(config, "xAxis") && config.xAxis.length > 0){
                config.xAxis[0].labelRotation = self.text_direction;
                config.xAxis[0].title.text = self.show_x_axis_title === true ? self.x_axis_title : "";
                config.xAxis[0].title.align = "center";
            }
        }

        function formatTickInYaxis(type, position){
            var formatter = '#.##';
            switch (type) {
                case self.constants.NORMAL:
                    formatter = '#.##';
                    break;
                case self.constants.ZERO2POINT:
                    formatter = '#0';
                    break;
                case self.constants.ONE2POINT:
                    formatter = '#0.0';
                    break;
                case self.constants.TWO2POINT:
                    formatter = '#0.00';
                    break;
            }
            if(position === self.constants.LEFT_AXIS){
                if(self.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            if(position === self.constants.RIGHT_AXIS){
                if(self.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            return "function(){return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]}"
        }

        function formatNumberLevelInYaxis(type, position){
            var magnify = 1;
            switch (type) {
                case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                    magnify = 1;
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                    magnify = 10000;
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                    magnify = 1000000;
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                    magnify = 100000000;
                    break;
            }
            if(magnify > 1){
                BI.each(result, function(idx, item){
                    BI.each(item.data, function(id, da){
                        if(position === item.yAxis){
                            da.y = da.y.div(magnify);
                        }
                    })
                })
            }
        }

        function getYAxisUnit(numberLevelType, position){
            var unit = BI.i18nText("BI-Count");
            switch (numberLevelType) {
                case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    unit = BI.i18nText("BI-Count");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                    unit = BI.i18nText("BI-Wan");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                    unit = BI.i18nText("BI-Million");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                    unit = BI.i18nText("BI-Yi");
                    break;
            }
            if(position === self.constants.LEFT_AXIS){
                self.left_y_axis_unit !== "" && (unit = unit + "/" + self.left_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS){
                self.right_y_axis_unit !== "" && (unit = unit + "/" + self.right_y_axis_unit)
            }
            return "(" + unit + ")";
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
        this.left_y_axis_number_level = options.left_y_axis_number_level;
        this.right_y_axis_number_level = options.right_y_axis_number_level;
        this.left_y_axis_unit = options.left_y_axis_unit;
        this.right_y_axis_unit = options.right_y_axis_unit;
        this.show_x_axis_title = options.show_x_axis_title;
        this.x_axis_title = options.x_axis_title;
        this.text_direction = options.text_direction || 0;
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
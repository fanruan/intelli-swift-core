/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    constants: {
        LEFT_AXIS: 0,
        RIGHT_AXIS: 1,
        RIGHT_AXIS_SECOND: 2,
        X_AXIS: 3,
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
                config.yAxis = [];
                var newxAxis = this._axisConfig();
                var newyAxis = this._axisConfig();
                newxAxis.position = "bottom";
                newxAxis.gridLineWidth = 0;
                config.xAxis.push(newxAxis);
                newxAxis.type = "value";
                newxAxis.formatter = "function(){if(this>0) return this; else return this*(-1); }";
                newyAxis.position = "left";
                newyAxis.type = "category";
                delete newyAxis.formatter;
                config.yAxis.push(newyAxis);
                break;

        }
        addOptionsToConfig();
        return [result, config];

        function addOptionsToConfig(){
            if(BI.isNotEmptyArray(self.chart_color)){
                config.colors = self.chart_color;
            }
            formatChartLineStyle();
            formatChartPieStyle();
            formatChartRadarStyle();
            formatElementAttrs();
            if(BI.has(config, "yAxis") && config.yAxis.length > 0){
                config.yAxis[0].reversed = self.left_y_axis_reversed ? !config.yAxis[0].reversed : config.yAxis[0].reversed;
                config.yAxis[0].formatter = formatTickInXYaxis(self.left_y_axis_style, self.constants.LEFT_AXIS);
                formatNumberLevelInYaxis(self.left_y_axis_number_level, self.constants.LEFT_AXIS);
                config.yAxis[0].title.text = self.left_y_axis_title + getXYAxisUnit(self.left_y_axis_number_level, self.constants.LEFT_AXIS);
                config.yAxis[0].title.text = self.show_left_y_axis_title === true ? config.yAxis[0].title.text : "";
                config.yAxis[0].gridLineWidth = self.show_grid_line === true ? 1 : 0;
                //config.yAxis[0].title.rotation = self.constants.ROTATION;
            }
            if(BI.has(config, "yAxis") && config.yAxis.length > 1){
                config.yAxis[1].formatter = formatTickInXYaxis(self.right_y_axis_style, self.constants.RIGHT_AXIS);
                config.yAxis[1].reversed = self.right_y_axis_reversed ? !config.yAxis[1].reversed : config.yAxis[1].reversed;
                formatNumberLevelInYaxis(self.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                config.yAxis[1].title.text = self.right_y_axis_title + getXYAxisUnit(self.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                config.yAxis[1].title.text = self.show_right_y_axis_title === true ? config.yAxis[1].title.text : "";
                config.yAxis[1].gridLineWidth = self.show_grid_line === true ? 1 : 0;
                //config.yAxis[1].title.rotation = self.constants.ROTATION;
            }
            if(BI.has(config, "yAxis") && config.yAxis.length > 2){
                config.yAxis[2].formatter = formatTickInXYaxis(self.right_y_axis_second_style, self.constants.RIGHT_AXIS_SECOND);
                config.yAxis[2].reversed = self.right_y_axis_second_reversed ? !config.yAxis[1].reversed : config.yAxis[2].reversed;
                formatNumberLevelInYaxis(self.right_y_axis_second_number_level, self.constants.RIGHT_AXIS_SECOND);
                config.yAxis[2].title.text = self.right_y_axis_second_title + getXYAxisUnit(self.right_y_axis_number_level, self.constants.RIGHT_AXIS_SECOND);
                config.yAxis[2].title.text = self.show_right_y_axis_second_title === true ? config.yAxis[2].title.text : "";
                config.yAxis[2].gridLineWidth = self.show_grid_line === true ? 1 : 0;
                //config.yAxis[1].title.rotation = self.constants.ROTATION;
            }
            if(BI.has(config, "xAxis") && config.xAxis.length > 0){
                config.xAxis[0].formatter = formatTickInXYaxis(self.x_axis_style, self.constants.X_AXIS);
                config.chartType === "bar" && delete config.yAxis[0].formatter;
                config.xAxis[0].labelRotation = self.text_direction;
                formatNumberLevelInXaxis(self.x_axis_number_level);
                config.xAxis[0].title.text = self.x_axis_title + getXYAxisUnit(self.x_axis_number_level, self.constants.X_AXIS);
                config.xAxis[0].title.text = self.show_x_axis_title === true ? config.xAxis[0].title.text : "";
                config.xAxis[0].title.align = "center";
                config.xAxis[0].gridLineWidth = self.show_grid_line === true ? 1 : 0;
            }
        }

        function formatTickInXYaxis(type, position){
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
            if(position === self.constants.X_AXIS){
                if(self.x_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
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
            if(position === self.constants.RIGHT_AXIS_SECOND){
                if(self.right_y_axis_second_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            return "function(){return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]}"
        }

        function formatNumberLevelInXaxis(type){
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
                        da.x = da.x.div(magnify);
                    })
                })
            }
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
                            if(item.type === BICst.WIDGET.BAR){
                                da.x = da.x.div(magnify);
                            }else{
                                da.y = da.y.div(magnify);
                            }
                        }
                    })
                })
            }
        }

        function getXYAxisUnit(numberLevelType, position){
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
            if(position === self.constants.X_AXIS){
                self.x_axis_unit !== "" && (unit = unit + "/" + self.x_axis_unit)
            }
            if(position === self.constants.LEFT_AXIS){
                self.left_y_axis_unit !== "" && (unit = unit + "/" + self.left_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS){
                self.right_y_axis_unit !== "" && (unit = unit + "/" + self.right_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS_SECOND){
                self.right_y_axis_second_unit !== "" && (unit = unit + "/" + self.right_y_axis_second_unit)
            }
            return "(" + unit + ")";
        }

        function formatChartLineStyle(){
            switch (self.chart_line_type) {
                case BICst.CHART_STYLE.RIGHT_ANGLE:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = true;
                    break;
                case BICst.CHART_STYLE.CURVE:
                    config.plotOptions.curve = true;
                    config.plotOptions.step = false;
                    break;
                case BICst.CHART_STYLE.NORMAL:
                default:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = false;
                    break;
            }
        }
        function formatChartPieStyle(){
            switch (self.chart_pie_type){
                case BICst.CHART_STYLE.EQUAL_ARC_ROSE:
                    config.plotOptions.roseType = "sameArc";
                    break;
                case BICst.CHART_STYLE.NOT_EQUAL_ARC_ROSE:
                    config.plotOptions.roseType = "differentArc";
                    break;
                case BICst.CHART_STYLE.NORMAL:
                default:
                    delete config.plotOptions.roseType;
                    break;
            }
            config.plotOptions.innerRadius = self.chart_inner_radius;
            config.plotOptions.endAngle = self.chart_total_angle;
        }

        function formatChartRadarStyle(){
            switch (self.chart_radar_type) {
                case BICst.CHART_STYLE.POLYGON:
                    config.plotOptions.roseType = "polygon";
                    break;
                case BICst.CHART_STYLE.CIRCLE:
                    config.plotOptions.roseType = "circle";
                    break;
            }
        }

        function formatElementAttrs(){
            switch (self.chart_legend) {
                case BICst.CHART_LEGEND.BOTTOM:
                    config.legend.enabled = true;
                    config.legend.position = "bottom";
                    break;
                case BICst.CHART_LEGEND.RIGHT:
                    config.legend.enabled = true;
                    config.legend.position = "right";
                    break;
                case BICst.CHART_LEGEND.NOT_SHOW:
                default:
                    config.legend.enabled = false;
                    break;
            }
            config.plotOptions.dataLabels.enabled = self.show_data_label;
            config.dataSheet.enabled = self.show_data_table;


        }
    },

    setTypes: function(types){
        this.options.types = types||[[]];
    },

    setOptions: function(options){
        this.left_y_axis_title = options.left_y_axis_title || "";
        this.right_y_axis_title = options.right_y_axis_title || "";
        this.right_y_axis_second_title = options.right_y_axis_second_title || "";
        this.chart_color = options.chart_color || [];
        this.chart_line_type = options.chart_line_type || BICst.CHART_STYLE.NORMAL;
        this.chart_pie_type = options.chart_pie_type || BICst.CHART_STYLE.NORMAL;
        this.chart_inner_radius = options.chart_inner_radius || 0;
        this.chart_total_angle = options.chart_total_angle || BICst.PIE_ANGLES.TOTAL;
        this.chart_radar_type = options.chart_radar_type || BICst.CHART_STYLE.NORMAL;
        this.left_y_axis_style = options.left_y_axis_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.x_axis_style = options.x_axis_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.right_y_axis_style = options.right_y_axis_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.right_y_axis_second_style = options.right_y_axis_second_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.show_x_axis_title = options.show_x_axis_title || false;
        this.show_left_y_axis_title = options.show_left_y_axis_title || false;
        this.show_right_y_axis_title = options.show_right_y_axis_title || false;
        this.show_right_y_axis_second_title = options.show_right_y_axis_second_title || false;
        this.left_y_axis_reversed = options.left_y_axis_reversed || false;
        this.right_y_axis_reversed = options.right_y_axis_reversed || false;
        this.right_y_axis_second_reversed = options.right_y_axis_second_reversed || false;
        this.x_axis_number_level = options.x_axis_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.left_y_axis_number_level = options.left_y_axis_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.right_y_axis_number_level = options.right_y_axis_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.right_y_axis_second_number_level = options.right_y_axis_second_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.x_axis_unit = options.x_axis_unit || "";
        this.left_y_axis_unit = options.left_y_axis_unit || "";
        this.right_y_axis_unit = options.right_y_axis_unit || "";
        this.right_y_axis_second_unit = options.right_y_axis_second_unit || "";
        this.x_axis_title = options.x_axis_title || "";
        this.chart_legend = options.chart_legend || BICst.CHART_LEGEND.NOT_SHOW;
        this.show_data_label = options.show_data_label || false;
        this.show_data_table = options.show_data_table || false;
        this.show_grid_line = options.show_grid_line || true;
        this.text_direction = options.text_direction;
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
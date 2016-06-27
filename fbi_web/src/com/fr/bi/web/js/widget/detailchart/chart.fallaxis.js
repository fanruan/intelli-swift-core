/**
 * 图表控件
 * @class BI.FallAxisChart
 * @extends BI.Widget
 */
BI.FallAxisChart = BI.inherit(BI.Widget, {

    constants: {
        LEFT_AXIS: 0,
        RIGHT_AXIS: 1,
        RIGHT_AXIS_SECOND: 2,
        X_AXIS: 3,
        ROTATION: -90,
        NORMAL: 1,
        LEGEND_BOTTOM: 4,
        ZERO2POINT: 2,
        ONE2POINT: 3,
        TWO2POINT: 4,
        STYLE_NORMAL: 21
    },

    _defaultConfig: function () {
        return BI.extend(BI.FallAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fall-axis-chart"
        })
    },

    _init: function () {
        BI.FallAxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.xAxis = [{
            type: "category",
            title: {
                style: {"fontFamily":"Verdana","color":"rgba(102,102,102,1.0)","fontSize":"11pt","fontWeight":""}
            },
            position: "bottom",
            gridLineWidth: 0
        }];
        this.yAxis = [{
            type: "value",
            title: {
                style: {"fontFamily":"Verdana","color":"rgba(102,102,102,1.0)","fontSize":"11pt","fontWeight":""}
            },
            position: "left",
            gridLineWidth: 0
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.FallAxisChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        config.colors = this.config.chart_color;
        config.style = formatChartStyle();
        formatCordon();
        config.legend.enabled = false;
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.dataSheet.enabled = this.config.show_data_table;
        if(config.dataSheet.enabled === true){
            config.xAxis[0].showLabel = false;
        }
        config.zoom.zoomTool.visible = this.config.show_zoom;
        this.config.show_zoom === true && delete config.dataSheet;
        config.yAxis = this.yAxis;

        config.yAxis[0].reversed = this.config.left_y_axis_reversed;
        config.yAxis[0].formatter = formatTickInXYaxis(this.config.left_y_axis_style, this.constants.LEFT_AXIS);
        formatNumberLevelInYaxis(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = getXYAxisUnit(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = this.config.show_left_y_axis_title === true ? this.config.left_y_axis_title + config.yAxis[0].title.text : config.yAxis[0].title.text;
        config.yAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.yAxis[0].title.rotation = this.constants.ROTATION;

        config.xAxis[0].title.text = this.config.x_axis_title;
        config.xAxis[0].labelRotation = this.config.text_direction;
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? config.xAxis[0].title.text : "";
        config.xAxis[0].title.align = "center";
        config.xAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;

        return [items, config];

        function formatChartStyle(){
            switch (self.config.chart_style) {
                case BICst.CHART_STYLE.STYLE_GRADUAL:
                    return "gradual";
                case BICst.CHART_STYLE.STYLE_NORMAL:
                default:
                    return "normal";
            }
        }

        function formatCordon(){
            BI.each(self.config.cordon, function(idx, cor){
                if(idx === 0 && self.xAxis.length > 0){
                    var magnify = calcMagnify(self.config.x_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function(i, t){
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": {"fontFamily": "Arial", "color": "rgba(0,0,0,1.0)", "fontSize": "9pt", "fontWeight": ""},
                                "text": t.text,
                                "align": "top"
                            }
                        });
                    });
                }
                if(idx > 0 && self.yAxis.length >= idx){
                    var magnify = 1;
                    switch (idx - 1) {
                        case self.constants.LEFT_AXIS:
                            magnify = calcMagnify(self.config.left_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS:
                            magnify = calcMagnify(self.config.right_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS_SECOND:
                            magnify = calcMagnify(self.config.right_y_axis_second_number_level);
                            break;
                    }
                    self.yAxis[idx - 1].plotLines = BI.map(cor, function(i, t){
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": {"fontFamily": "Arial", "color": "rgba(0,0,0,1.0)", "fontSize": "9pt", "fontWeight": ""},
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }

        function formatNumberLevelInYaxis(type, position){
            var magnify = calcMagnify(type);
            if(magnify > 1){
                BI.each(items, function(idx, item){
                    BI.each(item.data, function(id, da){
                        if (position === item.yAxis) {
                            da.y = da.y || 0;
                            da.y = da.y.div(magnify);
                        }
                    })
                })
            }
            if(type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                config.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
            }
        }

        function calcMagnify(type){
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
            return magnify;
        }

        function getXYAxisUnit(numberLevelType, position){
            var unit = "";
            switch (numberLevelType) {
                case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    unit = "";
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
                self.config.x_axis_unit !== "" && (unit = unit + self.config.x_axis_unit)
            }
            if(position === self.constants.LEFT_AXIS){
                self.config.left_y_axis_unit !== "" && (unit = unit + self.config.left_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS){
                self.config.right_y_axis_unit !== "" && (unit = unit + self.config.right_y_axis_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
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
            if(position === self.constants.LEFT_AXIS){
                if(self.config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            if(position === self.constants.RIGHT_AXIS){
                if(self.config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
        }
    },

    _formatItems: function(items){
        var o = this.options;
        if(BI.isEmptyArray(items)){
            return [];
        }
        items = items[0];
        var tables = [], sum = 0;
        var colors = this.config.chart_color || [];
        if(BI.isEmptyArray(colors)){
            colors = ["rgb(152, 118, 170)", "rgb(0, 157, 227)"];
        }
        BI.each(items, function(idx, item){
            BI.each(item.data, function(i, t){
                if(t.y < 0){
                    tables.push([t.x, t.y, sum + t.y, t.targetIds]);
                }else{
                    tables.push([t.x, t.y, sum, t.targetIds]);
                }
                sum += t.y;
            });
        });

        return [BI.map(BI.makeArray(2, null), function(idx, item){
            return {
                "data": BI.map(tables, function(id, cell){
                    var axis = BI.extend({targetIds: cell[3]}, {
                        x: cell[0],
                        y: Math.abs(cell[2 - idx])
                    });
                    if(idx === 1){
                        axis.color = cell[2 - idx] < 0 ? colors[1] : colors[0];
                    }else{
                        axis.color = "rgba(0,0,0,0)";
                        axis.borderColor = "rgba(0,0,0,0)";
                        axis.borderWidth = 0;
                        axis.clickColor = "rgba(0,0,0,0)";
                        axis.mouseOverColor = "rgba(0,0,0,0)";
                        axis.tooltip = {
                            enable: false
                        }
                    }
                    return axis;
                }),
                stack: "stackedFall",
                name: ""
            };
        })];
    },

    populate: function (items, options) {
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            chart_color: options.chart_color || ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"],
            chart_style: options.chart_style || c.NORMAL,
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            x_axis_title: options.x_axis_title || "",
            show_data_label: options.show_data_label || false,
            show_data_table: options.show_data_table || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            show_zoom: options.show_zoom || false,
            text_direction: options.text_direction || 0,
            cordon: options.cordon || []
        };
        this.options.items = items;
        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.AXIS);
            });
            types.push(type);
        });

        this.combineChart.populate(this._formatItems(items), types);
    },

    resize: function () {
        this.combineChart.resize();
    }
});
BI.FallAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.fall_axis_chart', BI.FallAxisChart);
/**
 * 图表控件
 * @class BI.CompareBarChart
 * @extends BI.Widget
 */
BI.CompareBarChart = BI.inherit(BI.Widget, {

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
        MINLIMIT: 1e-6,
        LEGEND_HEIGHT: 80
    },

    _defaultConfig: function () {
        return BI.extend(BI.CompareBarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-compare-bar-chart"
        })
    },

    _init: function () {
        BI.CompareBarChart.superclass._init.apply(this, arguments);
        var self = this;
        this.xAxis = [{
            type: "value",
            title: {
                style: {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""}
            },
            labelStyle: {
                "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
            },
            formatter: "function(){if(this>0) return this; else return this*(-1); }",
            gridLineWidth: 0
        }];
        this.yAxis = [{
            type: "category",
            title: {
                style: {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""}
            },
            labelStyle: {
                "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
            },
            gridLineWidth: 0,
            position: "left"
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CompareBarChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        config.colors = this.config.chart_color;
        config.style = formatChartStyle();
        formatCordon();
        switch (this.config.chart_legend){
            case BICst.CHART_LEGENDS.BOTTOM:
                config.legend.enabled = true;
                config.legend.position = "bottom";
                config.legend.maxHeight = self.constants.LEGEND_HEIGHT;
                break;
            case BICst.CHART_LEGENDS.RIGHT:
                config.legend.enabled = true;
                config.legend.position = "right";
                break;
            case BICst.CHART_LEGENDS.NOT_SHOW:
            default:
                config.legend.enabled = false;
                break;
        }
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.dataSheet.enabled = this.config.show_data_table;
        if(config.dataSheet.enabled === true){
            config.xAxis[0].showLabel = false;
        }
        config.zoom.zoomTool.visible = this.config.show_zoom;
        if(this.config.show_zoom === true){
            delete config.dataSheet;
            delete config.zoom.zoomType;
        }
        config.plotOptions.tooltip.formatter.valueFormat = "function(){if(this > 0){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0];} else {return window.FR ? (-1) * FR.contentFormat(arguments[0], '#.##') : (-1) * arguments[0];}}";

        config.yAxis = this.yAxis;
        config.yAxis[0].title.text = getXYAxisUnit(this.config.x_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = this.config.show_left_y_axis_title === true ? this.config.x_axis_title + config.yAxis[0].title.text : config.yAxis[0].title.text;
        config.yAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.yAxis[0].title.rotation = this.constants.ROTATION;

        config.yAxis[0].labelRotation = this.config.text_direction;
        config.xAxis[0].formatter = formatTickInXYaxis(this.config.left_y_axis_style, this.constants.X_AXIS);
        formatNumberLevelInXaxis(this.config.left_y_axis_number_level);
        config.xAxis[0].title.text = getXYAxisUnit(this.config.left_y_axis_number_level, this.constants.X_AXIS);
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? this.config.left_y_axis_title + config.xAxis[0].title.text : config.xAxis[0].title.text;
        config.xAxis[0].title.align = "center";
        config.xAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.chartType = "bar";
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
                    var magnify = calcMagnify(self.config.left_y_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function(i, t){
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""},
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
                            magnify = calcMagnify(self.config.x_axis_number_level);
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
                                "style": {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""},
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }

        function formatNumberLevelInXaxis(type){
            var magnify = calcMagnify(type);
            if(magnify > 1){
                BI.each(items, function(idx, item){
                    BI.each(item.data, function(id, da){
                        da.x = da.x || 0;
                        da.x = da.x.div(magnify);
                        if(self.constants.MINLIMIT.sub(Math.abs(da.x)) > 0){
                            da.x = 0;
                        }
                    })
                })
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
                self.config.left_y_axis_unit !== "" && (unit = unit + self.config.left_y_axis_unit)
            }
            if(position === self.constants.LEFT_AXIS){
                self.config.x_axis_unit !== "" && (unit = unit + self.config.x_axis_unit)
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
            if(position === self.constants.X_AXIS){
                if(self.config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                    return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "').substring(1) : arguments[0].substring(1);}"
                }
            }
            return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (FR.contentFormat(arguments[0], '" + formatter + "') + '').substring(1) : (arguments[0] + '').substring(1);}"
        }
    },

    _formatItems: function(items){
        var result = [];
        var i = BI.UUID();
        BI.each(items, function (idx, item) {
            BI.each(item, function (id, it) {
                BI.each(it.data, function (i, t) {
                    var tmp = t.x;
                    t.x = t.y;
                    t.y = tmp;
                    if (idx === 0) {
                        t.x = -t.x;
                    }
                });
                it.stack = i;
            })
        });
        BI.each(items, function(idx, item){
            result = BI.concat(result, item);
        });
        return [result];
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            chart_color: options.chart_color || [],
            chart_style: options.chart_style || c.STYLE_NORMAL,
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            x_axis_style: options.x_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
            x_axis_number_level: options.x_axis_number_level || c.NORMAL,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
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
                type.push(BICst.WIDGET.BAR);
            });
            types.push(type);
        });
        this.combineChart.populate(this._formatItems(items), types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function(){
        this.combineChart.magnify();
    }
});
BI.CompareBarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.compare_bar_chart', BI.CompareBarChart);
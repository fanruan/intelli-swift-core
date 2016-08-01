/**
 * 图表控件
 * @class BI.BubbleChart
 * @extends BI.Widget
 */
BI.BubbleChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.BubbleChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scatter-chart"
        })
    },

    _init: function () {
        BI.BubbleChart.superclass._init.apply(this, arguments);
        var self = this;
        this.xAxis = [{
            type: "value",
            title: {
                style: {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""}
            },
            labelStyle: {
                "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
            },
            position: "bottom",
            gridLineWidth: 0
        }];
        this.yAxis = [{
            type: "value",
            title: {
                style: {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""}
            },
            labelStyle: {
                "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
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
            self.fireEvent(BI.BubbleChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        config.colors = this.config.chart_color;
        config.style = formatChartStyle();
        config.plotOptions.tooltip.formatter = this.config.tooltip;
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
        config.plotOptions.dataLabels.formatter.identifier = "${X}${Y}${SIZE}";
        config.plotOptions.shadow = this.config.bubble_style !== this.constants.NO_PROJECT;
        config.yAxis = this.yAxis;

        config.yAxis[0].formatter = formatTickInXYaxis(this.config.left_y_axis_style, this.constants.LEFT_AXIS);
        formatNumberLevelInYaxis(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = getXYAxisUnit(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = this.config.show_left_y_axis_title === true ? this.config.left_y_axis_title + config.yAxis[0].title.text : config.yAxis[0].title.text;
        config.yAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.yAxis[0].title.rotation = this.constants.ROTATION;

        config.xAxis[0].formatter = formatTickInXYaxis(this.config.x_axis_style, this.constants.X_AXIS);
        formatNumberLevelInXaxis(this.config.x_axis_number_level);
        config.xAxis[0].title.text = getXYAxisUnit(this.config.x_axis_number_level, this.constants.X_AXIS);
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? this.config.x_axis_title + config.xAxis[0].title.text : config.xAxis[0].title.text;
        config.xAxis[0].title.align = "center";
        config.xAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.chartType = "bubble";

        ////为了给数据标签加个%,还要遍历所有的系列，唉
        if(config.plotOptions.dataLabels.enabled === true){
            BI.each(items, function(idx, item){
                var isNeedFormatDataLabelX = false;
                var isNeedFormatDataLabelY = false;
                if (self.config.x_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    isNeedFormatDataLabelX = true;
                }
                if (self.config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    isNeedFormatDataLabelY = true;
                }
                if(isNeedFormatDataLabelX === true || isNeedFormatDataLabelY === true){
                    item.dataLabels = {
                        "style": "{fontFamily:Microsoft YaHei, color: #808080, fontSize: 12pt}",
                        "align": "outside",
                        enabled: true,
                        formatter: {
                            identifier: "${X}${Y}${SIZE}",
                            "XFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                            "YFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                            "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        }
                    };
                    if(isNeedFormatDataLabelX === true){
                        item.dataLabels.formatter.XFormat = config.xAxis[0].formatter;
                    }
                    if(isNeedFormatDataLabelY === true){
                        item.dataLabels.formatter.YFormat = config.yAxis[0].formatter;
                    }
                }
            });
        }

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
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                    if(!BI.isNumber(da.x)){
                        da.x = BI.parseFloat(da.x);
                    }
                    da.x = da.x || 0;
                    da.x = da.x.div(magnify).toFixed(self.constants.FIX_COUNT);
                    if (self.constants.MINLIMIT.sub(Math.abs(da.x)) > 0) {
                        da.x = 0;
                    }
                })
            })
        }

        function formatNumberLevelInYaxis(type, position){
            var magnify = calcMagnify(type);
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                    if (position === item.yAxis) {
                        if(!BI.isNumber(da.y)){
                            da.y = BI.parseFloat(da.y);
                        }
                        da.y = da.y || 0;
                        da.y = da.y.div(magnify).toFixed(self.constants.FIX_COUNT);
                        if (self.constants.MINLIMIT.sub(Math.abs(da.y)) > 0) {
                            da.y = 0;
                        }
                    }
                })
            })
            if(type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                //config.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
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
            if(position === self.constants.X_AXIS){
                if(self.config.x_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            return "function(){return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0];}"
        }
    },

    _formatItems: function(items){
        BI.each(items, function(idx, item){
            BI.each(item, function(id, it){
                BI.each(it.data, function(i, da){
                    da.size = da.z;
                })
            })
        });
        return items;
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            chart_color: options.chart_color || [],
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            x_axis_style: options.x_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            x_axis_number_level: options.x_axis_number_level || c.NORMAL,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            cordon: options.cordon || [],
            tooltip: options.tooltip || "",
            bubble_style: options.bubble_style || c.NO_PROJECT
        };
        this.options.items = items;
        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.BUBBLE);
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
BI.BubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.bubble_chart', BI.BubbleChart);

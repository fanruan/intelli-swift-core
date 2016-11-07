/**
 * 图表控件
 * @class BI.BubbleChart
 * @extends BI.Widget
 */
BI.BubbleChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.BubbleChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-bubble-chart"
        })
    },

    _init: function () {
        BI.BubbleChart.superclass._init.apply(this, arguments);
        var self = this;
        this.xAxis = [{
            type: "value",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE,
            position: "bottom",
            gridLineWidth: 0
        }];
        this.yAxis = [{
            type: "value",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE,
            position: "left",
            gridLineWidth: 0
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            popupItemsGetter: o.popupItemsGetter,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.BubbleChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this, c = this.constants;

        formatCordon();
        formatForSize();

        switch (this.config.rules_display) {
            case BICst.DISPLAY_RULES.FIXED:
                delete config.legend;
                formatFixedLegend();
                break;
            case BICst.DISPLAY_RULES.GRADIENT:
                delete config.legend;
                formatGradientLegend();
                break;
            case BICst.DISPLAY_RULES.DIMENSION:
            default:
                formatLegend();
                break;
        }

        BI.extend(config.plotOptions, {
            large: this.config.big_data_mode,
            shadow: this.config.bubble_style !== c.NO_PROJECT
        });

        config.colors = this.config.chart_color;
        config.style = formatChartStyle();
        config.plotOptions.tooltip.formatter = this.config.tooltip;
        config.plotOptions.bubble.minSize = this.config.bubble_min_size;
        config.plotOptions.bubble.maxSize = this.config.bubble_max_size;
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.plotOptions.dataLabels.formatter.identifier = "${X}${Y}${SIZE}";
        config.plotOptions.shadow = this.config.bubble_style !== c.NO_PROJECT;
        config.yAxis = this.yAxis;

        formatNumberLevelInYaxis(this.config.left_y_axis_number_level, c.LEFT_AXIS);

        BI.extend(config.yAxis[0], self.leftAxisSetting(this.config));

        self.formatNumberLevelInXaxis(items, this.config.right_y_axis_number_level);

        BI.extend(config.xAxis[0], self.rightAxisSetting(this.config));
        config.xAxis[0].gridLineColor = this.config.v_grid_line_color;

        config.chartType = "bubble";

        if (BI.isNotEmptyArray(this.config.tooltip)) {
            config.plotOptions.tooltip.formatter = function () {
                var y = self.formatTickInXYaxis(self.config.left_y_axis_style, self.config.left_y_axis_number_level, self.config.num_separators)(this.y);
                var x = self.formatTickInXYaxis(self.config.right_y_axis_style, self.config.right_y_axis_number_level, self.config.right_num_separators)(this.x);
                var size =  BI.contentFormat(this.size,
                    self.formatToolTipAndDataLabel(items[0].settings.format || c.NORMAL, items[0].settings.num_level || c.NORMAL,
                        items[0].settings.unit || "", items[0].settings.num_separators || c.NUM_SEPARATORS));
                return this.seriesName + '<div>(X)' + self.config.tooltip[0] + ':' + x + '</div><div>(Y)' + self.config.tooltip[1]
                    + ':' + y + '</div><div>(' + BI.i18nText("BI-Size") + ')' + self.config.tooltip[2] + ':' + size + '</div>'
            };
        }

        //为了给数据标签加个%,还要遍历所有的系列，唉
        if (config.plotOptions.dataLabels.enabled === true) {
            BI.each(items, function (idx, item) {
                item.dataLabels = {
                    "style" : self.config.chart_font,
                    "align": "outside",
                    "autoAdjust": true,
                    enabled: true,
                    formatter: {
                        identifier: "${X}${Y}${SIZE}",
                        "XFormat": function () {
                            return BI.contentFormat(arguments[0], '#.##;-#.##')
                        },
                        "YFormat": function () {
                            return BI.contentFormat(arguments[0], '#.##;-#.##')
                        },
                        "sizeFormat": function () {
                            return BI.contentFormat(arguments[0], '#.##;-#.##')
                        }
                    }
                };
                if(item.settings.num_level) {
                    item.data[0].z = item.data[0].size = self.formatXYDataWithMagnify(item.data[0].z, self.calcMagnify(item.settings.num_level))
                }
                item.dataLabels.formatter.XFormat = config.xAxis[0].formatter;
                item.dataLabels.formatter.YFormat = config.yAxis[0].formatter;
                item.dataLabels.formatter.sizeFormat = function () {
                    return BI.contentFormat(arguments[0],
                        self.formatToolTipAndDataLabel(item.settings.format || c.NORMAL, item.settings.num_level || c.NORMAL,
                            item.settings.unit || "", item.settings.num_separators || c.NUM_SEPARATORS));
                }
            });
        }

        config.legend.style = BI.extend( this.config.chart_legend_setting, {
            fontSize:  this.config.chart_legend_setting.fontSize + "px"
        });

        //全局样式图表文字
        config.yAxis[0].title.style = this.config.chart_font;
        config.xAxis[0].title.style = this.config.chart_font;

        return [items, config];

        function formatChartStyle() {
            switch (self.config.chart_style) {
                case BICst.CHART_STYLE.STYLE_GRADUAL:
                    return "gradual";
                case BICst.CHART_STYLE.STYLE_NORMAL:
                default:
                    return "normal";
            }
        }

        function formatForSize() {

        }

        function formatLegend() {
            switch (self.config.chart_legend) {
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
        }

        function formatFixedLegend() {
            var min = calculateMaxAndMin()[0];
            var max = calculateMaxAndMin()[1];
            var range = [];

            BI.extend(config.rangeLegend, {
                enabled: true,
                visible: true,
                continuous: false,
                formatter: function () {
                    return this.to
                }
            });

            BI.each(self.config.fixed_colors, function (idx, item) {
                if(idx == 0 && min < item.range.min){
                    range.push({
                        from: min,
                        to: item.range.min,
                        color: '#808080'
                    })
                }

                range.push({
                    from: item.range.min,
                    to: item.range.max,
                    color: item.color
                });

                if(idx == (self.config.fixed_colors.length-1) && max > item.range.max){
                   range.push({
                       from: item.range.max,
                       to: max,
                       color: item.color
                   })
                }
            });

            config.rangeLegend.range = range;

            switch (self.config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    config.rangeLegend.position = "bottom";
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    config.rangeLegend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    config.rangeLegend.visible = false;
                    break;
            }
        }

        function formatGradientLegend() {
            var min = calculateMaxAndMin()[0];
            var max = calculateMaxAndMin()[1];
            var color = [];

            BI.extend(config.rangeLegend, {
                enabled: true,
                visible: true,
                continuous: true,
                formatter: function () {
                    return this.to
                }
            });

            config.rangeLegend.range.min = min;
            config.rangeLegend.range.max = max;

            BI.each(self.config.gradient_colors, function (idx, item) {
                var minProp = (item.range.min - min) / (max - min);
                var maxProp = (item.range.max - min) / (max - min);

                if (idx == 0 && minProp > 0) {
                    color.push([0, '#65B3EE'])
                }

                if (minProp >= 1) {
                    return true
                } else if (maxProp > 1) {
                    color.push([minProp, item.color_range.from_color]);
                    color.push([1, item.color_range.to_color]);
                    return true
                }

                color.push([minProp, item.color_range.from_color]);
                color.push([maxProp, item.color_range.to_color])
            });

            if (color.length > 1) {
                config.rangeLegend.range.color = color;
            }

            switch (self.config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    config.rangeLegend.position = "bottom";
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    config.rangeLegend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    config.rangeLegend.visible = false;
                    break;
            }
        }

        function calculateMaxAndMin() {
            var max = null, min = null;
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (i, da) {
                    if (BI.isNull(max) || max < da.z) {
                        max = da.z
                    }
                    if (BI.isNull(min) || min > da.z) {
                        min = da.z
                    }
                })
            });
            return _calculateValueNiceDomain(min, max);
        }

        function _calculateValueNiceDomain(minValue, maxValue) {
            minValue = Math.min(0, minValue);
            var tickInterval = _linearTickInterval(minValue, maxValue);

            return _linearNiceDomain(minValue, maxValue, tickInterval);
        }

        function _linearTickInterval(minValue, maxValue, m) {
            m = m || 5;
            var span = maxValue - minValue;
            var step = Math.pow(10, Math.floor(Math.log(span / m) / Math.LN10));
            var err = m / span * step;

            if (err <= .15) step *= 10; else if (err <= .35) step *= 5; else if (err <= .75) step *= 2;

            return step;
        }

        function _linearNiceDomain(minValue, maxValue, tickInterval) {
            minValue = VanUtils.accMul(Math.floor(minValue / tickInterval), tickInterval);
            maxValue = VanUtils.accMul(Math.ceil(maxValue / tickInterval), tickInterval);

            return [minValue, maxValue];
        }

        function formatCordon() {
            BI.each(self.config.cordon, function (idx, cor) {
                if (idx === 0 && self.xAxis.length > 0) {
                    var magnify = self.calcMagnify(self.config.x_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style" : self.config.chart_font,
                                "text": t.text,
                                "align": "top"
                            }
                        });
                    });
                }
                if (idx > 0 && self.yAxis.length >= idx) {
                    var magnify = 1;
                    switch (idx - 1) {
                        case self.constants.LEFT_AXIS:
                            magnify = self.calcMagnify(self.config.left_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS:
                            magnify = self.calcMagnify(self.config.right_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS_SECOND:
                            magnify = self.calcMagnify(self.config.right_y_axis_second_number_level);
                            break;
                    }
                    self.yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style" : self.config.chart_font,
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }

        function formatNumberLevelInYaxis(type, position) {
            var magnify = self.calcMagnify(type);
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                    if (position === item.yAxis) {
                        da.y = self.formatXYDataWithMagnify(da.y, magnify);
                    }
                })
            });
        }

        function getXYAxisUnit(numberLevelType, position) {
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
            if (position === self.constants.RIGHT_AXIS) {
                self.config.right_y_axis_unit !== "" && (unit = unit + self.config.right_y_axis_unit)
            }
            if (position === self.constants.LEFT_AXIS) {
                self.config.left_y_axis_unit !== "" && (unit = unit + self.config.left_y_axis_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
        }
    },

    _formatItems: function (items) {
        BI.each(items, function (idx, item) {
            BI.each(item, function (id, it) {
                BI.each(it.data, function (i, da) {
                    var data = da.z;
                    da.size = data.toFixed(2)
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
            right_y_axis_style: options.right_y_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            right_y_axis_number_level: options.right_y_axis_number_level || c.NORMAL,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            right_y_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            cordon: options.cordon || [],
            tooltip: options.tooltip || [],
            bubble_style: options.bubble_style || c.NO_PROJECT,
            big_data_mode: options.big_data_mode || false,
            bubble_min_size: options.bubble_min_size || c.BUBBLE_MIN_SIZE,
            bubble_max_size: options.bubble_max_size || c.BUBBLE_MAX_SIZE,
            rules_display: options.rules_display || c.RULE_DISPLAY,
            fixed_colors: options.fixed_colors || [],
            gradient_colors: options.gradient_colors || [],
            custom_y_scale: options.custom_y_scale || c.CUSTOM_SCALE,
            custom_x_scale: options.custom_x_scale || c.CUSTOM_SCALE,
            show_label: BI.isNull(options.show_label) ? true : options.show_label,
            enable_tick: BI.isNull(options.enable_tick) ? true : options.enable_tick,
            enable_minor_tick: BI.isNull(options.enable_minor_tick) ? true : options.enable_minor_tick,
            num_separators: options.num_separators || false,
            right_num_separators: options.right_num_separators || false,
            chart_font: options.chart_font || c.FONT_STYLE,
            show_left_label: BI.isNull(options.show_left_label) ? true : options.show_left_label,
            left_label_style: options.left_label_style ||  c.LEFT_LABEL_STYLE,
            left_line_color: options.left_line_color || "",
            show_right_label: BI.isNull(options.show_right_label) ? true : options.show_right_label,
            right_label_style: options.right_label_style ||  c.RIGHT_LABEL_STYLE,
            right_line_color: options.right_line_color || "",
            chart_legend_setting: options.chart_legend_setting || {},
            show_h_grid_line: BI.isNull(options.show_h_grid_line) ? true : options.show_h_grid_line,
            h_grid_line_color: options.h_grid_line_color || "",
            show_v_grid_line: BI.isNull(options.show_v_grid_line) ? true : options.show_v_grid_line,
            v_grid_line_color: options.v_grid_line_color || "",
            tooltip_setting: options.tooltip_setting || {},
        };
        this.options.items = items;
        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.BUBBLE);
            });
            types.push(type);
        });
        this.combineChart.populate(this._formatItems(items), types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function () {
        this.combineChart.magnify();
    }
});
BI.BubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.bubble_chart', BI.BubbleChart);

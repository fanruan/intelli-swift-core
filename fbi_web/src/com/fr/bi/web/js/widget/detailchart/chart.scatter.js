/**
 * 图表控件
 * @class BI.ScatterChart
 * @extends BI.Widget
 */
BI.ScatterChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.ScatterChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scatter-chart"
        })
    },

    _init: function () {
        BI.ScatterChart.superclass._init.apply(this, arguments);
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
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.ScatterChart.EVENT_CHANGE, obj);
        });
    },


    _formatConfig: function (config, items) {
        var self = this;
        config.colors = this.config.chart_color;
        config.plotOptions.style = formatChartStyle();
        config.plotOptions.marker = {"symbol": "circle", "radius": 4.5, "enabled": true};
        formatCordon();
        this.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.plotOptions.dataLabels.formatter.identifier = "${X}${Y}";

        config.yAxis = this.yAxis;
        config.xAxis = this.xAxis;

        config.yAxis[0].formatter = self.formatTickInXYaxis(this.config.left_y_axis_style, this.config.left_y_axis_number_level, this.config.num_separators);
        formatNumberLevelInYaxis(this.config.left_y_axis_number_level);
        config.yAxis[0].title.text = getXYAxisUnit(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = this.config.show_left_y_axis_title === true ? this.config.left_y_axis_title + config.yAxis[0].title.text : config.yAxis[0].title.text;
        config.yAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.yAxis[0].title.rotation = this.constants.ROTATION;
        config.yAxis[0].maxWidth = '40%';

        config.xAxis[0].formatter = self.formatTickInXYaxis(this.config.x_axis_style, this.config.x_axis_number_level, this.config.right_num_separators);
        formatNumberLevelInXaxis(this.config.x_axis_number_level);
        config.xAxis[0].title.text = getXYAxisUnit(this.config.x_axis_number_level, this.constants.X_AXIS);
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? this.config.x_axis_title + config.xAxis[0].title.text : config.xAxis[0].title.text;
        config.xAxis[0].title.align = "center";
        config.xAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.xAxis[0].maxHeight = '40%';
        config.chartType = "scatter";

        if (BI.isNotEmptyArray(this.config.tooltip)) {
            config.plotOptions.tooltip.formatter = function () {
                var y = self.formatTickInXYaxis(self.config.left_y_axis_style, self.config.left_y_axis_number_level, self.config.num_separators)(this.y);
                var x = self.formatTickInXYaxis(self.config.x_axis_style, self.config.x_axis_number_level, self.config.right_num_separators)(this.x);
                return this.seriesName + '<div>(X)' + self.config.tooltip[0]
                    + ':' + x + '</div><div>(Y)' + self.config.tooltip[1] + ':' + y + '</div>'
            };
        }

        if (config.plotOptions.dataLabels.enabled === true) {
            BI.each(items, function (idx, item) {
                item.dataLabels = {
                    "style": self.config.chart_font,
                    "align": "outside",
                    "autoAdjust": true,
                    enabled: true,
                    formatter: {
                        identifier: "${X}${Y}",
                        "XFormat": function () {
                            return BI.contentFormat(arguments[0], '#.##;-#.##')
                        },
                        "YFormat": function () {
                            return BI.contentFormat(arguments[0], '#.##;-#.##')
                        }
                    }
                };
                item.dataLabels.formatter.XFormat = config.xAxis[0].formatter;
                item.dataLabels.formatter.YFormat = config.yAxis[0].formatter;
            });
        }

        //全局样式图表文字
        config.legend.style = this.config.chart_font;
        config.yAxis[0].title.style = config.yAxis[0].labelStyle = this.config.chart_font;
        config.xAxis[0].title.style = config.xAxis[0].labelStyle = this.config.chart_font;

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

        function formatCordon() {
            BI.each(self.config.cordon, function (idx, cor) {
                if (idx === 0 && self.xAxis.length > 0) {
                    var magnify = self.calcMagnify(self.config.x_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": self.config.chart_font,
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
                                "style": self.config.chart_font,
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }

        function formatNumberLevelInXaxis(type) {
            var magnify = self.calcMagnify(type);
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                    da.x = self.formatXYDataWithMagnify(da.x, magnify);
                })
            })
        }

        function formatNumberLevelInYaxis(type) {
            var magnify = self.calcMagnify(type);
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                        da.y = self.formatXYDataWithMagnify(da.y, magnify);
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
            if (position === self.constants.X_AXIS) {
                self.config.x_axis_unit !== "" && (unit = unit + self.config.x_axis_unit)
            }
            if (position === self.constants.LEFT_AXIS) {
                self.config.left_y_axis_unit !== "" && (unit = unit + self.config.left_y_axis_unit)
            }
            if (position === self.constants.RIGHT_AXIS) {
                self.config.right_y_axis_unit !== "" && (unit = unit + self.config.right_y_axis_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
        }
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
            tooltip: options.tooltip || [],
            num_separators: options.num_separators || false,
            right_num_separators: options.right_num_separators || false,
            chart_font: options.chart_font || c.FONT_STYLE
        };
        this.options.items = items;
        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.SCATTER);
            });
            types.push(type);
        });
        this.combineChart.populate(items, types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function () {
        this.combineChart.magnify();
    }
});
BI.ScatterChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.scatter_chart', BI.ScatterChart);
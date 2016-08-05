/**
 * 图表控件
 * @class BI.AccumulateAreaChart
 * @extends BI.Widget
 */
BI.AccumulateAreaChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateAreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-area-chart"
        })
    },

    _init: function () {
        BI.AccumulateAreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.xAxis = [{
            type: "category",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE
        }];
        this.yAxis = [];

        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateAreaChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function (config, items) {
        var self = this, o = this.options;

        config.colors = this.config.chart_color;
        config.style = formatChartStyle(this.config.chart_style);
        formatChartLineStyle(this.config.chart_line_type);
        formatCordon(this.config.cordon);
        this.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.dataSheet.enabled = this.config.show_data_table;
        config.xAxis[0].showLabel = !config.dataSheet.enabled;
        config.zoom.zoomTool.visible = this.config.show_zoom;
        if (this.config.show_zoom === true) {
            delete config.dataSheet;
            delete config.zoom.zoomType;
        }

        config.yAxis = this.yAxis;

        BI.each(config.yAxis, function (idx, axis) {
            var unit = "";
            switch (axis.axisIndex) {
                case self.constants.LEFT_AXIS:
                    axis.reversed = self.config.left_y_axis_reversed;
                    unit = self.getXYAxisUnit(self.config.left_y_axis_number_level, self.config.left_y_axis_unit);
                    axis.title.text = self.config.show_left_y_axis_title === true ? self.config.left_y_axis_title + unit : unit;
                    axis.title.rotation = self.constants.ROTATION;
                    axis.gridLineWidth = self.config.show_grid_line === true ? 1 : 0;
                    axis.formatter = self.formatTickInXYaxis(self.config.left_y_axis_style, self.config.left_y_axis_number_level);
                    self.formatNumberLevelInYaxis(config, items, self.config.left_y_axis_number_level, idx, axis.formatter);

                    break;
                case self.constants.RIGHT_AXIS:
                    axis.reversed = self.config.right_y_axis_reversed;
                    unit = self.getXYAxisUnit(self.config.right_y_axis_number_level, self.config.right_y_axis_unit);
                    axis.title.text = self.config.show_right_y_axis_title === true ? self.config.right_y_axis_title + unit : unit;
                    axis.title.rotation = self.constants.ROTATION;
                    axis.gridLineWidth = self.config.show_grid_line === true ? 1 : 0;
                    axis.formatter = self.formatTickInXYaxis(self.config.right_y_axis_style, self.config.right_y_axis_number_level);
                    self.formatNumberLevelInYaxis(config, items, self.config.right_y_axis_number_level, idx, axis.formatter);
                    break;
            }
        });

        config.xAxis[0].labelRotation = this.config.text_direction;
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? config.xAxis[0].title.text : "";
        config.xAxis[0].title.align = "center";
        config.xAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;

        //为了给数据标签加个%,还要遍历所有的系列，唉
        if (config.plotOptions.dataLabels.enabled === true) {
            BI.each(items, function (idx, item) {
                var isNeedFormatDataLabel = false;
                switch (config.yAxis[item.yAxis].axisIndex) {
                    case self.constants.LEFT_AXIS:
                        if (self.config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                            isNeedFormatDataLabel = true;
                        }
                        break;
                    case self.constants.RIGHT_AXIS:
                        if (self.config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                            isNeedFormatDataLabel = true;
                        }
                        break;
                }
                if (isNeedFormatDataLabel === true) {
                    item.dataLabels = {
                        "style": self.constants.FONT_STYLE,
                        "align": "outside",
                        enabled: true,
                        formatter: {
                            identifier: "${VALUE}",
                            valueFormat: config.yAxis[item.yAxis].formatter
                        }
                    };
                }
            });
        }
        return [items, config];

        function formatChartStyle(v) {
            switch (v) {
                case BICst.CHART_STYLE.STYLE_GRADUAL:
                    return "gradual";
                case BICst.CHART_STYLE.STYLE_NORMAL:
                default:
                    return "normal";
            }
        }

        function formatChartLineStyle(v) {
            switch (v) {
                case BICst.CHART_SHAPE.RIGHT_ANGLE:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = true;
                    break;
                case BICst.CHART_SHAPE.CURVE:
                    config.plotOptions.curve = true;
                    config.plotOptions.step = false;
                    break;
                case BICst.CHART_SHAPE.NORMAL:
                default:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = false;
                    break;
            }
        }

        function formatCordon(cordon) {
            BI.each(cordon, function (idx, cor) {
                if (idx === 0 && self.xAxis.length > 0) {
                    var magnify = self.calcMagnify(self.config.x_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": self.constants.FONT_STYLE,
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
                                "style": self.constants.FONT_STYLE,
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }
    },

    _formatItems: function (items) {
        return BI.map(items, function (idx, item) {
            var i = BI.UUID();
            return BI.map(item, function (id, it) {
                return BI.extend({}, it, {stack: i});
            });
        });
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            right_y_axis_title: options.right_y_axis_title || "",
            chart_color: options.chart_color || [],
            chart_style: options.chart_style || c.STYLE_NORMAL,
            chart_line_type: options.chart_line_type || c.NORMAL,
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            right_y_axis_style: options.right_y_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            show_right_y_axis_title: options.show_right_y_axis_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
            right_y_axis_reversed: options.right_y_axis_reversed || false,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            right_y_axis_number_level: options.right_y_axis_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            right_y_axis_unit: options.right_y_axis_unit || "",
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
        this.yAxis = [];
        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.AREA);
            });
            types.push(type);
        });
        BI.each(types, function (idx, type) {
            if (BI.isEmptyArray(type)) {
                return;
            }
            var newYAxis = {
                type: "value",
                title: {
                    style: self.constants.FONT_STYLE
                },
                labelStyle: self.constants.FONT_STYLE,
                position: idx > 0 ? "right" : "left",
                lineWidth: 1,
                axisIndex: idx,
                gridLineWidth: 0
            };
            self.yAxis.push(newYAxis);
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
BI.AccumulateAreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_area_chart', BI.AccumulateAreaChart);
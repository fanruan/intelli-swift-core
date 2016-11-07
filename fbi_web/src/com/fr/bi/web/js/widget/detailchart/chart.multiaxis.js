/**
 * 图表控件
 * @class BI.MultiAxisChart
 * @extends BI.Widget
 * leftYxis 左值轴属性
 * rightYxis 右值轴属性
 * xAxis    分类轴属性
 */
BI.MultiAxisChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.MultiAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-axis-chart"
        })
    },

    _init: function () {
        BI.MultiAxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.xAxis = [{
            type: "category",
            title: {
                style: {"fontFamily": "inherit", "color": "#808080", "fontSize": "12px", "fontWeight": ""}
            },
            labelStyle: {
                "fontFamily": "inherit", "color": "#808080", "fontSize": "12px"
            },
            position: "bottom",
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
            self.fireEvent(BI.MultiAxisChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this, o = this.options;
        config.colors = this.config.chart_color;
        config.plotOptions.style = this.formatChartStyle();
        this.formatCordon();
        this.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.dataSheet.enabled = this.config.show_data_table;
        config.xAxis[0].showLabel = !config.dataSheet.enabled;
        this.formatZoom(config, this.config.show_zoom);

        config.yAxis = this.yAxis;
        config.plotOptions.connectNulls = this.config.null_continue;
        BI.each(config.yAxis, function (idx, axis) {
            switch (axis.axisIndex) {
                case self.constants.LEFT_AXIS:
                    BI.extend(axis, self.leftAxisSetting(self.config));
                    self.formatNumberLevelInYaxis(config, items, self.config.left_y_axis_number_level, idx, axis.formatter);
                    break;
                case self.constants.RIGHT_AXIS:
                    BI.extend(axis, self.rightAxisSetting(self.config));
                    self.formatNumberLevelInYaxis(config, items, self.config.right_y_axis_number_level, idx, axis.formatter);
                    break;
                case self.constants.RIGHT_AXIS_SECOND:
                    BI.extend(axis, self.right2AxisSetting(self.config));
                    self.formatNumberLevelInYaxis(config, items, self.config.right_y_axis_second_number_level, idx, axis.formatter);
                    break;
                default:
                    break;
            }
        });

        BI.extend(config.xAxis[0], self.catSetting(this.config));

        config.legend.style = BI.extend(this.config.chart_legend_setting, {
            fontSize: this.config.chart_legend_setting.fontSize + "px"
        });

        var lineItem = [];
        var otherItem = [];
        BI.each(items, function (idx, item) {
            item.color = [config.yAxis[idx].labelStyle.color];
            if (item.type === "line") {
                lineItem.push(item);
            } else {
                otherItem.push(item);
            }
        });

        //为了给数据标签加个%,还要遍历所有的系列，唉
        this.formatDataLabel(config.plotOptions.dataLabels.enabled, items, config, this.config.chart_font);

        //全局样式的图表文字
        if (config.dataSheet) {
            config.dataSheet.style = this.config.chart_font;
        }
        config.xAxis[0].title.style = this.config.chart_font;
        config.plotOptions.dataLabels.style = this.config.chart_font;
        BI.each(config.yAxis, function (idx, axis) {
            axis.title.style = self.config.chart_font;
        });

        return [BI.concat(otherItem, lineItem), config];
    },

    formatChartStyle: function () {
        switch (this.config.chart_style) {
            case BICst.CHART_STYLE.STYLE_GRADUAL:
                return "gradual";
            case BICst.CHART_STYLE.STYLE_NORMAL:
            default:
                return "normal";
        }
    },

    formatCordon: function () {
        var self = this;
        var magnify = 1;
        BI.each(this.config.cordon, function (idx, cor) {
            if (idx === 0 && self.xAxis.length > 0) {
                magnify = self.calcMagnify(self.config.x_axis_number_level);
                self.xAxis[0].plotLines = BI.map(cor, function (i, t) {
                    return BI.extend(t, {
                        value: t.value.div(magnify),
                        width: 1,
                        label: {
                            "style": {
                                "fontFamily": "inherit",
                                "color": "#808080",
                                "fontSize": "12px",
                                "fontWeight": ""
                            },
                            "text": t.text,
                            "align": "top"
                        }
                    });
                });
            }
            if (idx > 0 && self.yAxis.length >= idx) {
                magnify = 1;
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
                    default:
                        break;
                }
                self.yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                    return BI.extend(t, {
                        value: t.value.div(magnify),
                        width: 1,
                        label: {
                            "style": {
                                "fontFamily": "inherit",
                                "color": "#808080",
                                "fontSize": "12px",
                                "fontWeight": ""
                            },
                            "text": t.text,
                            "align": "left"
                        }
                    });
                });
            }
        })
    },

    getXYAxisUnit: function (numberLevelType, position) {
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
            default:
                break;
        }
        if (position === this.constants.X_AXIS) {
            this.config.x_axis_unit !== "" && (unit = unit + this.config.x_axis_unit)
        }
        if (position === this.constants.LEFT_AXIS) {
            this.config.left_y_axis_unit !== "" && (unit = unit + this.config.left_y_axis_unit)
        }
        if (position === this.constants.RIGHT_AXIS) {
            this.config.right_y_axis_unit !== "" && (unit = unit + this.config.right_y_axis_unit)
        }
        if (position === this.constants.RIGHT_AXIS_SECOND) {
            this.config.right_y_axis_second_unit !== "" && (unit = unit + this.config.right_y_axis_second_unit)
        }
        return unit === "" ? unit : "(" + unit + ")";
    },

    populate: function (items, options, types) {
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            right_y_axis_title: options.right_y_axis_title || "",
            right_y_axis_second_title: options.right_y_axis_second_title || "",
            chart_color: options.chart_color || ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"],
            chart_style: options.chart_style || c.NORMAL,
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            right_y_axis_style: options.right_y_axis_style || c.NORMAL,
            right_y_axis_second_style: options.right_y_axis_second_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            show_right_y_axis_title: options.show_right_y_axis_title || false,
            show_right_y_axis_second_title: options.show_right_y_axis_second_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
            right_y_axis_reversed: options.right_y_axis_reversed || false,
            right_y_axis_second_reversed: options.right_y_axis_second_reversed || false,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            right_y_axis_number_level: options.right_y_axis_number_level || c.NORMAL,
            right_y_axis_second_number_level: options.right_y_axis_second_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            right_y_axis_unit: options.right_y_axis_unit || "",
            right_y_axis_second_unit: options.right_y_axis_second_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_data_table: options.show_data_table || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            show_zoom: options.show_zoom || false,
            text_direction: options.text_direction || 0,
            cordon: options.cordon || [],
            line_width: BI.isNull(options.line_width) ? 1 : options.line_width,
            show_label: BI.isNull(options.show_label) ? true : options.show_label,
            enable_tick: BI.isNull(options.enable_tick) ? true : options.enable_tick,
            enable_minor_tick: BI.isNull(options.enable_minor_tick) ? true : options.enable_minor_tick,
            custom_y_scale: options.custom_y_scale || c.CUSTOM_SCALE,
            custom_x_scale: options.custom_x_scale || c.CUSTOM_SCALE,
            custom_z_scale: options.custom_z_scale || c.CUSTOM_SCALE,
            num_separators: options.num_separators || false,
            right_num_separators: options.right_num_separators || false,
            right2_num_separators: options.right2_num_separators || false,
            chart_font: options.chart_font || c.FONT_STYLE,
            show_left_label: BI.isNull(options.show_left_label) ? true : options.show_left_label,
            left_label_style: options.left_label_style || c.LEFT_LABEL_STYLE,
            left_line_color: options.left_line_color || "",
            show_right_label: BI.isNull(options.show_right_label) ? true : options.show_right_label,
            right_label_style: options.right_label_style || c.RIGHT_LABEL_STYLE,
            right_line_color: options.right_line_color || "",
            show_right2_label: BI.isNull(options.show_right2_label) ? true : options.show_right2_label,
            right2_label_style: options.right2_label_style || c.RIGHT2_LABEL_STYLE,
            right2_line_color: options.right2_line_color || "",
            show_cat_label: BI.isNull(options.show_cat_label) ? true : options.show_cat_label,
            cat_label_style: options.cat_label_style || c.CAT_LABEL_STYLE,
            cat_line_color: options.cat_line_color || "",
            chart_legend_setting: options.chart_legend_setting || {},
            show_h_grid_line: BI.isNull(options.show_h_grid_line) ? true : options.show_h_grid_line,
            h_grid_line_color: options.h_grid_line_color || "",
            show_v_grid_line: BI.isNull(options.show_v_grid_line) ? true : options.show_v_grid_line,
            v_grid_line_color: options.v_grid_line_color || "",
            tooltip_setting: options.tooltip_setting || {},
            null_continue: options.null_continue,
            left_title_style: options.left_title_style || {},
            right_title_style: options.right_title_style || {},
            right2_title_style: options.right2_title_style || {},
            cat_title_style: options.cat_title_style || {}
        };
        this.options.items = items;

        this.yAxis = [];
        BI.each(types, function (idx, type) {
            if (BI.isEmptyArray(type)) {
                return;
            }
            var newYAxis = {
                type: "value",
                title: {
                    style: {"fontFamily": "inherit", "color": "#808080", "fontSize": "12px", "fontWeight": ""}
                },
                labelStyle: {
                    "fontFamily": "inherit", "color": "#808080", "fontSize": "12px"
                },
                position: idx > 0 ? "right" : "left",
                lineWidth: 1,
                axisIndex: idx,
                gridLineWidth: 0
            };
            self.yAxis.push(newYAxis);
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
BI.MultiAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.multi_axis_chart', BI.MultiAxisChart);

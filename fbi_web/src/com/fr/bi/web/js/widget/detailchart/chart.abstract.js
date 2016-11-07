/**
 * 图表控件
 * @class BI.AbstractChart
 * @extends BI.Widget
 */
BI.AbstractChart = BI.inherit(BI.Widget, {

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
        MINLIMIT: 1e-5,
        LEGEND_HEIGHT: 80,
        LEGEND_WIDTH: "30.0%",
        FIX_COUNT: 6,
        STYLE_NORMAL: 21,
        NO_PROJECT: 16,
        DASHBOARD_AXIS: 4,
        ONE_POINTER: 1,
        MULTI_POINTER: 2,
        HALF_DASHBOARD: 9,
        PERCENT_DASHBOARD: 10,
        PERCENT_SCALE_SLOT: 11,
        VERTICAL_TUBE: 12,
        HORIZONTAL_TUBE: 13,
        LNG_FIRST: 3,
        LAT_FIRST: 4,
        theme_color: "#65bce7",
        auto_custom: 1,
        POLYGON: 7,
        AUTO_CUSTOM: 1,
        AUTO: 1,
        NOT_SHOW: 2,
        LINE_WIDTH: 1,
        BUBBLE_MIN_SIZE: 15,
        BUBBLE_MAX_SIZE: 80,
        RULE_DISPLAY: 1,
        NUM_SEPARATORS: false,
        FONT_STYLE: {
            "fontFamily": "inherit",
            "color": "inherit",
            "fontSize": "12px"
        },
        CUSTOM_SCALE: {
            maxScale: {
                scale: null
            },
            minScale: {
                scale: null
            },
            interval: {
                scale: null
            }
        },
        LEFT_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        },
        RIGHT_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        },
        RIGHT2_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        },
        CAT_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        }
    },

    _defaultConfig: function () {
        return BI.extend(BI.AbstractChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-abstract-chart",
            popupItemsGetter: BI.emptyFn
        })
    },

    _init: function () {
        BI.AbstractChart.superclass._init.apply(this, arguments);
    },

    formatZoom: function (config, show_zoom) {
        config.zoom.zoomTool.enabled = this.config.show_zoom;
        if (show_zoom === true) {
            delete config.dataSheet;
            config.zoom.zoomType = "";
        }
    },

    /**
     * 格式化坐标轴数量级及其所影响的系列的各项属性
     * @param config  配置信息
     * @param items  系列数据
     * @param type  坐标轴数量级
     * @param position 坐标轴位置
     * @param formatter 系列tooltip格式化内容
     */
    formatNumberLevelInYaxis: function (config, items, type, position, formatter) {
        var magnify = this.calcMagnify(type);
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                if (position === item.yAxis) {
                    if (BI.isNotNull(da.y) && !BI.isNumber(da.y)) {
                        da.y = BI.parseFloat(da.y);
                    }
                    if (BI.isNotNull(da.y)) {
                        da.y = BI.contentFormat(BI.parseFloat(da.y.div(magnify).toFixed(4)), "#.####;-#.####");
                    }
                }
            });
            if (position === item.yAxis) {
                item.tooltip = BI.deepClone(config.plotOptions.tooltip);
                item.tooltip.formatter.valueFormat = formatter;
            }
        });
    },

    formatNumberLevelInXaxis: function (items, type) {
        var magnify = this.calcMagnify(type);
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                if (BI.isNotNull(da.x) && !BI.isNumber(da.x)) {
                    da.x = BI.parseFloat(da.x);
                }
                if (BI.isNotNull(da.x)) {
                    da.x = BI.contentFormat(BI.parseFloat(da.x.div(magnify).toFixed(4)), "#.####;-#.####");
                }
            });
        })
    },

    formatXYDataWithMagnify: function (number, magnify) {
        if (BI.isNull(number)) {
            return null
        }
        if (!BI.isNumber(number)) {
            number = BI.parseFloat(number);
        }
        if (BI.isNotNull(number)) {
            return BI.contentFormat(BI.parseFloat(number.div(magnify).toFixed(4)), "#.####;-#.####");
        }
    },

    formatToolTipAndDataLabel: function (type, numberLevel, unit, separators) {
        var formatter = this.formatNumberLevelAndSeparators(type, separators);
        formatter += this.getXYAxisUnit(numberLevel, unit);

        return formatter;
    },

    calcMagnify: function (type) {
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
    },

    formatChartLegend: function (config, chart_legend) {
        switch (chart_legend) {
            case BICst.CHART_LEGENDS.BOTTOM:
                config.legend.enabled = true;
                config.legend.position = "bottom";
                config.legend.maxHeight = this.constants.LEGEND_HEIGHT;
                break;
            case BICst.CHART_LEGENDS.RIGHT:
                config.legend.enabled = true;
                config.legend.position = "right";
                config.legend.maxWidth = this.constants.LEGEND_WIDTH;
                break;
            case BICst.CHART_LEGENDS.NOT_SHOW:
            default:
                config.legend.enabled = false;
                break;
        }
    },

    getXYAxisUnit: function (numberLevelType, axis_unit) {
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
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                unit += '%';
                break;
        }
        return (BI.isEmptyString(unit) && BI.isEmptyString(axis_unit)) ? unit : (unit + axis_unit);
    },

    formatTickInXYaxis: function (type, number_level, separators) {
        var formatter = this.formatNumberLevelAndSeparators(type, separators);
        if (number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
            formatter += '%';
        }
        formatter += ";-" + formatter;
        return function () {
            return BI.contentFormat(arguments[0], formatter)
        }
    },

    formatTickForRadar: function (type, number_level, separators, unit) {
        var formatter = this.formatNumberLevelAndSeparators(type, separators);
        formatter += this.getXYAxisUnit(number_level, unit);
        formatter += ";-" + formatter;
        return function () {
            return BI.contentFormat(arguments[0], formatter)
        }
    },

    formatNumberLevelAndSeparators: function (type, separators) {
        var formatter;
        switch (type) {
            case this.constants.NORMAL:
                formatter = '#.##';
                if (separators) formatter = '#,###.##';
                break;
            case this.constants.ZERO2POINT:
                formatter = '#0';
                if (separators) formatter = '#,###';
                break;
            case this.constants.ONE2POINT:
                formatter = '#0.0';
                if (separators) formatter = '#,###.0';
                break;
            case this.constants.TWO2POINT:
                formatter = '#0.00';
                if (separators) formatter = '#,###.00';
                break;
        }
        return formatter
    },

    formatDataLabel: function (state, items, config, style) {
        if (state === true) {
            BI.each(items, function (idx, item) {
                item.dataLabels = {
                    "align": "outside",
                    "autoAdjust": true,
                    style: style,
                    enabled: true,
                    formatter: {
                        identifier: "${VALUE}",
                        valueFormat: config.yAxis[item.yAxis].formatter
                    }
                };
            });
        }
    },

    formatDataLabelForAxis: function (state, items, format, style) {
        if (state === true) {
            BI.each(items, function (idx, item) {
                item.dataLabels = {
                    "align": "outside",
                    "autoAdjust": true,
                    style: style,
                    enabled: true,
                    formatter: {
                        identifier: "${VALUE}",
                        valueFormat: format
                    }
                };
            });
        }
    },

    catSetting: function (config) {
        return BI.extend({
            enableTick: config.enable_tick,
            lineWidth: config.line_width,
            lineColor: config.cat_line_color,
            gridLineColor: config.v_grid_line_color,
            gridLineWidth: config.show_v_grid_line === true ? 1 : 0,
            showLabel: config.show_cat_label,
            labelRotation: config.cat_label_style.text_direction,
            labelStyle: BI.extend(config.cat_label_style.text_style, {
                fontSize: config.cat_label_style.text_style.fontSize + "px"
            }),
        }, {
            title: {
                align: "center",
                text: config.show_x_axis_title ? config.x_axis_title : "",
                style:  BI.extend(config.cat_title_style, {
                    fontSize: config.cat_title_style.fontSize + "px"
                })
            }
        })
    },

    leftAxisSetting: function (config) {
        var title = this.getXYAxisUnit(config.left_y_axis_number_level, config.left_y_axis_unit);
        return BI.extend({
            lineWidth: config.line_width,
            lineColor: config.left_line_color,
            tickColor: config.left_line_color,
            gridLineWidth: config.show_h_grid_line === true ? 1 : 0,
            gridLineColor: config.h_grid_line_color,
            showLabel: config.show_left_label,
            labelStyle: BI.extend(config.left_label_style.text_style, {
                fontSize: config.left_label_style.text_style.fontSize + "px"
            }),
            labelRotation: config.left_label_style.text_direction,
            enableTick: config.enable_tick,
            reversed: config.left_y_axis_reversed,
            enableMinorTick: config.enable_minor_tick,
            min: config.custom_y_scale.minScale.scale || null,
            max: config.custom_y_scale.maxScale.scale || null,
            tickInterval: BI.isNumber(config.custom_y_scale.interval.scale) && config.custom_y_scale.interval.scale > 0 ?
                config.custom_y_scale.interval.scale : null,
            formatter: this.formatTickInXYaxis(config.left_y_axis_style, config.left_y_axis_number_level, config.num_separators)
        }, {
            title: {
                text: config.show_left_y_axis_title ? config.left_y_axis_title + title : title,
                rotation: this.constants.ROTATION,
                style: BI.extend(config.left_title_style, {
                    fontSize: config.left_title_style.fontSize + "px"
                })
            }
        })
    },

    rightAxisSetting: function (config) {
        var unit = this.getXYAxisUnit(config.right_y_axis_number_level, config.right_y_axis_unit);
        return BI.extend({
            lineWidth: config.line_width,
            lineColor: config.right_line_color,
            tickColor: config.right_line_color,
            gridLineWidth: config.show_h_grid_line === true ? 1 : 0,
            gridLineColor: config.h_grid_line_color,
            showLabel: config.show_right_label,
            labelStyle: BI.extend(config.right_label_style.text_style, {
                fontSize: config.right_label_style.text_style.fontSize + "px"
            }),
            labelRotation: config.right_label_style.text_direction,
            reversed: config.right_y_axis_reversed,
            enableTick: config.enable_tick,
            enableMinorTick: config.enable_minor_tick,
            min: config.custom_x_scale.minScale.scale || null,
            max: config.custom_x_scale.maxScale.scale || null,
            tickInterval: BI.isNumber(config.custom_x_scale.interval.scale) && config.custom_x_scale.interval.scale > 0 ?
                config.custom_x_scale.interval.scale : null,
            formatter: this.formatTickInXYaxis(config.right_y_axis_style, config.right_y_axis_number_level, config.right_num_separators)
        }, {
            title: {
                text: config.show_right_y_axis_title ? config.right_y_axis_title + unit : unit,
                rotation: this.constants.ROTATION,
                style: BI.extend(config.right_title_style, {
                    fontSize: config.right_title_style.fontSize + "px"
                })
            }
        })
    },

    right2AxisSetting: function (config) {
        return BI.extend({
            lineWidth: config.line_width,
            lineColor: config.right2_line_color,
            tickColor: config.right2_line_color,
            gridLineWidth: config.show_h_grid_line === true ? 1 : 0,
            gridLineColor: config.h_grid_line_color,
            showLabel: config.show_right2_label,
            labelStyle: BI.extend(config.right2_label_style.text_style, {
                fontSize: config.right2_label_style.text_style.fontSize + "px"
            }),
            labelRotation: config.right2_label_style.text_direction,
            reversed: config.right_y_axis_second_reversed,
            enableTick: config.enable_tick,
            enableMinorTick: config.enable_minor_tick,
            min: config.custom_z_scale.minScale.scale || null,
            max: config.custom_z_scale.maxScale.scale || null,
            tickInterval: BI.isNumber(config.custom_z_scale.interval.scale) && config.custom_z_scale.interval.scale > 0 ?
                config.custom_z_scale.interval.scale : null,
            formatter: this.formatTickInXYaxis(config.right_y_axis_second_style, config.right_y_axis_second_number_level, config.right2_num_separators)
        }, {
            title: {

            }
        })
    },

    setFontStyle: function (fontStyle, config) {
        if (config.dataSheet) {
            config.dataSheet.style = fontStyle;
        }
    },

    _formatItems: function (items) {
        return items;
    },

    populate: function (items, options) {
    },

    resize: function () {
    },

    magnify: function () {
    }
});

BI.AbstractChart.EVENT_CHANGE = "EVENT_CHANGE";
BI.AbstractChart.EVENT_ITEM_CLICK = "EVENT_ITEM_CLICK";

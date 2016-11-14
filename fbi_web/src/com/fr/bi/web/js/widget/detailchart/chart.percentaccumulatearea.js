/**
 * 图表控件
 * @class BI.PercentAccumulateAreaChart
 * @extends BI.Widget
 */
BI.PercentAccumulateAreaChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.PercentAccumulateAreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-percent-accumulate-area-chart"
        })
    },

    _init: function () {
        BI.PercentAccumulateAreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.xAxis = [{
            type: "category",
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
            self.fireEvent(BI.PercentAccumulateAreaChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this;
        config.colors = this.config.chart_color;
        config.plotOptions.style = formatChartStyle();
        formatCordon();
        this.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.dataSheet.enabled = this.config.show_data_table;
        config.xAxis[0].showLabel = !config.dataSheet.enabled;
        this.formatZoom(config, this.config.show_zoom);

        config.yAxis = this.yAxis;
        BI.extend(config.yAxis[0], self.leftAxisSetting(self.config));
        self.formatNumberLevelInYaxis(config, items, this.config.left_y_axis_number_level, this.constants.LEFT_AXIS, config.yAxis[0].formatter);

        BI.extend(config.xAxis[0], self.catSetting(this.config));

        config.legend.style = BI.extend(this.config.chart_legend_setting, {
            fontSize: this.config.chart_legend_setting.fontSize + "px"
        });

        config.chartType = "area";
        config.plotOptions.tooltip.formatter.identifier = "${CATEGORY}${SERIES}${PERCENT}";

        //为了给数据标签加个%,还要遍历所有的系列，唉
        this.formatDataLabelForAxis(config.plotOptions.dataLabels.enabled, items, config.yAxis[0].formatter, this.config.chart_font);

        //全局样式的图表文字
        this.setFontStyle(this.config.chart_font, config);

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
    },

    _formatItems: function (items) {
        return BI.map(items, function (idx, item) {
            var i = BI.UUID();
            return BI.map(item, function (id, it) {
                return BI.extend({}, it, {stack: i, stackByPercent: true});
            });
        });
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            chart_color: options.chart_color || [],
            chart_style: options.chart_style || c.NORMAL,
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
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
            cordon: options.cordon || [],
            line_width: BI.isNull(options.line_width) ? 1 : options.line_width,
            show_label: BI.isNull(options.show_label) ? true : options.show_label,
            enable_tick: BI.isNull(options.enable_tick) ? true : options.enable_tick,
            enable_minor_tick: BI.isNull(options.enable_minor_tick) ? true : options.enable_minor_tick,
            custom_y_scale: options.custom_y_scale || c.CUSTOM_SCALE,
            num_separators: options.num_separators || false,
            chart_font: options.chart_font || c.FONT_STYLE,
            show_left_label: BI.isNull(options.show_left_label) ? true : options.show_left_label,
            left_label_style: options.left_label_style || c.LEFT_LABEL_STYLE,
            left_line_color: options.left_line_color || "",
            show_cat_label: BI.isNull(options.show_cat_label) ? true : options.show_cat_label,
            cat_label_style: options.cat_label_style || c.CAT_LABEL_STYLE,
            cat_line_color: options.cat_line_color || "",
            chart_legend_setting: options.chart_legend_setting || {},
            show_h_grid_line: BI.isNull(options.show_h_grid_line) ? true : options.show_h_grid_line,
            h_grid_line_color: options.h_grid_line_color || "",
            show_v_grid_line: BI.isNull(options.show_v_grid_line) ? true : options.show_v_grid_line,
            v_grid_line_color: options.v_grid_line_color || "",
            tooltip_setting: options.tooltip_setting || {},
            left_title_style: options.left_title_style || {},
            cat_title_style: options.cat_title_style || {}
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                self.defaultFormatDataLabel(item.data);
                type.push(BICst.WIDGET.AREA);
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
BI.PercentAccumulateAreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.percent_accumulate_area_chart', BI.PercentAccumulateAreaChart);

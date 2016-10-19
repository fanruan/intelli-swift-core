/**
 * 图表控件
 * @class BI.AccumulateRadarChart
 * @extends BI.Widget
 */
BI.AccumulateRadarChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateRadarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-radar-chart"
        })
    },

    _init: function () {
        BI.AccumulateRadarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.radiusAxis = [{
            type: "value",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE,
            formatter: function () {
                return this > 0 ? this : (-1) * this
            },
            gridLineWidth: 0,
            position: "bottom"
        }];

        this.angleAxis = [{
            type: "category",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE
        }];

        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateRadarChart.EVENT_CHANGE, obj);
        });
    },

    _formatItems: function (items) {
        return BI.map(items, function (idx, item) {
            var i = BI.UUID();
            return BI.map(item, function (id, it) {
                return BI.extend({}, it, {stack: i});
            });
        });
    },

    _formatConfig: function (config, items) {
        var self = this;
        var title = getXYAxisUnit(this.config.left_y_axis_number_level, self.constants.LEFT_AXIS);
        formatChartRadarStyle();
        config.colors = this.config.chart_color;
        self.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;

        config.radiusAxis = this.radiusAxis;
        config.angleAxis = this.angleAxis;
        config.radiusAxis[0].formatter = self.formatTickForRadar(this.config.left_y_axis_style, this.config.left_y_axis_number_level, this.config.num_separators, this.config.left_y_axis_unit);
        formatNumberLevelInYaxis(this.config.left_y_axis_number_level, self.constants.LEFT_AXIS, config.radiusAxis[0].formatter);
        config.radiusAxis[0].title.text = this.config.show_left_y_axis_title === true ? this.config.left_y_axis_title + title : title;
        config.radiusAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.radiusAxis[0].min = this.config.custom_y_scale.minScale.scale || null;
        config.radiusAxis[0].max = this.config.custom_y_scale.maxScale.scale || null;
        config.radiusAxis[0].tickInterval = BI.isNumber(self.config.custom_y_scale.interval.scale) && self.config.custom_y_scale.interval.scale > 0 ?
            self.config.custom_y_scale.interval.scale : null;
        config.chartType = "radar";
        config.plotOptions.columnType = true;
        delete config.xAxis;
        delete config.yAxis;

        config.angleAxis[0].labelStyle = BI.extend(this.config.lvalue_axis_label_setting.text_style, {
            fontSize: this.config.lvalue_axis_label_setting.text_style.fontSize + "px"
        });
        config.angleAxis[0].showLabel = this.config.show_lvalue_axis_label;
        config.angleAxis[0].lineColor = this.config.lvalue_axis_line_color;
        config.legend.style = BI.extend( this.config.chart_legend_setting, {
            fontSize:  this.config.chart_legend_setting.fontSize + "px"
        });

        //为了给数据标签加个%,还要遍历所有的系列，唉
        self.formatDataLabelForAxis(config.plotOptions.dataLabels.enabled, items, config.radiusAxis[0].formatter, this.config.chart_font, this.config.left_y_axis_unit);

        //全局样式的图表文字
        config.radiusAxis[0].labelStyle = config.radiusAxis[0].title.style = this.config.chart_font;

        return [items, config];

        function formatChartRadarStyle() {
            switch (self.config.chart_radar_type) {
                case BICst.CHART_SHAPE.POLYGON:
                    config.plotOptions.shape = "polygon";
                    break;
                case BICst.CHART_SHAPE.CIRCLE:
                    config.plotOptions.shape = "circle";
                    break;
            }
        }

        function formatNumberLevelInYaxis(type, position, formatter) {
            var magnify = self.calcMagnify(type);
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                    if (position === item.yAxis) {
                        da.y = self.formatXYDataWithMagnify(da.y, magnify);
                    }
                })
            });
            config.plotOptions.tooltip.formatter.valueFormat = formatter;
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
            chart_radar_type: options.chart_radar_type || c.NORMAL,
            chart_color: options.chart_color || [],
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            cordon: options.cordon || [],
            custom_y_scale: options.custom_y_scale || c.CUSTOM_SCALE,
            num_separators: options.num_separators || false,
            chart_font: options.chart_font || c.FONT_STYLE,
            left_y_axis_unit: options.left_y_axis_unit || "",
            show_lvalue_axis_label: BI.isNull(options.show_lvalue_axis_label) ? true : options.show_lvalue_axis_label,
            lvalue_axis_label_setting: options.lvalue_axis_label_setting || {},
            lvalue_axis_line_color: options.lvalue_axis_line_color || "",
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
                type.push(BICst.WIDGET.RADAR);
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
BI.AccumulateRadarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_radar_chart', BI.AccumulateRadarChart);

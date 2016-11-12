/**
 * 图表控件
 * @class BI.RangeAreaChart
 * @extends BI.Widget
 * 范围面积图的构造范围的两组item的必须有对应y值item1完全大于item2
 */
BI.RangeAreaChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.RangeAreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-range-area-chart"
        })
    },

    _init: function () {
        BI.RangeAreaChart.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.RangeAreaChart.EVENT_CHANGE, obj);
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
        config.plotOptions.connectNulls = this.config.null_continue;
        this.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;

        config.yAxis = this.yAxis;
        BI.extend(config.yAxis[0], self.leftAxisSetting(self.config));
        formatNumberLevelInYaxis(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS, config.yAxis[0].formatter);

        BI.extend(config.xAxis[0], self.catSetting(this.config));

        config.legend.style = BI.extend(this.config.chart_legend_setting, {
            fontSize: this.config.chart_legend_setting.fontSize + "px"
        });

        config.chartType = "area";
        config.plotOptions.tooltip.formatter.identifier = "${CATEGORY}${VALUE}";

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
    },

    _formatItems: function (data) {
        var o = this.options;
        var items = [];
        BI.each(data, function (idx, item) {
            items = BI.concat(items, item);
        });
        if (BI.isEmptyArray(items)) {
            return [];
        }
        if (items.length === 1) {
            return [items];
        }
        var colors = this.config.chart_color || [];
        if (BI.isEmptyArray(colors)) {
            colors = ["#5caae4"];
        }
        var seriesMinus = [];
        BI.each(items[0].data, function (idx, item) {
            var res = items[1].data[idx].y - item.y;
            seriesMinus.push({
                x: items[1].data[idx].x,
                y: res,
                targetIds: items[1].data[idx].targetIds
            });
        });
        items[1] = {
            data: seriesMinus,
            name: items[1].name,
            stack: "stackedArea",
            fillColor: colors[0]
        };
        BI.each(items, function (idx, item) {
            if (idx === 0) {
                BI.extend(item, {
                    name: items[0].name,
                    fillColorOpacity: 0,
                    stack: "stackedArea",
                    marker: {enabled: false},
                    fillColor: "#000000"
                });
            }
        });
        return [items];
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = self.getChartConfig(options);
        this.options.items = items;

        var types = [];
        var type = [];
        BI.each(items, function (idx, axisItems) {
            type.push(BICst.WIDGET.AREA);
        });
        if (BI.isNotEmptyArray(type)) {
            types.push(type);
        }

        this.combineChart.populate(this._formatItems(items), types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function () {
        this.combineChart.magnify();
    }
});
BI.RangeAreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.range_area_chart', BI.RangeAreaChart);

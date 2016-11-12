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
            item.color = [config.yAxis[idx].lineColor];
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
        config.plotOptions.dataLabels.style = this.config.chart_font;

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

    populate: function (items, options, types) {
        var self = this, c = this.constants;
        this.config = self.getChartConfig(options);
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

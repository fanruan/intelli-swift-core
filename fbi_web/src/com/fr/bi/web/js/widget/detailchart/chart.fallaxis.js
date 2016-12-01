/**
 * 图表控件
 * @class BI.FallAxisChart
 * @extends BI.Widget
 */
BI.FallAxisChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.FallAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fall-axis-chart"
        })
    },

    _init: function () {
        BI.FallAxisChart.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.FallAxisChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this, o = this.options;

        config.colors = this.config.chartColor;
        config.plotOptions.style = formatChartStyle();
        formatCordon();
        config.legend.enabled = false;
        config.plotOptions.dataLabels.enabled = this.config.showDataLabel;
        config.dataSheet.enabled = this.config.showDataTable;
        if (config.dataSheet.enabled === true) {
            config.xAxis[0].showLabel = false;
        }
        this.formatZoom(config, this.config.showZoom);

        config.yAxis = this.yAxis;
        BI.extend(config.xAxis[0], self.catSetting(this.config));
        formatNumberLevelInYaxis(this.config.leftYNumberLevel, this.constants.LEFT_AXIS, config.yAxis[0].formatter);

        BI.extend(config.yAxis[0], self.leftAxisSetting(self.config));

        config.legend.style = BI.extend(this.config.legendStyle, {
            fontSize: this.config.legendStyle.fontSize + "px"
        });

        if (config.plotOptions.dataLabels.enabled === true) {
            var self = this;
            BI.each(items, function (idx, item) {
                if (idx === 0) {
                    item.dataLabels = {};
                    return;
                }
                item.dataLabels = {
                    "style": self.config.dataLabelSetting.textStyle,
                    "align": self.setDataLabelPosition(self.config),
                    "autoAdjust": true,
                    enabled: true,
                    formatter: {
                        identifier: self.setDataLabelContent(self.config),
                        valueFormat: config.yAxis[0].formatter
                    }
                };
                self.formatDataLabelForEachData(item.data, config.yAxis[0].formatter, self.config);
            });
        }

        //全局样式的图表文字
        this.setFontStyle(this.config.chartFont, config);

        return [items, config];

        function formatChartStyle() {
            switch (self.config.chartStyle) {
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
                    var magnify = self.calcMagnify(1);
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
                            magnify = self.calcMagnify(self.config.leftYNumberLevel);
                            break;
                        case self.constants.RIGHT_AXIS:
                            magnify = self.calcMagnify(self.config.rightYNumberLevel);
                            break;
                        case self.constants.RIGHT_AXIS_SECOND:
                            magnify = self.calcMagnify(self.config.rightY2NumberLevel);
                            break;
                    }
                    self.yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": self.config.chartFont,
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

    _formatItems: function (items) {
        var o = this.options;
        if (BI.isEmptyArray(items)) {
            return [];
        }
        items = items[0];
        var tables = [], sum = 0;
        var colors = this.config.chartColor || [];
        if (BI.isEmptyArray(colors)) {
            colors = ["rgb(152, 118, 170)", "rgb(0, 157, 227)"];
        }
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (i, t) {
                if (t.y < 0) {
                    tables.push([t.x, t.y, sum + t.y, t.targetIds, t.dataLabels, t.image, t.imageWidth, t.imageHeight]);
                } else {
                    tables.push([t.x, t.y, sum, t.targetIds, t.dataLabels, t.image, t.imageWidth, t.imageHeight]);
                }
                sum += t.y;
            });
        });

        return [BI.map(BI.makeArray(2, null), function (idx, item) {
            return {
                "data": BI.map(tables, function (id, cell) {
                    var axis = {};
                    if (idx === 1) {
                        axis = BI.extend({
                            targetIds: cell[3],
                            dataLabels: cell[4],
                            image: cell[5],
                            imageWidth: cell[6],
                            imageHeight: cell[7]
                        }, {
                            x: cell[0],
                            y: Math.abs(cell[2 - idx])
                        });
                        axis.color = cell[2 - idx] < 0 ? colors[1] : colors[0];
                    } else {
                        axis = BI.extend({targetIds: cell[3]}, {
                            x: cell[0],
                            y: Math.abs(cell[2 - idx])
                        });
                        axis.color = "rgba(0,0,0,0)";
                        axis.borderColor = "rgba(0,0,0,0)";
                        axis.borderWidth = 0;
                        axis.clickColor = "rgba(0,0,0,0)";
                        axis.mouseOverColor = "rgba(0,0,0,0)";
                        axis.tooltip = {
                            enable: false
                        }
                    }
                    return axis;
                }),
                stack: "stackedFall",
                name: idx === 1 ? items[0].name : BI.UUID()
            };
        })];
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = self.getChartConfig(options);
        this.options.items = items;
        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.AXIS);
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
BI.FallAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.fall_axis_chart', BI.FallAxisChart);

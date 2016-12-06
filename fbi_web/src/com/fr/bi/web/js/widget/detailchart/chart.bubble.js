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
        var self = this, o = this.options;
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
        delete config.zoom;
        formatCordon();

        switch (this.config.displayRules) {
            case BICst.DISPLAY_RULES.FIXED:
                delete config.legend;
                formatFixedLegend();
                config.rangeLegend.style = BI.extend(this.config.legendStyle, {
                    fontSize: this.config.legendStyle.fontSize + "px"
                });
                break;
            case BICst.DISPLAY_RULES.GRADIENT:
                delete config.legend;
                formatGradientLegend();
                config.rangeLegend.style = BI.extend(this.config.legendStyle, {
                    fontSize: this.config.legendStyle.fontSize + "px"
                });
                break;
            case BICst.DISPLAY_RULES.DIMENSION:
            default:
                self.formatChartLegend(config, this.config.legend);
                config.legend.style = BI.extend(this.config.legendStyle, {
                    fontSize: this.config.legendStyle.fontSize + "px"
                });
                break;
        }

        config.colors = this.config.chartColor;
        config.style = formatChartStyle();
        config.plotOptions.bubble.minSize = this.config.bubbleSizeFrom;
        config.plotOptions.bubble.maxSize = this.config.bubbleSizeTo;
        config.plotOptions.dataLabels.formatter.identifier = setDataLabelContentForBubble();
        BI.extend(config.plotOptions.dataLabels, {
            align: self.setDataLabelPosition(this.config),
            enabled: this.config.showDataLabel,
            style: this.config.dataLabelSetting.textStyle,
        });
        config.yAxis = this.yAxis;

        formatNumberLevelInYaxis(this.config.leftYNumberLevel, c.LEFT_AXIS);

        BI.extend(config.yAxis[0], self.leftAxisSetting(this.config));

        self.formatNumberLevelInXaxis(items, this.config.rightYNumberLevel);

        BI.extend(config.xAxis[0], self.rightAxisSetting(this.config));
        config.xAxis[0].title.rotation = 0;
        config.xAxis[0].gridLineColor = this.config.vGridLineColor;

        config.chartType = "bubble";

        if (BI.isNotEmptyArray(this.config.tooltip)) {
            config.plotOptions.tooltip.formatter = function () {
                var y = self.formatTickInXYaxis(self.config.leftYNumberFormat, self.config.leftYNumberLevel, self.config.leftYSeparator)(this.y);
                var x = self.formatTickInXYaxis(self.config.rightYNumberFormat, self.config.rightYNumberLevel, self.config.rightYSeparator)(this.x);
                var size = BI.contentFormat(this.size,
                    self.formatToolTipAndDataLabel(items[0].settings.format || c.NORMAL, items[0].settings.numLevel || c.NORMAL,
                        items[0].settings.unit || "", items[0].settings.numSeparators || c.NUM_SEPARATORS));
                return this.seriesName + '<div>(X)' + self.config.tooltip[0] + ':' + x + '</div><div>(Y)' + self.config.tooltip[1]
                    + ':' + y + '</div><div>(' + BI.i18nText("BI-Size") + ')' + self.config.tooltip[2] + ':' + size + '</div>'
            };
        }

        if (config.plotOptions.dataLabels.enabled === true && !this.config.bigDataMode) {
            BI.each(items, function (idx, item) {
                item.dataLabels = {
                    align: self.setDataLabelPosition(self.config),
                    autoAdjust: true,
                    style: self.config.dataLabelSetting.textStyle,
                    enabled: true,
                    formatter: {
                        identifier: setDataLabelContentForBubble(),
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
                if (item.settings.numLevel) {
                    item.data[0].z = item.data[0].size = self.formatXYDataWithMagnify(item.data[0].z, self.calcMagnify(item.settings.numLevel))
                }
                item.dataLabels.formatter.XFormat = config.xAxis[0].formatter;
                item.dataLabels.formatter.YFormat = config.yAxis[0].formatter;
                item.dataLabels.formatter.sizeFormat = function () {
                    return BI.contentFormat(arguments[0],
                        self.formatToolTipAndDataLabel(item.settings.format || c.NORMAL, item.settings.numLevel || c.NORMAL,
                            item.settings.unit || "", item.settings.numSeparators || c.NUM_SEPARATORS));
                };
                self.formatDataLabelForEachData(item.data);
                BI.each(item.data, function (i, data) {
                    if (data.dataLabels && data.dataLabels.styleSetting && data.dataLabels.styleSetting.type === BICst.DATA_LABEL_STYLE_TYPE.TEXT) {
                        data.dataLabels.formatter = {};
                        data.dataLabels.formatter.XFormat = config.xAxis[0].formatter;
                        data.dataLabels.formatter.YFormat = config.yAxis[0].formatter;
                        data.dataLabels.formatter.sizeFormat = item.dataLabels.formatter.sizeFormat;
                    }
                });
                self._formatDataLabel(item.data);
            });
        }

        BI.extend(config.plotOptions, {
            large: this.config.bigDataMode,
            shadow: this.config.bubbleStyle !== c.NO_PROJECT
        });

        if(this.config.bigDataMode) {
            config.plotOptions.dataLabels.enabled = false;
            config.plotOptions.tooltip.enabled = false;
        }

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

            BI.each(self.config.fixedStyle, function (idx, item) {
                if (idx === 0 && min < item.range.min) {
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

                if (idx == (self.config.fixedStyle.length - 1) && max > item.range.max) {
                    range.push({
                        from: item.range.max,
                        to: max,
                        color: item.color
                    })
                }
            });

            config.rangeLegend.range = range;

            switch (self.config.legend) {
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

        function setDataLabelContentForBubble () {
            var setting = self.config.dataLabelSetting, identifier = '';
            if(setting.showSeriesName) {
                identifier += '${SERIES}'
            }
            if(setting.showValue) {
                identifier += '${X}${Y}${SIZE}'
            }
            return identifier
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

            BI.each(self.config.gradientStyle, function (idx, item) {
                var minProp = (item.range.min - min) / (max - min);
                var maxProp = (item.range.max - min) / (max - min);

                if (idx === 0 && minProp > 0) {
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

            switch (self.config.legend) {
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

            if (err <= .15) {
                step *= 10;
            } else if (err <= .35) {
                step *= 5;
            } else if (err <= .75) {
                step *= 2;
            }

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
                    var magnify = self.calcMagnify(1);
                    self.xAxis[0].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": self.config.chartFont,
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

    _formatDataLabel: function (items) {
        BI.each(items, function (idx, item) {
            if (item.dataLabels && item.dataLabels.formatter && item.dataLabels.styleSetting.type === BICst.DATA_LABEL_STYLE_TYPE.TEXT) {
                item.dataLabels.formatter.identifier = item.dataLabels.formatterConf.x + item.dataLabels.formatterConf.y + item.dataLabels.formatterConf.z;
            }
        })
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

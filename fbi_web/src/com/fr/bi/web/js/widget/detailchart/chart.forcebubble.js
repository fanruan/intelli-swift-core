/**
 * 图表控件
 * @class BI.ForceBubbleChart
 * @extends BI.Widget
 */
BI.ForceBubbleChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.ForceBubbleChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-force-bubble-chart"
        })
    },

    _init: function () {
        BI.ForceBubbleChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.ForceBubbleChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this, o = this.options;
        config.chartType = 'forceBubble';
        config.colors = this.config.chart_color;

        switch (this.config.rules_display) {
            case BICst.DISPLAY_RULES.FIXED:
                delete config.legend;
                formatFixedLegend();
                break;
            case BICst.DISPLAY_RULES.GRADIENT:
                delete config.legend;
                formatGradientLegend();
                break;
            case BICst.DISPLAY_RULES.DIMENSION:
            default:
                formatLegend();
                break;
        }

        config.plotOptions.force = true;
        config.plotOptions.shadow = this.config.bubble_style !== this.constants.NO_PROJECT;
        config.plotOptions.bubble.minSize = this.config.bubble_min_size;
        config.plotOptions.bubble.maxSize = this.config.bubble_max_size;
        config.plotOptions.dataLabels.enabled = true;
        config.plotOptions.dataLabels.align = "inside";
        config.plotOptions.dataLabels.style = this.config.chart_font;
        config.plotOptions.dataLabels.formatter.identifier = "${CATEGORY}${VALUE}";
        delete config.xAxis;
        delete config.yAxis;
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                da.y = self.formatXYDataWithMagnify(da.y, 1);
            })
        });
        config.legend.style = this.config.chart_font;
        return [items, config];

        function formatLegend() {
            switch (self.config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    config.legend.enabled = true;
                    config.legend.position = "bottom";
                    config.legend.maxHeight = self.constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    config.legend.enabled = true;
                    config.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    config.legend.enabled = false;
                    break;
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

            BI.each(self.config.fixed_colors, function (idx, item) {
                if (idx == 0 && min < item.range.min) {
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

                if (idx == (self.config.fixed_colors.length - 1) && max > item.range.max) {
                    range.push({
                        from: item.range.max,
                        to: max,
                        color: item.color
                    })
                }
            });

            config.rangeLegend.range = range;

            switch (self.config.chart_legend) {
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

            BI.each(self.config.gradient_colors, function (idx, item) {
                var minProp = (item.range.min - min) / (max - min);
                var maxProp = (item.range.max - min) / (max - min);

                if (idx == 0 && minProp > 0) {
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
                color.push([maxProp, item.color_range.to_color]);

                if(idx == self.config.gradient_colors.length - 1 && maxProp < 1) {
                    color.push([1, item.color_range.to_color])
                }
            });

            if (color.length > 1) {
                config.rangeLegend.range.color = color;
            }

            switch (self.config.chart_legend) {
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
                    if (BI.isNull(max) || max < da.y) {
                        max = da.y
                    }
                    if (BI.isNull(min) || min > da.y) {
                        min = da.y
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

            if (err <= .15) step *= 10; else if (err <= .35) step *= 5; else if (err <= .75) step *= 2;

            return step;
        }

        function _linearNiceDomain(minValue, maxValue, tickInterval) {
            minValue = VanUtils.accMul(Math.floor(minValue / tickInterval), tickInterval);
            maxValue = VanUtils.accMul(Math.ceil(maxValue / tickInterval), tickInterval);

            return [minValue, maxValue];
        }

    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            chart_color: options.chart_color || [],
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            bubble_style: options.bubble_style || c.NO_PROJECT,
            rules_display: options.rules_display || c.RULE_DISPLAY,
            fixed_colors: options.fixed_colors || [],
            gradient_colors: options.gradient_colors || [],
            bubble_min_size: options.bubble_min_size || c.BUBBLE_MIN_SIZE,
            bubble_max_size: options.bubble_max_size || c.BUBBLE_MAX_SIZE,
	     chart_font: options.chart_font || c.FONT_STYLE
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.FORCE_BUBBLE);
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
BI.ForceBubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.force_bubble_chart', BI.ForceBubbleChart);

/**
 * 图表控件
 * @class BI.MapChart
 * @extends BI.Widget
 */
BI.MapChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.MapChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-map-chart"
        })
    },

    _init: function () {
        BI.MapChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.MapChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function (config, items) {
        var self = this, o = this.options, c = this.constants;
        formatRangeLegend();
        delete config.legend;
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.plotOptions.tooltip.shared = true;
        var formatterArray = [];
        BI.backEach(items, function (idx, item) {
            if (BI.has(item, "settings")) {
                formatterArray.push(formatToolTipAndDataLabel(item.settings.format || c.NORMAL, item.settings.num_level || c.NORMAL));
            }
        });
        config.plotOptions.tooltip.formatter = function () {
            var tip = this.name;
            BI.each(this.points, function (idx, point) {
                var value = point.size || point.y;
                tip += ('<div>' + point.seriesName + ':' + (window.FR ? FR.contentFormat(value, formatterArray[idx]) : value) + '</div>');
            });
            return tip;
        };
        config.plotOptions.dataLabels.formatter.valueFormat = function () {
            return window.FR ? FR.contentFormat(arguments[0], formatterArray[0]) : arguments[0];
        };

        config.geo = this.config.geo;
        if (this.config.initDrillPath.length > 1) {
            config.initDrillPath = this.config.initDrillPath;
        }
        config.dTools.click = function (point) {
            point = point || {};
            var pointOption = point.pointOption || {};
            self.fireEvent(BI.MapChart.EVENT_CLICK_DTOOL, pointOption);
        };
        config.chartType = "areaMap";
        delete config.xAxis;
        delete config.yAxis;

        var find = BI.find(items, function (idx, item) {
            return BI.has(item, "type") && item.type === "areaMap";
        });
        if (BI.isNull(find)) {
            items.push({
                type: "areaMap",
                data: []
            })
        }
        return [items, config];

        function formatRangeLegend() {
            config.rangeLegend.enabled = true;
            switch (self.config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    config.rangeLegend.visible = true;
                    config.rangeLegend.position = "bottom";
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    config.rangeLegend.visible = true;
                    config.rangeLegend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                    config.rangeLegend.visible = false;
                    break;
            }
            config.rangeLegend.continuous = false;
            config.rangeLegend.range = getRangeStyle(self.config.map_styles, self.config.auto_custom, self.config.theme_color);
            config.rangeLegend.formatter = function () {
                var to = this.to;
                if (BI.isNotEmptyArray(items) && BI.has(items[0], "settings")) {
                    switch (items[0].settings.num_level || c.NORMAL) {
                        case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                            to += '';
                            break;
                        case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                            to += BI.i18nText("BI-Wan");
                            break;
                        case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                            to += BI.i18nText("BI-Million");
                            break;
                        case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                            to += BI.i18nText("BI-Yi");
                            break;
                        case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                            to = FR.contentFormat(BI.parseFloat(to), "#0%");
                            break;
                    }
                }
                return to
            };
        }

        function formatToolTipAndDataLabel(format, numberLevel) {
            var formatter = '#.##';
            switch (format) {
                case self.constants.NORMAL:
                    formatter = '#.##';
                    break;
                case self.constants.ZERO2POINT:
                    formatter = '#0';
                    break;
                case self.constants.ONE2POINT:
                    formatter = '#0.0';
                    break;
                case self.constants.TWO2POINT:
                    formatter = '#0.00';
                    break;
            }

            switch (numberLevel) {
                case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    formatter += '';
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                    formatter += BI.i18nText("BI-Wan");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                    formatter += BI.i18nText("BI-Million");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                    formatter += BI.i18nText("BI-Yi");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                    if (format === self.constants.NORMAL) {
                        formatter = '#0%'
                    } else {
                        formatter += '%';
                    }
                    break;
            }

            return formatter;
        }

        function getRangeStyle(styles, change, defaultColor) {
            var range = [], color = null, defaultStyle = {};
            var conditionMax = null, conditionMin = null, max = null, min = null;

            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, it) {
                    if (BI.isNull(min) || BI.parseFloat(min) > BI.parseFloat(it.y)) {
                        min = it.y
                    }
                    if (BI.isNull(max) || BI.parseFloat(max) < BI.parseFloat(it.y)) {
                        max = it.y
                    }
                })
            });

            switch (change) {
                case BICst.SCALE_SETTING.AUTO:
                    defaultStyle.color = defaultColor;
                    return defaultStyle;
                case BICst.SCALE_SETTING.CUSTOM:
                    if (styles.length !== 0) {
                        BI.each(styles, function (idx, style) {
                            range.push({
                                color: style.color,
                                from: style.range.min,
                                to: style.range.max
                            });
                            color = style.color;
                            conditionMax = style.range.max
                        });

                        conditionMin = BI.parseInt(styles[0].range.min);
                        if (conditionMin !== 0) {
                            range.push({
                                color: "#808080",
                                from: 0,
                                to: conditionMin
                            });
                        }

                        var maxScale = _calculateValueNiceDomain(0, max)[1];

                        if (conditionMax < maxScale) {
                            range.push({
                                color: color,
                                from: conditionMax,
                                to: maxScale
                            });
                        }
                        return range;
                    } else {
                        defaultStyle.color = defaultColor;
                        return defaultStyle;
                    }
            }
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

    _formatDrillItems: function (items) {
        var self = this;
        BI.each(items.series, function (idx, da) {
            BI.each(da.data, function (idx, data) {
                data.y = data.y.toFixed(self.constants.FIX_COUNT);
                if (self.constants.MINLIMIT.sub(Math.abs(data.y)) > 0) {
                    data.y = 0;
                }
                if (BI.has(da, "settings")) {
                    data.y = self._formatNumberLevel(da.settings.num_level || self.constants.NORMAL, data.y);
                }
                if (BI.has(da, "type") && da.type == "bubble") {
                    data.name = data.x;
                    data.size = data.y;
                } else {
                    data.name = data.x;
                    data.value = data.y;
                }
                if (BI.has(data, "drilldown")) {
                    self._formatDrillItems(data.drilldown);
                }
            })
        });
    },

    _formatItems: function (items) {
        var self = this;
        this.max = null;
        this.min = null;
        BI.each(items, function (idx, item) {
            BI.each(item, function (id, it) {
                BI.each(it.data, function (i, da) {
                    da.y = da.y.toFixed(self.constants.FIX_COUNT);
                    if (self.constants.MINLIMIT.sub(Math.abs(da.y)) > 0) {
                        da.y = 0;
                    }
                    if (BI.has(it, "settings")) {
                        da.y = self._formatNumberLevel(it.settings.num_level || self.constants.NORMAL, da.y);
                    }
                    if ((BI.isNull(self.max) || BI.parseFloat(da.y) > BI.parseFloat(self.max)) && id === 0) {
                        self.max = da.y;
                    }
                    if ((BI.isNull(self.min) || BI.parseFloat(da.y) > BI.parseFloat(self.min)) && id === 0) {
                        self.min = da.y;
                    }
                    if (BI.has(it, "type") && it.type == "bubble") {
                        da.name = da.x;
                        da.size = da.y;
                    } else {
                        da.name = da.x;
                        da.value = da.y;
                    }
                    if (BI.has(da, "drilldown")) {
                        self._formatDrillItems(da.drilldown);
                    }
                })
            })
        });
        return items;
    },

    _formatNumberLevel: function (numberLevel, y) {
        var magnify = 1;
        switch (numberLevel) {
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
        y = BI.parseFloat(y);
        y = FR.contentFormat(BI.parseFloat(y.div(magnify).toFixed(2)), "#.####");
        return y;
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            geo: options.geo || {data: BICst.MAP_PATH[BICst.MAP_TYPE.CHINA], name: BI.i18nText("BI-China")},
            initDrillPath: options.initDrillPath || [],
            tooltip: options.tooltip || "",
            theme_color: options.theme_color || "#65bce7",
            map_styles: options.map_styles || [],
            auto_custom: options.auto_custom || c.AUTO_CUSTOM
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.MAP);
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
BI.MapChart.EVENT_CHANGE = "EVENT_CHANGE";
BI.MapChart.EVENT_CLICK_DTOOL = "EVENT_CLICK_DTOOL";
$.shortcut('bi.map_chart', BI.MapChart);
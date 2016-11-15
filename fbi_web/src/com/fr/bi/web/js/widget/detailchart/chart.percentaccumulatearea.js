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
        config.colors = this.config.chartColor;
        config.plotOptions.style = formatChartStyle();
        formatCordon();
        this.formatChartLegend(config, this.config.legend);
        config.plotOptions.dataLabels.enabled = this.config.showDataLabel;
        config.dataSheet.enabled = this.config.showDataTable;
        config.xAxis[0].showLabel = !config.dataSheet.enabled;
        this.formatZoom(config, this.config.showZoom);

        config.yAxis = this.yAxis;
        BI.extend(config.yAxis[0], self.leftAxisSetting(self.config));
        self.formatNumberLevelInYaxis(config, items, this.config.leftYNumberLevel, this.constants.LEFT_AXIS, config.yAxis[0].formatter);

        BI.extend(config.xAxis[0], self.catSetting(this.config));

        config.legend.style = BI.extend(this.config.legendStyle, {
            fontSize: this.config.legendStyle.fontSize + "px"
        });

        config.chartType = "area";
        config.plotOptions.tooltip.formatter.identifier = "${CATEGORY}${SERIES}${PERCENT}";

        //为了给数据标签加个%,还要遍历所有的系列，唉
        this.formatDataLabelForAxis(config.plotOptions.dataLabels.enabled, items, config.yAxis[0].formatter, this.config.chartFont);

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
        this.config = self.getChartConfig(options);
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

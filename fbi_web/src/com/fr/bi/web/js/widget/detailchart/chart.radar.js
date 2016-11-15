/**
 * 图表控件
 * @class BI.RadarChart
 * @extends BI.Widget
 */
BI.RadarChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.RadarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-radar-chart"
        })
    },

    _init: function () {
        BI.RadarChart.superclass._init.apply(this, arguments);
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
            popupItemsGetter: o.popupItemsGetter,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.RadarChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this;
        var title = getXYAxisUnit(this.config.rightYNumberLevel, this.constants.LEFT_AXIS);

        config.colors = this.config.chartColor;
        config.plotOptions.style = formatChartStyle();
        formatChartRadarStyle();
        this.formatChartLegend(config, this.config.legend);
        config.plotOptions.dataLabels.enabled = this.config.showDataLabel;

        config.radiusAxis = this.radiusAxis;
        config.angleAxis = this.angleAxis;
        config.radiusAxis[0].formatter = self.formatTickInXYaxis(this.config.leftYNumberFormat, this.config.leftYNumberLevel, this.config.leftYSeparator);
        formatNumberLevelInYaxis(this.config.leftYNumberLevel, this.constants.LEFT_AXIS, config.radiusAxis[0].formatter);
        config.radiusAxis[0].title.text = this.config.leftYShowTitle === true ? this.config.leftYTitle + title : title;
        config.radiusAxis[0].gridLineWidth = this.config.hShowGridLine === true ? 1 : 0;
        config.radiusAxis[0].gridLineColor = this.config.hGridLineColor;
        config.radiusAxis[0].lineColor = this.config.vShowGridLine;
        config.radiusAxis[0].min = this.config.leftYCustomScale.minScale.scale || null;
        config.radiusAxis[0].max = this.config.leftYCustomScale.maxScale.scale || null;
        config.radiusAxis[0].tickInterval = BI.isNumber(self.config.leftYCustomScale.interval.scale) && self.config.leftYCustomScale.interval.scale > 0 ?
            self.config.leftYCustomScale.interval.scale : null;
        config.chartType = "radar";
        config.plotOptions.connectNulls = this.config.nullContinuity;
        delete config.xAxis;
        delete config.yAxis;
        //为了给数据标签加个%,还要遍历所有的系列，唉
        this.formatDataLabelForOthers(config.plotOptions.dataLabels.enabled, items, config.radiusAxis[0].formatter, this.config.chart_font);

        config.angleAxis[0].labelStyle = BI.extend(this.config.leftYLabelStyle.textStyle, {
            fontSize: this.config.leftYLabelStyle.textStyle.fontSize + "px"
        });

        config.angleAxis[0].showLabel = this.config.leftYShowLabel;
        config.angleAxis[0].lineColor = this.config.leftYLineColor;
        config.angleAxis[0].gridLineWidth = this.config.vShowGridLine === true ? 1 : 0;

        config.radiusAxis[0].title.style = this.config.chartFont;
        config.angleAxis[0].title.style = this.config.chartFont;

        config.legend.style = BI.extend(this.config.legendStyle, {
            fontSize: this.config.legendStyle.fontSize + "px"
        });

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

        function formatChartRadarStyle() {
            switch (self.config.radarChartType) {
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
        this.config = self.getChartConfig(options);
        this.options.items = items;
        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.RADAR);
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
BI.RadarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.radar_chart', BI.RadarChart);

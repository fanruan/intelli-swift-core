/**
 * Created by fay on 2017/1/3.
 */
/**
 * 图表控件 漏斗图
 * @class BI.FunnelChart
 * @extends BI.Widget
 */
BI.FunnelChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.FunnelChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-funnel-chart"
        })
    },

    _init: function () {
        BI.FunnelChart.superclass._init.apply(this, arguments);
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
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            popupItemsGetter: o.popupItemsGetter,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.FunnelChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this, o = this.options;
        delete config.zoom;
        config.colors = this.config.chartColor;
        config.plotOptions.style = formatChartStyle();
        config.plotOptions.useSameSlantAngle = formatSlantStyle();

        this.formatChartLegend(config, this.config.legend);

        config.plotOptions.tooltip.formatter.identifier = "${NAME}${VALUE}${PERCENT}";
        config.chartType = "funnel";
        delete config.xAxis;
        delete config.yAxis;

        BI.extend(config.plotOptions.dataLabels, {
            enabled: this.config.showDataLabel
        });
        config.plotOptions.dataLabels.formatter.identifier = "${NAME}${VALUE}${PERCENT}";
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                da.y = self.formatXYDataWithMagnify(da.y, 1);
            })
        });

        config.legend.style = BI.extend({}, this.config.legendStyle, {
            fontSize: this.config.legendStyle && this.config.legendStyle.fontSize + "px"
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

        function formatSlantStyle() {
            switch (self.config.slantStyle) {
                case BICst.FUNNEL_SLANT_STYLE.DIFF:
                    return false;
                case BICst.FUNNEL_SLANT_STYLE.SAME:
                default:
                    return true;
            }
        }

    },

    _formatItems: function (items, options) {
        var self = this;
        BI.each(items, function (idx, item) {
            BI.each(item, function (id, it) {
                BI.each(it.data, function (i, da) {
                    da.y = self.formatXYDataWithMagnify(da.y, 1);
                    da.name = da.x;
                    da.value = da.y;
                });
            })
        });
        return items;
    },

    populate: function (items, options, types) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = self.getChartConfig(options);
        this.options.items = items;

        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.FUNNEL);
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
BI.FunnelChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.funnel_chart', BI.FunnelChart);

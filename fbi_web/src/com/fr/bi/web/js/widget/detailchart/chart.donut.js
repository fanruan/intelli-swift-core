/**
 * 图表控件
 * @class BI.DonutChart
 * @extends BI.Widget
 */
BI.DonutChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.DonutChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-donut-chart"
        })
    },

    _init: function () {
        BI.DonutChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            popupItemsGetter: o.popupItemsGetter,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.DonutChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this;
        delete config.zoom;
        config.colors = this.config.chartColor;
        config.plotOptions.style = formatChartStyle();

        this.formatChartLegend(config, this.config.legend);

        config.plotOptions.dataLabels.enabled = this.config.showDataLabel;

        config.plotOptions.innerRadius = "50.0%";
        config.chartType = "pie";
        BI.extend(config.plotOptions.dataLabels, {
            align: self.setDataLabelPosition(this.config),
            style: this.config.dataLabelSetting.textStyle,
            connectorWidth: 1
        });
        config.plotOptions.dataLabels.formatter.identifier = self.setDataLabelContent(this.config);
        delete config.xAxis;
        delete config.yAxis;
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
                type.push(BICst.WIDGET.DONUT);
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
BI.DonutChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.donut_chart', BI.DonutChart);
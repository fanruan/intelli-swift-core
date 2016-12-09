/**
 * 图表控件
 * @class BI.RectTreeChart
 * @extends BI.Widget
 */
BI.RectTreeChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.RectTreeChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-rect-tree-chart"
        })
    },

    _init: function () {
        BI.RectTreeChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            popupItemsGetter: o.popupItemsGetter,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.RectTreeChart.EVENT_CHANGE, obj);
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

        this.formatChartLegend(config, this.config.legend);

        config.plotOptions.tooltip.formatter.identifier = "${NAME}${SERIES}${VALUE}";
        config.chartType = "treeMap";
        delete config.xAxis;
        delete config.yAxis;

        BI.extend(config.plotOptions.dataLabels, {
            enabled: this.config.showDataLabel,
            align: self.setDataLabelPosition(this.config),
            style: this.config.dataLabelSetting.textStyle,
            connectorWidth: this.config.dataLabelSetting.showTractionLine,
        });
        config.plotOptions.dataLabels.formatter.identifier = self.setDataLabelContent(this.config);
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

    _formatDrillItems: function (items) {
        var self = this;
        BI.each(items, function (idx, data) {
            data.y = self.formatXYDataWithMagnify(data.y, 1);
            data.name = data.x;
            data.value = data.y;
            if (BI.has(data, "children")) {
                self._formatDrillItems(data.children);
            }
        });
        return items;
    },

    _formatItems: function (items) {
        var self = this;
        BI.each(items, function (idx, item) {
            BI.each(item, function (id, it) {
                BI.each(it.data, function (i, da) {
                    da.y = self.formatXYDataWithMagnify(da.y, 1);
                    da.name = da.x;
                    da.value = da.y;
                    if (BI.has(da, "children")) {
                        self._formatDrillItems(da.children);
                    }
                });
            })
        });
        return items;
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
                type.push(BICst.WIDGET.RECT_TREE);
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
BI.RectTreeChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.rect_tree_chart', BI.RectTreeChart);
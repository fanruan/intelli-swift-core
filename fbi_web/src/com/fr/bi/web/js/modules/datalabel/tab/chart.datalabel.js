/**
 * 条形图预览
 * Created by Fay on 2016/7/13.
 */
BI.DataLabelChart = BI.inherit(BI.Widget, {
    _data: [[{
        "data": [
            {"x": "data1", "y": 40},
            {"x": "data2", "y": 70},
            {"x": "data3", "y": 100},
            {"x": "data4", "y": 30},
            {"x": "data5", "y": 10}
        ],
        "name": "data"
    }]],
    _constant: {
        ICON_WIDTH: 20,
        ICON_HEIGHT: 20
    },
    _defaultConfig: function () {
        var conf = BI.DataLabelChart.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: ""
        });
    },

    _init: function () {
        BI.DataLabelChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var title = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Preview")
        });
        this.xAxis = [{type: "category"}];
        var type = this.createChartByType(o.chartType);
        this.combineChart = BI.createWidget({
            type: type,
            width: 150,
            height: 130,
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this)
        });
        BI.createWidget({
            type: "bi.absolute",
            cls: "bi-data-tab-chart",
            element: this.element,
            items: [{
                el: this.combineChart,
                left: -5,
                top: 10
            }, {
                el: title,
                left: 20,
                top: 5
            }],
            width: 150,
            height: 130
        });
    },

    createChartByType: function (type) {
        switch (type) {
            case BICst.WIDGET.AXIS:
                return "bi.combine_chart";
            case BICst.WIDGET.BUBBLE:
                return "bi.bubble_chart";
            case BICst.WIDGET.SCATTER:
                return "bi.scatter_chart";
        }
    },

    _formatConfig: function (config, items) {
        config.legend.enabled = false;
        config.legend.visible = false;
        config.legend.margin = 0;
        config.xAxis[0].showLabel = false;
        config.xAxis[0].enableTick = false;
        config.yAxis[0].showLabel = false;
        config.yAxis[0].lineWidth = 0;
        config.yAxis[0].tickInterval = 25;
        config.plotOptions.dataLabels.enabled = true;
        config.plotOptions.dataLabels.align = "inside";
        config.plotOptions.dataLabels.style = {
            color: "#ffffff"
        };
        return [items, config];
    },

    populate: function (src) {
        if (src) {
            var formatter = "function() { return '<div><img width=" + this._constant.ICON_WIDTH + "px height=" + this._constant.ICON_HEIGHT + "px src=" + src + "></div>'}";
            this._data[0][0].data[0].dataLabels = {
                enabled: true,
                align: "outside",
                useHtml: true,
                formatter: formatter
            };
        }
        this.combineChart.populate(this._data);
    }
});
BI.DataLabelChart.EVENT_CHANGE = "BI.DataLabelChart.EVENT_CHANGE";
$.shortcut("bi.data_label_chart", BI.DataLabelChart);
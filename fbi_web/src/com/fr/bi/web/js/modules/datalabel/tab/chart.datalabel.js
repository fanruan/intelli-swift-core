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
    _bubble_data: [[{
        "data": [
            {"x": 30, "y": 40, "z":20},
            {"x": 40, "y": 70, "z":50},
            {"x": 50, "y": 100, "z":60},
            {"x": 20, "y": 30, "z":10},
            {"x": 70, "y": 10, "z":80}
        ]
    }]],
    _scatter_data: [[{
        "data": [
            {"x": 30, "y": 40},
            {"x": 40, "y": 70},
            {"x": 50, "y": 100},
            {"x": 20, "y": 30},
            {"x": 70, "y": 10}
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
        var o = this.options;
        var title = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Preview")
        });
        this.xAxis = [{type: "category"}];
        var type = this.createChartByType(o.chartType);
        this.config = this._formatConfig();
        this.combineChart = BI.createWidget({
            type: type,
            width: 170,
            height: 140
        });
        BI.createWidget({
            type: "bi.absolute",
            cls: "bi-data-tab-chart",
            element: this.element,
            items: [{
                el: this.combineChart
            }, {
                el: title,
                left: 20,
                top: 5
            }],
            width: 170,
            height: 140
        });
    },

    createChartByType: function (type) {
        switch (type) {
            case BICst.WIDGET.LINE:
                this.data = this._data;
                return "bi.line_chart";
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
                this.data = this._data;
                return "bi.area_chart";
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                this.data = this._data;
                return "bi.bar_chart";
            case BICst.WIDGET.BUBBLE:
                this.data = this._bubble_data;
                return "bi.bubble_chart";
            case BICst.WIDGET.SCATTER:
                this.data = this._scatter_data;
                return "bi.scatter_chart";
            default:
                this.data = this._data;
                return "bi.axis_chart";
        }
    },

    _formatConfig: function () {
        var config = {};
        config.chart_legend = BICst.CHART_LEGENDS.NOT_SHOW;
        config.show_label = false;
        return config;
    },

    populate: function (src) {
        var data = BI.deepClone(this.data);
        if (src) {
            if(this.options.showType === "data_image"){
                data[0][0].data[0].imageHeight = 20;
                data[0][0].data[0].imageWidth = 20;
                data[0][0].data[0].image = src
            } else {
                var formatter = "function() { return '<div><img width=" + this._constant.ICON_WIDTH + "px height=" + this._constant.ICON_HEIGHT + "px src=" + src + "></div>'}";
                data[0][0].data[0].dataLabels = {
                    enabled: true,
                    align: "outside",
                    useHtml: true,
                    formatter: formatter
                };
            }
        }
        this.combineChart.populate(data, this.config);
    }
});
BI.DataLabelChart.EVENT_CHANGE = "BI.DataLabelChart.EVENT_CHANGE";
$.shortcut("bi.data_label_chart", BI.DataLabelChart);
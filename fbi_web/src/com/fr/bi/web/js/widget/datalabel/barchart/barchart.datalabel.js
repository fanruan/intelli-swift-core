/**
 * 条形图预览
 * Created by Fay on 2016/7/13.
 */
BI.DataLabelBarChart = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelBarChart.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: ""
        });
    },
    
    _init: function () {
        BI.DataLabelBarChart.superclass._init.apply(this, arguments);
        this.data = [[{
            "data": [],
            "name": "scores"
        }]];
        this.xAxis = [{type: "category"}];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            width: 150,
            height: 130,
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.populate(this.data);
    },

    _formatConfig: function (config, items) {
        console.log(config);
        config.legend.enabled = false;
        config.xAxis[0].showLabel = false;
        config.yAxis[0].showLabel = false;
        config.yAxis[0].lineWidth = 0;
        config.yAxis[0].min = 0;
        config.yAxis[0].max = 100;
        config.yAxis[0].tickInterval = 40;
        config.drag = false;
        return [items, config];
    },

    _formatItems: function (items) {
        BI.each(items, function (idx, item) {
            BI.each(item, function (id, it) {
                BI.each(it.data, function (i, t) {
                    var tmp = t.x;
                    t.x = t.y;
                    t.y = tmp;
                })
            });
        });
        return items;
    },

    populate: function (items) {
        this.combineChart.populate(items, types);
    }
});
BI.DataLabelBarChart.EVENT_CHANGE = "BI.DataLabelBarChart.EVENT_CHANGE";
$.shortcut("bi.data_label_bar_chart", BI.DataLabelBarChart);
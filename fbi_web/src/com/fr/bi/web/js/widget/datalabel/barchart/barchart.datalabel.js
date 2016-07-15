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
        var title = BI.createWidget({
            type: "bi.label",
            text: "预览"
        });
        this.data = [[{
            "data": [
                {"x": "孙林", "y": 90},
                {"x": "金士鹏", "y": 70},
                {"x": "张珊", "y": 100},
                {"x": "孙阳", "y": 30},
                {"x": "袁成洁", "y": 10}
            ],
            "name": "scores"
        }]];
        this.xAxis = [{type: "category"}];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            width: 150,
            height: 130,
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this)
        });
        BI.createWidget({
            type: "bi.absolute",
            cls: "bi-data-label-bar-chart",
            element: this.element,
            items: [{
                el: this.combineChart,
                left: -5,
                top: 10
            },{
                el: title,
                left:20,
                top: 5
            }],
            width: 150,
            height: 130
        });
        this.combineChart.populate(this.data);
    },

    _formatConfig: function (config, items) {
        config.legend.enabled = false;
        config.legend.margin = 0;
        config.xAxis[0].showLabel = false;
        config.xAxis[0].enableTick = false;
        config.yAxis[0].showLabel = false;
        config.yAxis[0].lineWidth = 0;
        config.yAxis[0].tickInterval = 25;
        config.plotOptions.dataLabels.enabled = true;
        config.plotOptions.dataLabels.align = "inside";
        config.plotOptions.dataLabels.style = {
            "color": "#ffffff"
        };
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
/**
 * 图表控件
 * @class BI.AccumulateAxisChart
 * @extends BI.Widget
 */
BI.AccumulateAxisChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-axis-chart"
        })
    },

    _init: function () {
        BI.AccumulateAxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AccumulateAxisChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.AccumulateAxisChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateAxisChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.AccumulateAxisChart.populate([BI.AccumulateAxisChart.formatItems(items)]);
    },

    resize: function () {
        this.AccumulateAxisChart.resize();
    }
});
BI.extend(BI.AccumulateAxisChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name,
            stack: true
        }
    },
    formatConfig: function () {
        return {
            "plotOptions": {
                "categoryGap": "16.0%",
                "borderColor": "rgb(255,255,255)",
                "borderWidth": 1,
                "gap": "22.0%",
                "tooltip": {
                    "formatter": {
                        "identifier": "${CATEGORY}${SERIES}${VALUE}",
                        "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                        "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                    },
                    "shared": false,
                    "padding": 5,
                    "backgroundColor": "rgba(0,0,0,0.4980392156862745)",
                    "borderColor": "rgb(0,0,0)",
                    "shadow": false,
                    "borderRadius": 2,
                    "borderWidth": 0,
                    "follow": false,
                    "enabled": true,
                    "animation": true
                },
                "animation": true
            },
            "borderColor": "rgb(238,238,238)",
            "xAxis": [{
                "enableMinorTick": false,
                "minorTickColor": "rgb(176,176,176)",
                "tickColor": "rgb(176,176,176)",
                "showArrow": false,
                "lineColor": "rgb(176,176,176)",
                "plotLines": [],
                "type": "category",
                "lineWidth": 1,
                "showLabel": true,
                "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "gridLineWidth": 0,
                "enableTick": true,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "11pt", "fontWeight": ""},
                "plotBands": [],
                "position": "bottom",
                "labelRotation": 0,
                "reversed": false
            }],
            "shadow": false,
            "legend": {
                "borderColor": "rgb(204,204,204)",
                "borderRadius": 0,
                "shadow": false,
                "borderWidth": 0,
                "style": {"fontFamily": "微软雅黑", "color": "rgba(102,102,102,1.0)", "fontSize": "11pt", "fontWeight": ""},
                "position": "right",
                "enabled": true
            },
            "zoom": {"zoomType": "xy", "zoomTool": {"visible": false, "resize": true, "from": "", "to": ""}},
            "plotBorderColor": "rgba(255,255,255,0)",
            "tools": {
                "hidden": true,
                "toImage": {"enabled": true},
                "sort": {"enabled": true},
                "enabled": true,
                "fullScreen": {"enabled": true}
            },
            "plotBorderWidth": 0,
            "colors": ["rgb(99,178,238)", "rgb(118,218,145)"],
            "yAxis": [{
                "enableMinorTick": false,
                "gridLineColor": "rgb(196,196,196)",
                "minorTickColor": "rgb(176,176,176)",
                "tickColor": "rgb(176,176,176)",
                "showArrow": false,
                "lineColor": "rgb(176,176,176)",
                "plotLines": [],
                "type": "value",
                "lineWidth": 0,
                "showLabel": true,
                "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                "gridLineWidth": 1,
                "enableTick": true,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "11pt", "fontWeight": ""},
                "plotBands": [],
                "position": "left",
                "labelRotation": 0,
                "reversed": false
            }],
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "column",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
    }
});
BI.AccumulateAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_axis_chart', BI.AccumulateAxisChart);
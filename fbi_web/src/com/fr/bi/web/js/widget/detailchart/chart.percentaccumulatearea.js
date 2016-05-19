/**
 * 图表控件
 * @class BI.PercentAccumulateAreaChart
 * @extends BI.Widget
 */
BI.PercentAccumulateAreaChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PercentAccumulateAreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-percent-accumulate-area-chart"
        })
    },

    _init: function () {
        BI.PercentAccumulateAreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.PercentAccumulateAreaChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.PercentAccumulateAreaChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.PercentAccumulateAreaChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.PercentAccumulateAreaChart.populate([BI.PercentAccumulateAreaChart.formatItems(items)]);
    },

    resize: function () {
        this.PercentAccumulateAreaChart.resize();
    }
});
BI.extend(BI.PercentAccumulateAreaChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name,
            stack: true
        }
    },
    formatConfig: function(){
        return {

            "plotOptions": {
                "fillColor": true,
                "fillColorOpacity": 0.15,
                "large": false,
                "connectNulls": false,
                "curve": false,
                "marker": {"symbol": "null_marker", "radius": 4.5, "enabled": true},
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
                "step": false,
                "lineWidth": 2,
                "animation": true,
                stackByPercent: true
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
                "enableTick": false,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "9pt", "fontWeight": ""},
                "plotBands": [{"color": "rgba(255,204,204,0.2980392156862745)", "from": "Friday", "to": "Saturday"}],
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
                "style": {
                    "fontFamily": "Microsoft YaHei UI",
                    "color": "rgba(102,102,102,1.0)",
                    "fontSize": "10pt",
                    "fontWeight": ""
                },
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
                "gridLineColor": "rgb(242,242,242)",
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
                "enableTick": false,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "9pt", "fontWeight": ""},
                "plotBands": [{"color": "rgba(204,255,255,0.2980392156862745)", "from": 7.5, "to": 10}],
                "position": "left",
                "labelRotation": 0,
                "reversed": false
            }],
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "area",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
    }
});
BI.PercentAccumulateAreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.percent_accumulate_area_chart', BI.PercentAccumulateAreaChart);
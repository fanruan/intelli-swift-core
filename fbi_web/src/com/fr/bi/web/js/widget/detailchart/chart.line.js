/**
 * 图表控件
 * @class BI.LineChart
 * @extends BI.Widget
 */
BI.LineChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.LineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-line-chart"
        })
    },

    _init: function () {
        BI.LineChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.LineChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.LineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.LineChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.LineChart.populate([BI.LineChart.formatItems(items)]);
    },

    resize: function () {
        this.LineChart.resize();
    }
});
BI.extend(BI.LineChart, {
    formatItems: function(items){
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name,
            stack: false
        }
    },
    formatConfig: function () {
        return {
            "plotOptions": {
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
                "lineWidth": 2,
                "showLabel": true,
                "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], 'Ddd') : arguments[0]}",
                "gridLineWidth": 0,
                "enableTick": false,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "9pt", "fontWeight": ""},
                "plotBands": [],
                "position": "bottom",
                "labelRotation": 0,
                "reversed": false
            }],
            "shadow": false,
            "legend": {"enabled": false},
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
            "colors": ["rgb(99,178,238)"],
            "yAxis": [{
                "enableMinorTick": false,
                "gridLineColor": "rgb(222,222,222)",
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
                "plotBands": [],
                "position": "left",
                "labelRotation": 0,
                "reversed": false
            }],
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "line",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
    }
});
BI.LineChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.line_chart', BI.LineChart);
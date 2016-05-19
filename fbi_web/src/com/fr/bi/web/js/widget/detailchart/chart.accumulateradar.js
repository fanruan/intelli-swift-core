/**
 * 图表控件
 * @class BI.AccumulateRadarChart
 * @extends BI.Widget
 */
BI.AccumulateRadarChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateRadarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-accumulate-bar-chart"
        })
    },

    _init: function () {
        BI.AccumulateRadarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.AccumulateRadarChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.AccumulateRadarChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AccumulateRadarChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.AccumulateRadarChart.populate([BI.AccumulateRadarChart.formatItems(items)]);
    },

    resize: function () {
        this.AccumulateRadarChart.resize();
    }
});
BI.extend(BI.AccumulateRadarChart, {
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
                click: function () {
                    self.fireEvent(BI.Chart.EVENT_CHANGE, {
                        category: this.category,
                        seriesName: this.seriesName,
                        value: this.value
                    });
                },
                "fillColor": true,
                "fillColorOpacity": 0,
                "columnType": false,
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
                "type": "circle",
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
                "lineWidth": 1,
                "showLabel": true,
                "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "gridLineWidth": 0,
                "enableTick": true,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "9pt", "fontWeight": ""},
                "plotBands": [],
                "position": "bottom",
                "labelRotation": 0,
                "reversed": false
            }],
            "shadow": false,
            "legend": {"enabled": false},
            "plotBorderColor": "rgba(255,255,255,0)",
            "tools": {
                "hidden": true,
                "toImage": {"enabled": true},
                "sort": {"enabled": true},
                "enabled": true,
                "fullScreen": {"enabled": true}
            },
            "plotBorderWidth": 0,
            "colors": ["rgb(99,178,238)", "rgb(118,218,145)", "rgb(248,203,127)"],
            "yAxis": [{
                "enableMinorTick": false,
                "gridLineColor": "rgb(196,196,196)",
                "minorTickColor": "rgb(176,176,176)",
                "tickColor": "rgb(176,176,176)",
                "showArrow": false,
                "lineColor": "rgb(176,176,176)",
                "plotLines": [],
                "type": "value",
                "lineWidth": 1,
                "showLabel": true,
                "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                "gridLineWidth": 1,
                "enableTick": true,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "9pt", "fontWeight": ""},
                "plotBands": [],
                "position": "left",
                "labelRotation": 0,
                "reversed": false
            }],
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "radar",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        }
    }
});
BI.AccumulateRadarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.accumulate_radar_chart', BI.AccumulateRadarChart);
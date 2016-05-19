/**
 * 图表控件
 * @class BI.ScatterChart
 * @extends BI.Widget
 */
BI.ScatterChart = BI.inherit(BI.Widget, {

    constants:{
        SCATTER_ITEM_COUNT: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.ScatterChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scatter-chart"
        })
    },

    _init: function () {
        BI.ScatterChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.ScatterChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.ScatterChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.ScatterChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.ScatterChart.populate([BI.ScatterChart.formatItems(items)]);
    },

    loading: function(){
        this.ScatterChart.loading();
    },

    loaded: function(){
        this.ScatterChart.loaded();
    },

    resize: function () {
        this.ScatterChart.resize();
    }
});
BI.extend(BI.ScatterChart, {
    formatItems: function (items) {
        var name = BI.keys(items)[0];
        return {
            "data": items[name],
            "name": name,
            stack: false
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
                "fillColorOpacity": 1,
                "large": false,
                "connectNulls": false,
                "curve": false,
                "marker": {
                    "symbol": "null_marker",
                    "radius": 4.5,
                    "enabled": true
                },
                "tooltip": {
                    "formatter": {
                        "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        "identifier": "${SERIES}${X}${Y}${SIZE}",
                        "yFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "xFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
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
                "lineWidth": 0,
                "animation": true
            },
            "borderColor": "rgb(238,238,238)",
            "xAxis": [
                {
                    "enableMinorTick": false,
                    "minorTickColor": "rgb(176,176,176)",
                    "tickColor": "rgb(176,176,176)",
                    "showArrow": false,
                    "lineColor": "rgb(176,176,176)",
                    "plotLines": [],
                    "type": "value",
                    "lineWidth": 1,
                    "showLabel": true,
                    "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                    "gridLineWidth": 0,
                    "enableTick": true,
                    "labelStyle": {
                        "fontFamily": "Verdana",
                        "color": "rgba(102,102,102,1.0)",
                        "fontSize": "11pt",
                        "fontWeight": ""
                    },
                    "plotBands": [],
                    "position": "bottom",
                    "labelRotation": 0,
                    "reversed": false
                }
            ],
            "shadow": false,
            "zoom": {
                "zoomType": "xy",
                "zoomTool": {
                    "visible": false,
                    "resize": true,
                    "from": "",
                    "to": ""
                }
            },
            "plotBorderColor": "rgba(255,255,255,0)",
            "tools": {
                "hidden": true,
                "toImage": {
                    "enabled": true
                },
                "sort": {
                    "enabled": false
                },
                "enabled": true,
                "fullScreen": {
                    "enabled": true
                }
            },
            "plotBorderWidth": 0,
            "colors": [
                "rgb(99,178,238)",
                "rgb(118,218,145)",
                "rgb(248,203,127)",
                "rgb(248,149,136)",
                "rgb(124,214,207)",
                "rgb(145,146,171)",
                "rgb(120,152,225)",
                "rgb(239,166,102)",
                "rgb(237,221,134)",
                "rgb(153,135,206)",
                "rgb(99,178,238)",
                "rgb(118,218,145)",
                "rgb(248,203,127)",
                "rgb(248,149,136)",
                "rgb(124,214,207)",
                "rgb(145,146,171)",
                "rgb(120,152,225)",
                "rgb(239,166,102)",
                "rgb(237,221,134)",
                "rgb(153,135,206)",
                "rgb(99,178,238)",
                "rgb(118,218,145)",
                "rgb(248,203,127)"
            ],
            "yAxis": [
                {
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
                    "enableTick": false,
                    "labelStyle": {
                        "fontFamily": "Verdana",
                        "color": "rgba(102,102,102,1.0)",
                        "fontSize": "11pt",
                        "fontWeight": ""
                    },
                    "plotBands": [],
                    "position": "left",
                    "labelRotation": 0,
                    "reversed": false
                }
            ],
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "scatter",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
    }
});
BI.ScatterChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.scatter_chart', BI.ScatterChart);
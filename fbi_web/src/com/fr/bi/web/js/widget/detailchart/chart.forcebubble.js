/**
 * 图表控件
 * @class BI.ForceBubbleChart
 * @extends BI.Widget
 */
BI.ForceBubbleChart = BI.inherit(BI.Widget, {

    constants:{
        BUBBLE_ITEM_COUNT: 3
    },

    _defaultConfig: function () {
        return BI.extend(BI.ForceBubbleChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-force-chart"
        })
    },

    _init: function () {
        BI.ForceBubbleChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.ForceBubbleChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.ForceBubbleChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.ForceBubbleChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.ForceBubbleChart.populate([BI.ForceBubbleChart.formatItems(items)]);
    },

    resize: function () {
        this.ForceBubbleChart.resize();
    }
});
BI.extend(BI.ForceBubbleChart, {
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
                        value: this.value,
                        size: this.size
                    });
                },
                "large": false,
                "connectNulls": false,
                "shadow": true,
                "curve": false,
                "sizeBy": "area",
                "tooltip": {
                    "formatter": {
                        "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        "identifier": "${X}${Y}${SIZE}",
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
                    "style": {},
                    "follow": false,
                    "enabled": true,
                    "animation": false
                },
                "maxSize": 60,
                "lineWidth": 0,
                "animation": true,
                "dataLabels": {
                    "formatter": {
                        "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        "identifier": "${X}${Y}${SIZE}",
                        "yFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "xFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                    },
                    "align": "inside",
                    "enabled": true
                },
                "fillColorOpacity": 0.699999988079071,
                "marker": {
                    "symbol": "circle",
                    "radius": 28.39695010101295,
                    "enabled": true
                },
                "step": false,
                "force": true,
                "minSize": 12,
                "displayNegative": true
            },
            "borderColor": "rgb(196,196,196)",
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
            "plotBorderWidth": 0,
            "colors": [
                "rgb(99,178,238)",
                "rgb(118,218,145)",
                "rgb(248,203,127)",
                "rgb(248,149,136)",
                "rgb(124,214,207)",
                "rgb(145,146,171)",
                "rgb(120,152,225)",
                "rgb(239,166,102)"
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
            "chartType": "bubble",
            "style": "gradual",
            "legend": {
                "borderColor": "rgb(204,204,204)",
                "borderRadius": 0,
                "shadow": false,
                "borderWidth": 0,
                "style": {
                    "fontFamily": "Dialog",
                    "color": "rgba(102,102,102,1.0)",
                    "fontSize": "11pt",
                    "fontWeight": ""
                },
                "position": "right",
                "enabled": false
            },
            "plotShadow": false,
            "plotBorderRadius": 0
        }
    }
});
BI.ForceBubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.force_bubble_chart', BI.ForceBubbleChart);
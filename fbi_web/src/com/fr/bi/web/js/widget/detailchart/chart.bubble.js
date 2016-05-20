/**
 * 图表控件
 * @class BI.BubbleChart
 * @extends BI.Widget
 */
BI.BubbleChart = BI.inherit(BI.Widget, {

    constants:{
        BUBBLE_ITEM_COUNT: 3
    },

    _defaultConfig: function () {
        return BI.extend(BI.BubbleChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scatter-chart"
        })
    },

    _init: function () {
        BI.BubbleChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.BubbleChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.BubbleChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.BubbleChart.EVENT_CHANGE, obj);
        });
    },

    formatItems: function (items) {
        return BI.map(items, function(idx, item){
            var name = BI.keys(item)[0];
            return {
                "data": BI.map(item[name], function(idx, it){
                    return BI.extend(it, {
                        "x": it.x,
                        "y": it.y,
                        "size": it.z
                    });
                }),
                "name": name
            }
        });
    },

    setTypes: function(){
    },

    populate: function (items) {
        var self = this;
        var config = BI.BubbleChart.formatConfig();
        config.plotOptions.click = function(){
            self.fireEvent(BI.BubbleChart.EVENT_CHANGE, {category: this.category,
                seriesName: this.seriesName,
                value: this.value,
                options: this.pointOption.options});
        };
        this.BubbleChart.populate(this.formatItems(items), config);
    },

    resize: function () {
        this.BubbleChart.resize();
    }
});
BI.extend(BI.BubbleChart, {
    formatConfig: function(){
        return {
            "plotOptions": {
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
                "force": false,
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
            "rangeLegend": {
                "borderColor": "rgb(204,204,204)",
                "borderRadius": 0,
                "shadow": false,
                "borderWidth": 0,
                "floating": true,
                "x": "50.0%",
                "y": "50.0%",
                "range": {
                    "min": -19,
                    "color": [
                        [
                            0,
                            "rgb(182,226,255)"
                        ],
                        [
                            0.5,
                            "rgb(109,196,255)"
                        ],
                        [
                            1,
                            "rgb(36,167,255)"
                        ]
                    ],
                    "max": 24000000000
                },
                "style": {
                    "fontFamily": "Microsoft YaHei",
                    "color": "rgba(102,102,102,1.0)",
                    "fontSize": "11pt",
                    "fontWeight": ""
                },
                "enabled": true
            },
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
        };
    }
});
BI.BubbleChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.bubble_chart', BI.BubbleChart);
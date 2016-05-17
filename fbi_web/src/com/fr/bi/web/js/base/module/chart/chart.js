/**
 * 图表控件
 * @class BI.Chart
 * @extends BI.Widget
 */
BI.Chart = BI.inherit(BI.Pane, {

    _defaultConfig: function () {
        return BI.extend(BI.Chart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart",
            items: [],
            config: {},
            chartType: BICst.WIDGET.AXIS
        })
    },

    _init: function () {
        BI.Chart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.setChartType(o.chartType);
        this.config.series = o.items;

        this.isInit = false;
        this.isSetOptions = false;
        this.wants2SetData = false;
        var width = 0;
        var height = 0;

        var setOptions = function () {
            self.vanCharts.setOptions(self.config);
            self.isSetOptions = true;
            if (self.wants2SetData === true) {
                self._setData();
            }
        };
        var init = function () {
            if (self.element.is(":visible")) {
                width = self.element.width();
                height = self.element.height();
                self.vanCharts = VanCharts.init(self.element[0]);
                BI.delay(setOptions, 1);
                self.isInit = true;
            }
        };
        BI.delay(init, 1);

        BI.Resizers.add(this.getName(), function () {
            if (self.element.is(":visible")) {
                var newW = self.element.width(), newH = self.element.height();
                if (width > 0 && height > 0 && (width !== newW || height !== newH)) {
                    self.vanCharts.resize();
                    width = newW;
                    height = newH;
                }
            }
        });
    },

    showTheOtherYAxis: function () {
        this.config.yAxis = BI.makeArray(2, this.config.yAxis[0]);
        this.config.yAxis[1].position = "right";
        switch (this.options.chartType){
            case BICst.WIDGET.COMPARE_AXIS:
                this.config.yAxis[1].reversed = true;
                break;
            default:
                return;
        }
    },

    hideTheOtherYAxis: function () {
        if (BI.has(this.config, "yAxis")) {
            this.config.yAxis = BI.makeArray(1, this.config.yAxis[0]);
        }
    },

    setChartType: function (type) {
        var self = this;
        this.options.chartType = type;
        this.config = this._createChartConfigByType();
    },

    _setData: function () {
        this.vanCharts.setData(this.config);
    },

    resize: function () {
        if (this.element.is(":visible") && this.isSetOptions === true) {
            this.vanCharts && this.vanCharts.resize();
        }
    },

    populate: function (items) {
        var self = this, o = this.options;
        o.items = items;
        this.config.series = o.items;
        if (this.element.is(":visible") && this.isSetOptions === true) {
            this._setData();
            this.wants2SetData = null;
        } else {
            this.wants2SetData = true;
        }
        this.loaded();
    },

    _createChartConfigByType: function () {
        var self = this;
        var defaultConfig = {};
        var columnConfig = {
            "plotOptions": {
                lineWidth: 2,
                click: function () {
                    self.fireEvent(BI.Chart.EVENT_CHANGE, {
                        category: this.category,
                        seriesName: this.seriesName,
                        value: this.value
                    });
                },
                "categoryGap": "20.0%",
                "borderColor": "rgb(255,255,0)",
                "borderWidth": 1,
                "gap": "20.0%",
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
            "xAxis": [
                {
                    "enableMinorTick": false,
                    "minorTickColor": "rgb(0,0,0)",
                    "tickColor": "rgb(0,0,0)",
                    "showArrow": false,
                    "lineColor": "rgb(0,0,0)",
                    "plotLines": [],
                    "type": "category",
                    "lineWidth": 1,
                    "showLabel": true,
                    "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
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
                "enabled": true
            },
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
                    "enabled": true
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
                "rgb(124,214,207)"
            ],
            "yAxis": [
                {
                    "enableMinorTick": true,
                    "minorTickColor": "rgb(176,176,176)",
                    "tickColor": "rgb(176,176,176)",
                    "showArrow": false,
                    "lineColor": "rgb(176,176,176)",
                    "plotLines": [],
                    "type": "value",
                    "lineWidth": 2,
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
                    "position": "left",
                    "labelRotation": 0,
                    "reversed": false
                }
            ],
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "column",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
        var lineConfig = {
            "plotOptions": {
                click: function () {
                    self.fireEvent(BI.Chart.EVENT_CHANGE, {
                        category: this.category,
                        seriesName: this.seriesName,
                        value: this.value
                    });
                },
                "large": false,
                "connectNulls": true,
                "curve": false,
                "marker": {
                    "symbol": "null_marker",
                    "radius": 4.5,
                    "enabled": true
                },
                "tooltip": {
                    "formatter": {
                        "identifier": "${CATEGORY}${SERIES}${VALUE}",
                        "valueFormat": "#.##",
                        "percentFormat": "#.##%"
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
            "xAxis": [
                {
                    "enableMinorTick": false,
                    "minorTickColor": "rgb(176,176,176)",
                    "tickColor": "rgb(176,176,176)",
                    "showArrow": false,
                    "lineColor": "rgb(176,176,176)",
                    "plotLines": [],
                    "type": "category",
                    "lineWidth": 1,
                    "showLabel": true,
                    "formatter": {},
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
                "enabled": true
            },
            "zoom": {
                "zoomType": "xy",
                "zoomTool": {
                    "visible": false,
                    "resize": true,
                    "from": "",
                    "to": ""
                }
            },
            "plotBorderColor": "blue",
            "tools": {
                "hidden": true,
                "toImage": {
                    "enabled": true
                },
                "sort": {
                    "enabled": true
                },
                "enabled": true,
                "fullScreen": {
                    "enabled": true
                }
            },
            "plotBorderWidth": 2,
            "colors": [
                "rgb(14,114,204)",
                "rgb(108,163,15)",
                "rgb(245,147,17)",
                "rgb(250,67,67)",
                "rgb(22,175,204)"
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
                    "formatter": {
                        "format": "#.##"
                    },
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
            "chartType": "line",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
        var areaConfig = {

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
        var bubbleConfig = {
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
        };
        var pieConfig = {
            "plotOptions": {
                click: function () {
                    self.fireEvent(BI.Chart.EVENT_CHANGE, {
                        category: this.seriesName,
                        seriesName: this.category,
                        value: this.value,
                        size: this.size
                    });
                },
                innerRadius: '0.0%',
                "dataLabels": {
                    "formatter": {
                        "identifier": "${CATEGORY}${SERIES}",
                        "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                        "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                    },
                    "connectorWidth": 1,
                    "align": "outside",
                    "enabled": true
                },
                "rotatable": false,
                "borderColor": "rgb(255,255,255)",
                "startAngle": 0,
                "borderRadius": 0,
                "borderWidth": 1,
                "tooltip": {
                    "formatter": {
                        "identifier": "${SERIES}${VALUE}",
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
                "endAngle": 360,
                "animation": true
            },
            "borderColor": "rgb(0,0,255)",
            "shadow": false,
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
                "enabled": true
            },
            "plotBorderColor": "rgb(238,238,238)",
            "tools": {
                "hidden": true,
                "toImage": {
                    "enabled": true
                },
                "sort": {
                    "enabled": true
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
                "rgb(124,214,207)"
            ],
            "chartType": "pie",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };

        var redarConfig = {
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
                "series": [{
                    "data": [{"x": "工作态度", "y": 5}, {"x": "业务能力", "y": 4}, {"x": "沟通能力", "y": 2}],
                    "name": "Jane"
                }, {
                    "data": [{"x": "工作态度", "y": 3}, {"x": "业务能力", "y": 3}, {"x": "沟通能力", "y": 2}],
                    "name": "Lucy"
                }, {"data": [{"x": "工作态度", "y": 2}, {"x": "业务能力", "y": 4}, {"x": "沟通能力", "y": 4}], "name": "Jack"}],
                "chartType": "radar",
                "style": "gradual",
                "plotShadow": false,
                "plotBorderRadius": 0
        };
        switch (this.options.chartType) {
            case BICst.WIDGET.AXIS:
                defaultConfig = columnConfig;
                break;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                defaultConfig = columnConfig;
                defaultConfig.plotOptions.stack = "stackedColumn";
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                defaultConfig = columnConfig;
                defaultConfig.plotOptions.stack = "stackedColumn";
                defaultConfig.plotOptions.stackByPercent = true;
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                defaultConfig = columnConfig;
                break;
            case BICst.WIDGET.FALL_AXIS:
                defaultConfig = columnConfig;
                defaultConfig.plotOptions.stack = "stackedFall";
                break;
            case BICst.WIDGET.BAR:
                defaultConfig = columnConfig;
                delete defaultConfig.xAxis;
                delete defaultConfig.yAxis;
                defaultConfig.chartType = "bar";
                break;
            case BICst.WIDGET.ACCUMULATE_BAR:
                defaultConfig = columnConfig;
                defaultConfig.plotOptions.stack = "stackedBar";
                delete defaultConfig.xAxis;
                delete defaultConfig.yAxis;
                defaultConfig.chartType = "bar";
                break;
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
                defaultConfig = lineConfig;
                break;
            case BICst.WIDGET.AREA:
                defaultConfig = areaConfig;
                break;
            case BICst.WIDGET.ACCUMULATE_AREA:
                defaultConfig = areaConfig;
                defaultConfig.plotOptions.stack = "stackedArea";
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                defaultConfig = areaConfig;
                defaultConfig.plotOptions.stack = "stackedArea";
                defaultConfig.plotOptions.stackByPercent = true;
                break;
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
                break;
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                break;
            case BICst.WIDGET.PIE:
                defaultConfig = pieConfig;
                break;
            case BICst.WIDGET.DONUT:
                defaultConfig = pieConfig;
                defaultConfig.plotOptions.innerRadius = "50.0%";
                defaultConfig.plotOptions.borderWidth = 10;
                break;
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DASHBOARD:
                defaultConfig = {
                    "plotOptions": {
                        click: function () {
                            self.fireEvent(BI.Chart.EVENT_CHANGE, {
                                category: this.seriesName,
                                seriesName: this.category,
                                value: this.value
                            });
                        },
                        "layout": "horizontal",
                        "hinge": "rgb(101,107,109)",
                        "valueLabel": {
                            "formatter": {
                                "identifier": "${SERIES}${VALUE}",
                                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                            },
                            "backgroundColor": "rgb(255,255,0)",
                            "style": {
                                "fontFamily": "Verdana",
                                "color": "rgba(51,51,51,1.0)",
                                "fontSize": "8pt",
                                "fontWeight": ""
                            },
                            "align": "inside",
                            "enabled": true
                        },
                        "hingeBackgroundColor": "rgb(220,242,249)",
                        "seriesLabel": {
                            "formatter": {
                                "identifier": "${CATEGORY}",
                                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                            },
                            "style": {
                                "fontFamily": "Verdana",
                                "color": "rgba(51,51,51,1.0)",
                                "fontSize": "10pt",
                                "fontWeight": ""
                            },
                            "align": "bottom",
                            "enabled": true
                        },
                        "style": "pointer",
                        "paneBackgroundColor": "rgb(252,252,252)",
                        "needle": "rgb(229,113,90)",
                        "animation": true
                    },
                    "borderColor": "rgb(238,238,238)",
                    "shadow": false,
                    "plotBorderColor": "rgba(255,255,255,0)",
                    "tools": {
                        "hidden": true,
                        "toImage": {
                            "enabled": true
                        },
                        "sort": {
                            "enabled": true
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
                        "rgb(248,203,127)"
                    ],
                    "yAxis": [
                        {
                            "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                            "minorTickColor": "rgb(226,226,226)",
                            "tickColor": "rgb(186,186,186)",
                            "labelStyle": {
                                "fontFamily": "Verdana",
                                "color": "rgba(102,102,102,1.0)",
                                "fontSize": "8pt",
                                "fontWeight": ""
                            },
                            "showLabel": true
                        }
                    ],
                    "borderRadius": 0,
                    "borderWidth": 0,
                    "chartType": "gauge",
                    "style": "gradual",
                    "plotShadow": false,
                    "plotBorderRadius": 0
                };
                break;
            case BICst.WIDGET.BUBBLE:
                defaultConfig = bubbleConfig;
                break;
            case BICst.WIDGET.FORCE_BUBBLE:
                defaultConfig = bubbleConfig;
                defaultConfig.plotOptions.force = true;
                break;
            case BICst.WIDGET.SCATTER:
                defaultConfig = {
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
                break;
            case BICst.WIDGET.RADAR:
                defaultConfig = redarConfig;
                break;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                defaultConfig = redarConfig;
                defaultConfig.plotOptions.stack = "stackRadar";
                break;
            case BICst.WIDGET.FUNNEL:
                defaultConfig = areaConfig;
                defaultConfig.chartType = "funnel";
                break
        }
        //todo 将options中的config与defaultConfig校验起来
        return defaultConfig;
    }
});
BI.Chart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.chart', BI.Chart);
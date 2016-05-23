BI.ChartCombineFormatItemFactory = {
    combineItems: function(types, items){
        var calItems = BI.values(items);
        return BI.map(calItems, function(idx, item){
            return BI.ChartCombineFormatItemFactory.formatItems(types[idx], item);
        });
    },
    formatItems: function(type, items){
        var item = {};
        switch (type) {
            case BICst.WIDGET.BAR:
                item = BI.extend({"type": "bar"}, items);
                break;
            case BICst.WIDGET.BUBBLE:
                item = BI.extend({"type": "bubble"}, items);
                break;
            case BICst.WIDGET.SCATTER:
                item = BI.extend({"type": "scatter"}, items);
                break;
            case BICst.WIDGET.AXIS:
                item = BI.extend({"type": "column"}, items);
                break;
            case BICst.WIDGET.LINE:
                item = BI.extend({"type": "line"}, items);
                break;
            case BICst.WIDGET.AREA:
                item = BI.extend({"type": "area"}, items);
                break;
            case BICst.WIDGET.DONUT:
                item = BI.extend({"type": "pie"}, items);
                break;
            case BICst.WIDGET.RADAR:
                item = BI.extend({"type": "radar"}, items);
                break;
            case BICst.WIDGET.PIE:
                item = BI.extend({"type": "pie"}, items);
                break;
            case BICst.WIDGET.DASHBOARD:
                item = BI.extend({"type": "gauge"}, items);
                break;
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                item = {};
                break;
            default:
                item = BI.extend({"type": "column"}, items);
                break;
        }
        item.data = BI.map(item.data, function(idx, it){
            if(BI.has(it, "color") || BI.has(item, "color")){
                it.color = BI.has(it, "color") ? it.color : item.color;
            }
            if(BI.has(it, "borderColor") || BI.has(item, "borderColor")){
                it.borderColor = BI.has(it, "borderColor") ? it.borderColor : item.borderColor;
            }
            if(BI.has(it, "clickColor") || BI.has(item, "clickColor")){
                it.clickColor = BI.has(it, "clickColor") ? it.clickColor : item.clickColor;
            }
            if(BI.has(it, "mouseOverColor") || BI.has(item, "mouseOverColor")){
                it.mouseOverColor = BI.has(it, "mouseOverColor") ? it.mouseOverColor : item.mouseOverColor;
            }
            if(BI.has(it, "tooltip") || BI.has(item, "tooltip")){
                it.tooltip = BI.has(it, "tooltip") ? it.tooltip : item.tooltip;
            }
            if(BI.has(it, "dataLabels") || BI.has(item, "dataLabels")){
                it.dataLabels = BI.has(it, "dataLabels") ? it.dataLabels : item.dataLabels;
            }
            return it;
        });
        return item;
    },


    combineConfig: function(type){
        switch (type) {
            case BICst.WIDGET.BAR:
                return BI.extend({"chartType": "bar"}, BI.ChartCombineFormatItemFactory.AxisConfig());
            case BICst.WIDGET.BUBBLE:
                return BI.extend({"chartType": "bubble"}, BI.ChartCombineFormatItemFactory.BubbleConfig());
            case BICst.WIDGET.SCATTER:
                return BI.extend({"chartType": "scatter"}, BI.ChartCombineFormatItemFactory.BubbleConfig());
            case BICst.WIDGET.AXIS:
                return BI.extend({"chartType": "column"}, BI.ChartCombineFormatItemFactory.AxisConfig());
            case BICst.WIDGET.LINE:
                return BI.extend({"chartType": "line"}, BI.ChartCombineFormatItemFactory.AxisConfig());
            case BICst.WIDGET.AREA:
                return BI.extend({"chartType": "area"}, BI.ChartCombineFormatItemFactory.AxisConfig());
            case BICst.WIDGET.DONUT:
                var config = BI.ChartCombineFormatItemFactory.AxisConfig();
                config.plotOptions.innerRadius = "50.0%";
                config.plotOptions.borderWidth = 10;
                return BI.extend({"chartType": "pie"}, config);
            case BICst.WIDGET.RADAR:
                return BI.extend({"chartType": "radar"}, BI.ChartCombineFormatItemFactory.AxisConfig());
            case BICst.WIDGET.PIE:
                return BI.extend({"chartType": "pie"}, BI.ChartCombineFormatItemFactory.AxisConfig());
            case BICst.WIDGET.DASHBOARD:
                return BI.extend({"chartType": "gauge"}, BI.ChartCombineFormatItemFactory.DashBoardConfig());
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return {};
            default:
                return BI.extend({"chartType": "column"}, BI.ChartCombineFormatItemFactory.AxisConfig());
        }
    },

    AxisConfig: function (){
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
                "animation": true,
                "lineWidth": 2
            },
            "borderColor": "rgb(238,238,238)",
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
            "borderRadius": 0,
            "borderWidth": 0,
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        }
    },

    BubbleConfig: function(){
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
                "maxSize": 70,
                "lineWidth": 0,
                "animation": true,
                "fillColorOpacity": 0.5,
                "marker": {"symbol": "circle", "radius": 4.5, "enabled": true},
                "step": false,
                "force": false,
                "minSize": 15,
                "displayNegative": true
            },
            "borderColor": "rgb(238,238,238)",
            "shadow": false,
            "legend": {
                "borderColor": "rgb(204,204,204)",
                "borderRadius": 0,
                "shadow": false,
                "borderWidth": 0,
                "style": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "9pt", "fontWeight": ""},
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
            "colors": ["rgb(249,120,147)", "rgb(98,179,240)"],
            "borderRadius": 0,
            "borderWidth": 0,
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        }
    },

    DashBoardConfig: function () {
        return {
            "plotOptions": {
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
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "gauge",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
    }
};
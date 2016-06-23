/**
 * 图表控件
 * @class BI.FallAxisChart
 * @extends BI.Widget
 */
BI.FallAxisChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.FallAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fall-axis-chart"
        })
    },

    _init: function () {
        BI.FallAxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.FallAxisChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.FallAxisChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.FallAxisChart.EVENT_CHANGE, obj);
        });
    },

    setTypes: function(types){
    },

    populate: function (items) {
        var self = this;
        this.FallAxisChart.populate(BI.FallAxisChart.formatItems(items));
    },

    resize: function () {
        this.FallAxisChart.resize();
    }
});
BI.extend(BI.FallAxisChart, {
    formatItems: function (item) {
        var tables = [], sum = 0;
        var name = BI.keys(item)[0];
        BI.each(item[name], function(i, t){
            if(t.y < 0){
                tables.push([t.x, t.y, sum + t.y, t.options]);
            }else{
                tables.push([t.x, t.y, sum, t.options]);
            }
            sum += t.y;
        });

        return BI.map(BI.makeArray(2, null), function(idx, item){
            return {
                "data": BI.map(tables, function(id, cell){
                    var axis = BI.extend({options: cell[3]}, {
                        x: cell[0],
                        y: Math.abs(cell[2 - idx])
                    });
                    if(idx === 1){
                        axis.color = cell[2 - idx] < 0 ? "rgb(152, 118, 170)" : "rgb(0, 157, 227)";
                    }else{
                        axis.color = "rgba(0,0,0,0)";
                        axis.borderColor = "rgba(0,0,0,0)";
                        axis.borderWidth = 0;
                        axis.clickColor = "rgba(0,0,0,0)";
                        axis.mouseOverColor = "rgba(0,0,0,0)";
                        axis.dataLabel = {
                            enable: false
                        };
                        axis.tooltip = {
                            enable: false
                        }
                    }
                    return axis;
                })
            };
        });
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
                "animation": true,
                stack: "stackedFall"
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
                "enabled": false
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
BI.FallAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.fall_axis_chart', BI.FallAxisChart);
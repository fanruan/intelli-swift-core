/**
 * 图表控件
 * @class BI.CompareAreaChart
 * @extends BI.Widget
 */
BI.CompareAreaChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CompareAreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-compare-area-chart"
        })
    },

    _init: function () {
        BI.CompareAreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.CompareAreaChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.CompareAreaChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CompareAreaChart.EVENT_CHANGE, obj);
        });
    },

    _formatItems: function (items) {
        var result = [], o = this.options;
        BI.each(items, function(i, belongAxisItems){
            BI.each(belongAxisItems, function(j, axisItems){
                var name = BI.keys(axisItems)[0];
                result.push({
                    "data": axisItems[name],
                    "name": name,
                    "yAxis": i
                });
            });
        });
        return result;
    },

    setTypes: function(types){
    },

    populate: function (items) {
        var self = this;
        var config = BI.CompareAreaChart.formatConfig();
        config.plotOptions.click = function(){
            self.fireEvent(BI.CompareAreaChart.EVENT_CHANGE, {category: this.category,
                seriesName: this.seriesName,
                value: this.value,
                options: this.pointOption.options});
        };
        this.CompareAreaChart.populate(this._formatItems(items), config);
    },

    resize: function () {
        this.CompareAreaChart.resize();
    }
});
BI.extend(BI.CompareAreaChart, {
    formatConfig: function(){
        return {
            "plotOptions": {
                "fillColor": true,
                "fillColorOpacity": 1,
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
                    "shared": true,
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
                "stack": "stackedArea"
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
                "style": {"fontFamily": "微软雅黑", "color": "rgba(102,102,102,1.0)", "fontSize": "10pt", "fontWeight": ""},
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
            }, {
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
                "gridLineWidth": 0,
                "enableTick": false,
                "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "9pt", "fontWeight": ""},
                "plotBands": [],
                "position": "right",
                "labelRotation": 0,
                "reversed": true
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
BI.CompareAreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.compare_area_chart', BI.CompareAreaChart);
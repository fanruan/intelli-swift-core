/**
 * 图表控件
 * @class BI.CompareBarChart
 * @extends BI.Widget
 */
BI.CompareBarChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CompareBarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-compare-bar-chart"
        })
    },

    _init: function () {
        BI.CompareBarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.CompareBarChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.CompareBarChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CompareBarChart.EVENT_CHANGE, obj);
        });
    },

    _formatItems: function (items) {
        var result = [], o = this.options;
        var re = BI.any(items, function(idx, item){
            return BI.isEmptyArray(item);
        });
        if(re === true){
            return [];
        }
        BI.each(items, function(i, belongAxisItems){
            BI.each(belongAxisItems, function(j, axisItems){
                var name = BI.keys(axisItems)[0];
                result.push({
                    "data": BI.map(axisItems[name], function(idx, item){
                        if(i === 0){
                            return BI.extend({options: item.options}, {
                                y: item.x,
                                x: -item.y
                            });
                        }else{
                            return BI.extend({options: item.options}, {
                                y: item.x,
                                x: item.y
                            });
                        }
                    }),
                    "name": name
                });
            });
        });
        return result;
    },

    setTypes: function(types){
    },

    populate: function (items) {
        var self = this;
        var config = BI.CompareBarChart.formatConfig();
        config.plotOptions.click = function(){
            self.fireEvent(BI.CompareBarChart.EVENT_CHANGE, {category: this.category,
                seriesName: this.seriesName,
                value: this.value,
                options: this.pointOption.options});
        };
        this.CompareBarChart.populate(this._formatItems(items), config);
    },

    resize: function () {
        this.CompareBarChart.resize();
    }
});
BI.extend(BI.CompareBarChart, {
    formatConfig: function () {
        return {
            "plotOptions": {
                "categoryGap": "20.0%",
                "borderColor": "rgb(255,255,255)",
                "borderWidth": 1,
                "gap": "0.0%",
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
            "xAxis": [{
                "enableMinorTick": false,
                "gridLineColor": "rgb(196,196,196)",
                "minorTickColor": "rgb(176,176,176)",
                "tickColor": "rgb(176,176,176)",
                "showArrow": false,
                "lineColor": "rgb(176,176,176)",
                "plotLines": [],
                "type": "value",
                "lineWidth": 2,
                "showLabel": true,
                "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                "gridLineWidth": 1,
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
                "position": "top",
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
            "colors": ["rgb(99,178,238)", "rgb(118,218,145)", "rgb(248,203,127)"],
            "yAxis": [{
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
                "position": "left",
                "labelRotation": 0,
                "reversed": false
            }],
            "borderRadius": 0,
            "borderWidth": 0,
            "chartType": "bar",
            "style": "gradual",
            "plotShadow": false,
            "plotBorderRadius": 0
        };
    }
});
BI.CompareBarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.compare_bar_chart', BI.CompareBarChart);
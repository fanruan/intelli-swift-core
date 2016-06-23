/**
 * 图表控件
 * @class BI.PieChart
 * @extends BI.Widget
 */
BI.PieChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PieChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-pie-chart"
        })
    },

    _init: function () {
        BI.PieChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.PieChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.PieChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.PieChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.PieChart.populate([BI.PieChart.formatItems(items)]);
    },

    resize: function () {
        this.PieChart.resize();
    }
});
BI.extend(BI.PieChart, {
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
    }
});
BI.PieChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.pie_chart', BI.PieChart);
/**
 * 图表控件
 * @class BI.DashboardChart
 * @extends BI.Widget
 */
BI.DashboardChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DashboardChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-chart"
        })
    },

    _init: function () {
        BI.DashboardChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.DashboardChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.DashboardChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.DashboardChart.EVENT_CHANGE, obj);
        });
    },

    populate: function (items) {
        this.DashboardChart.populate([BI.DashboardChart.formatItems(items)]);
    },

    resize: function () {
        this.DashboardChart.resize();
    }
});
BI.extend(BI.DashboardChart, {
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
    }
});
BI.DashboardChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.dashboard_chart', BI.DashboardChart);
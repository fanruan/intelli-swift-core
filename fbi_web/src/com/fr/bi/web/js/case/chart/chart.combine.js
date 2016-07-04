/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CombineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart",
            items: [],
            xAxis: [{type: "category"}],
            yAxis: [{type: "value"}],
            types: [[], []],
            formatConfig: function(config){return config;}
        })
    },

    _init: function () {
        BI.CombineChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //图可配置属性
        this.CombineChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.CombineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, obj);
        });

        if (BI.isNotEmptyArray(o.items)) {
            this.populate(o.items);
        }
    },

    _formatItems: function (items) {
        var result = [], self = this, o = this.options;
        var yAxisIndex = 0;
        BI.each(items, function (i, belongAxisItems) {
            var combineItems = BI.ChartCombineFormatItemFactory.combineItems(o.types[i], belongAxisItems);
            BI.each(combineItems, function (j, axisItems) {
                if (BI.isArray(axisItems)) {
                    result = BI.concat(result, axisItems);
                } else {
                    result.push(BI.extend(axisItems, {"yAxis": yAxisIndex}));
                }
            });
            if (BI.isNotEmptyArray(combineItems)) {
                yAxisIndex++;
            }
        });
        var config = BI.ChartCombineFormatItemFactory.combineConfig();
        config.plotOptions.click = function () {
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, BI.extend(this.pointOption, {
                category: this.category
            }));
        };
        return [result, config];
    },

    setTypes: function (types) {
        this.options.types = types || [[]];
    },

    populate: function (items, types) {
        var o = this.options;
        if (BI.isNotNull(types)) {
            this.setTypes(types);
        }
        var opts = this._formatItems(items);
        BI.extend(opts[1], {
            xAxis: o.xAxis,
            yAxis: o.yAxis
        });
        var result = o.formatConfig(opts[1], opts[0]);
        this.CombineChart.populate(result[0], result[1]);
    },

    resize: function () {
        this.CombineChart.resize();
    },

    magnify: function(){
        this.CombineChart.magnify();
    }
});
BI.CombineChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.combine_chart', BI.CombineChart);

var a = {
    "plotOptions": {
        "rotatable": false,
        "startAngle": 0,
        "borderRadius": 0,
        "endAngle": 360,
        "innerRadius": "0.0%",
        "layout": "horizontal",
        "hinge": "rgb(101,107,109)",
        "dataLabels": {
            "style": "{fontFamily:Microsoft YaHei, color: #808080, fontSize: 12pt}",
            "formatter": {
                "identifier": "${VALUE}",
                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "xFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "yFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}"
            },
            "align": "outside",
            "enabled": false
        },
        "percentageLabel": {
            "formatter": {
                "identifier": "${PERCENT}",
                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
            },
            "style": {"fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"},
            "align": "bottom",
            "enabled": true
        },
        "valueLabel": {
            "formatter": {
                "identifier": "${SERIES}${VALUE}",
                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
            },
            "backgroundColor": "rgb(255,255,0)",
            "style": {"fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"},
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
            "style": {"fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"},
            "align": "bottom",
            "enabled": true
        },
        "style": "pointer",
        "paneBackgroundColor": "rgb(252,252,252)",
        "needle": "rgb(229,113,90)",
        "large": false,
        "connectNulls": false,
        "shadow": true,
        "curve": false,
        "sizeBy": "area",
        "tooltip": {
            "formatter": "function(){var tip = this.name; BI.each(this.points, function(idx, point){tip += ('<div>' + point.seriesName + ':' + (point.size || point.y) + '</div>');});return tip; }",
            "shared": true,
            "padding": 5,
            "backgroundColor": "rgba(0,0,0,0.4980392156862745)",
            "borderColor": "rgb(0,0,0)",
            "shadow": false,
            "borderRadius": 2,
            "borderWidth": 0,
            "follow": false,
            "enabled": true,
            "animation": true,
            "style": {
                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                "color": "#c4c6c6",
                "fontSize": "12px",
                "fontWeight": ""
            }
        },
        "maxSize": 70,
        "fillColorOpacity": 0.5,
        "marker": {"symbol": "circle", "radius": 4.5, "enabled": true},
        "step": false,
        "force": false,
        "minSize": 15,
        "displayNegative": true,
        "categoryGap": "16.0%",
        "borderColor": "rgb(255,255,255)",
        "borderWidth": 1,
        "gap": "22.0%",
        "animation": true,
        "lineWidth": 2,
        "bubble": {
            "large": false,
            "connectNulls": false,
            "shadow": true,
            "curve": false,
            "sizeBy": "area",
            "maxSize": 70,
            "minSize": 15,
            "lineWidth": 0,
            "animation": true,
            "fillColorOpacity": 0.699999988079071,
            "marker": {"symbol": "circle", "radius": 28.39695010101295, "enabled": true}
        }
    },
    "dataSheet": {
        "enabled": false,
        "borderColor": "rgb(0,0,0)",
        "borderWidth": 1,
        "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
        "style": {"fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"}
    },
    "borderColor": "rgb(238,238,238)",
    "shadow": false,
    "rangeLegend": {
        "range": {
            "min": 0,
            "color": [[0, "rgb(182,226,255)"], [0.5, "rgb(109,196,255)"], [1, "rgb(36,167,255)"]],
            "max": 88853900
        }, "enabled": true, "position": "right"
    },
    "zoom": {"zoomType": "xy", "zoomTool": {"visible": false, "resize": true, "from": "", "to": ""}},
    "plotBorderColor": "rgba(255,255,255,0)",
    "tools": {
        "hidden": true,
        "toImage": {"enabled": true},
        "sort": {"enabled": true},
        "enabled": false,
        "fullScreen": {"enabled": true}
    },
    "plotBorderWidth": 0,
    "colors": ["rgb(99,178,238)", "rgb(118,218,145)"],
    "borderRadius": 0,
    "borderWidth": 0,
    "style": "normal",
    "plotShadow": false,
    "plotBorderRadius": 0,
    "geo": {"data": "http://localhost:8080/WebReport/ReportServer?op=resource&resource=/com/fr/bi/web/js/data/map/china.json"},
    "chartType": "areaMap",

}
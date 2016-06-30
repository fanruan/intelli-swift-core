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
            "style": "{color: #d4dadd, fontSize: 9pt}",
            "formatter": {
                "identifier": "${X}${Y}",
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
            "style": {"fontFamily": "Verdana", "color": "rgba(51,51,51,1.0)", "fontSize": "14pt", "fontWeight": "bold"},
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
            "style": {"fontFamily": "Verdana", "color": "rgba(51,51,51,1.0)", "fontSize": "8pt", "fontWeight": ""},
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
            "style": {"fontFamily": "Verdana", "color": "rgba(51,51,51,1.0)", "fontSize": "10pt", "fontWeight": ""},
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
            "formatter": "function(){ return this.seriesName+'<div>(X)计算指标1:'+ this.x +'</div><div>(Y)计算指标:'+ this.y +'</div>'}",
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
        "style": {"fontFamily": "宋体", "color": "rgba(0,0,0,1.0)", "fontSize": "9pt", "fontWeight": ""}
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
        "enabled": false
    },
    "rangeLegend": {
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
        "enabled": false,
        "fullScreen": {"enabled": true}
    },
    "plotBorderWidth": 0,
    "colors": ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"],
    "borderRadius": 0,
    "borderWidth": 0,
    "style": "normal",
    "plotShadow": false,
    "plotBorderRadius": 0,
    "xAxis": [{
        "type": "value",
        "title": {
            "style": {
                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                "color": "#808080",
                "fontSize": "12pt",
                "fontWeight": ""
            }, "text": "", "align": "center"
        },
        "labelStyle": {"fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"},
        "position": "bottom",
        "gridLineWidth": 1,
        "plotLines": [],
        "formatter": "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '#.##') : (-1) * arguments[0];}"
    }],
    "yAxis": [{
        "type": "value",
        "title": {
            "style": {
                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                "color": "#808080",
                "fontSize": "12pt",
                "fontWeight": ""
            }, "text": "", "rotation": -90
        },
        "labelStyle": {"fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"},
        "position": "left",
        "gridLineWidth": 1,
        "plotLines": [],
        "formatter": "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '#.##') : (-1) * arguments[0];}"
    }],
    "chartType": "scatter",
    "series": [{
        "type": "scatter",
        "name": "东城汇总",
        "data": [{"x": -0.0002, "y": -2, "seriesName": "东城汇总", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "洪泽支行(汇总)",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 30,
            "seriesName": "洪泽支行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "连云港灌云汇总",
        "data": [{
            "x": 0.0008633333333333334,
            "y": 259,
            "seriesName": "连云港灌云汇总",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "连云港民主汇总",
        "data": [{
            "x": -0.000051666236114699044,
            "y": -1,
            "seriesName": "连云港民主汇总",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行(南京地区汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行(南京地区汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行（全行汇总）",
        "data": [{
            "x": -0.00002857142857142857,
            "y": -1,
            "seriesName": "某银行（全行汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宝应支行",
        "data": [{
            "x": -0.00035714285714285714,
            "y": -5,
            "seriesName": "某银行宝应支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京安定门支行",
        "data": [{
            "x": 0.0006603773584905661,
            "y": 7,
            "seriesName": "某银行北京安定门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京朝阳门支行",
        "data": [{
            "x": -0.00034094783498124785,
            "y": -2,
            "seriesName": "某银行北京朝阳门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京德胜支行",
        "data": [{
            "x": 0.0007550018875047187,
            "y": 2,
            "seriesName": "某银行北京德胜支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京东三环支行",
        "data": [{
            "x": 0.000225,
            "y": 45,
            "seriesName": "某银行北京东三环支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京东四环支行",
        "data": [{
            "x": 0.00015,
            "y": 9,
            "seriesName": "某银行北京东四环支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京东直门支行",
        "data": [{
            "x": 0.00024695142720893796,
            "y": 29,
            "seriesName": "某银行北京东直门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京分行(汇总)",
        "data": [{
            "x": 0.0009666666666666667,
            "y": 58,
            "seriesName": "某银行北京分行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京分行清算中心",
        "data": [{
            "x": 0.00018734193024635463,
            "y": 6,
            "seriesName": "某银行北京分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京分行银行卡部",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -2,
            "seriesName": "某银行北京分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京分行营业部",
        "data": [{
            "x": 0.0011,
            "y": 44,
            "seriesName": "某银行北京分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京光华路支行",
        "data": [{
            "x": 0.00047337278106508875,
            "y": 2,
            "seriesName": "某银行北京光华路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京马连道支行",
        "data": [{
            "x": 0.0008,
            "y": 112,
            "seriesName": "某银行北京马连道支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京上地支行",
        "data": [{
            "x": 0.0003701475963540462,
            "y": 8,
            "seriesName": "某银行北京上地支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京石景山支行",
        "data": [{
            "x": 0.0005438723712835388,
            "y": 3,
            "seriesName": "某银行北京石景山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京通州支行",
        "data": [{
            "x": -0.00006,
            "y": -3,
            "seriesName": "某银行北京通州支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京望京支行",
        "data": [{
            "x": 0.001,
            "y": 10,
            "seriesName": "某银行北京望京支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京西三环支行",
        "data": [{
            "x": 0.00009228497600590623,
            "y": 1,
            "seriesName": "某银行北京西三环支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京宣武门支行",
        "data": [{
            "x": 0.0007604562737642585,
            "y": 1,
            "seriesName": "某银行北京宣武门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京亚运村支行",
        "data": [{
            "x": -0.0003333333333333333,
            "y": -30,
            "seriesName": "某银行北京亚运村支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京亦庄支行",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行北京亦庄支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京中关村西区支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行北京中关村西区支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京中关村支行",
        "data": [{
            "x": 0.0004927827854546948,
            "y": 58,
            "seriesName": "某银行北京中关村支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行北京总部基地支行",
        "data": [{
            "x": 0.00088,
            "y": 44,
            "seriesName": "某银行北京总部基地支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行滨海支行",
        "data": [{
            "x": 0.0008666666666666666,
            "y": 26,
            "seriesName": "某银行滨海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常熟梅李支行",
        "data": [{
            "x": 0.00034484946631096596,
            "y": 128,
            "seriesName": "某银行常熟梅李支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常熟招商城支行",
        "data": [{
            "x": 0.00108,
            "y": 54,
            "seriesName": "某银行常熟招商城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常熟支行",
        "data": [{
            "x": 0.0005251457279395032,
            "y": 30,
            "seriesName": "某银行常熟支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行常州(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州北大街支行",
        "data": [{
            "x": -0.00026265102433899493,
            "y": -3,
            "seriesName": "某银行常州北大街支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州博爱路(汇总)",
        "data": [{
            "x": 0.000468384074941452,
            "y": 1,
            "seriesName": "某银行常州博爱路(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州博爱路支行",
        "data": [{
            "x": -0.7526546458804458,
            "y": 61798,
            "seriesName": "某银行常州博爱路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州长虹路支行",
        "data": [{
            "x": 0.01737242128121607,
            "y": 32,
            "seriesName": "某银行常州长虹路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州朝阳支行",
        "data": [{
            "x": 0.0008,
            "y": 8,
            "seriesName": "某银行常州朝阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州春江支行",
        "data": [{
            "x": 0.0002277644915157727,
            "y": 2,
            "seriesName": "某银行常州春江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州大学城科技支行",
        "data": [{
            "x": -0.0004,
            "y": -4,
            "seriesName": "某银行常州大学城科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州分行国际业务部",
        "data": [{
            "x": -0.000475,
            "y": -19,
            "seriesName": "某银行常州分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州分行清算中心",
        "data": [{
            "x": -0.00005836689429755443,
            "y": -2,
            "seriesName": "某银行常州分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州分行银行卡部",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行常州分行银行卡部", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州分行营业部",
        "data": [{
            "x": 0.00034000113333711114,
            "y": 93,
            "seriesName": "某银行常州分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州广化街支行",
        "data": [{
            "x": 0.0012111628845862701,
            "y": 72,
            "seriesName": "某银行常州广化街支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州横林支行",
        "data": [{
            "x": -0.00005,
            "y": -1,
            "seriesName": "某银行常州横林支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州横山桥支行",
        "data": [{
            "x": 0.0008472729913578154,
            "y": 40,
            "seriesName": "某银行常州横山桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州红梅支行",
        "data": [{
            "x": 0.0007687576875768758,
            "y": 5,
            "seriesName": "某银行常州红梅支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州虹桥支行",
        "data": [{
            "x": 0.001616,
            "y": 3232,
            "seriesName": "某银行常州虹桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州怀德支行",
        "data": [{
            "x": 0.0002,
            "y": 2,
            "seriesName": "某银行常州怀德支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州经济开发区支行",
        "data": [{
            "x": -0.00025004673203487057,
            "y": -96,
            "seriesName": "某银行常州经济开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州兰陵(汇总)",
        "data": [{
            "x": 0.00072007200720072,
            "y": 4,
            "seriesName": "某银行常州兰陵(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州兰陵支行",
        "data": [{
            "x": 0.0004917387883556255,
            "y": 5,
            "seriesName": "某银行常州兰陵支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州龙城大道(汇总)",
        "data": [{
            "x": 0.018590178952189913,
            "y": 1177,
            "seriesName": "某银行常州龙城大道(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州龙城大道支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 6,
            "seriesName": "某银行常州龙城大道支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州勤业支行",
        "data": [{
            "x": 0.0005610471257464499,
            "y": 2029,
            "seriesName": "某银行常州勤业支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州清凉路(汇总)",
        "data": [{
            "x": 0.00106,
            "y": 53,
            "seriesName": "某银行常州清凉路(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州清凉路支行",
        "data": [{
            "x": 0.00088,
            "y": 22,
            "seriesName": "某银行常州清凉路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州清潭支行",
        "data": [{
            "x": -0.0006551142854860229,
            "y": -80,
            "seriesName": "某银行常州清潭支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州三井社区支行",
        "data": [{
            "x": 0.0009,
            "y": 45,
            "seriesName": "某银行常州三井社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州天宁(汇总)",
        "data": [{
            "x": 0.0006983782105998293,
            "y": 9,
            "seriesName": "某银行常州天宁(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州天宁支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行常州天宁支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州文化宫科技支行",
        "data": [{
            "x": 0.00037544890630101206,
            "y": 24,
            "seriesName": "某银行常州文化宫科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州武进(汇总)",
        "data": [{
            "x": -0.000075,
            "y": -3,
            "seriesName": "某银行常州武进(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州武进支行",
        "data": [{
            "x": -0.00015158405335758679,
            "y": -4,
            "seriesName": "某银行常州武进支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州西新桥支行",
        "data": [{
            "x": -0.0005633802816901409,
            "y": -4,
            "seriesName": "某银行常州西新桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州新北(汇总)",
        "data": [{
            "x": 0.00112,
            "y": 56,
            "seriesName": "某银行常州新北(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州新北支行",
        "data": [{
            "x": 0.000475,
            "y": 19,
            "seriesName": "某银行常州新北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州新丰街支行",
        "data": [{
            "x": 0.001191636875744773,
            "y": 11,
            "seriesName": "某银行常州新丰街支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州新天地社区支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行常州新天地社区支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州薛家支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行常州薛家支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州营业部(汇总)",
        "data": [{
            "x": 0.00106,
            "y": 53,
            "seriesName": "某银行常州营业部(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州钟楼(汇总)",
        "data": [{
            "x": 0.00015304560759106213,
            "y": 1,
            "seriesName": "某银行常州钟楼(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州钟楼支行",
        "data": [{
            "x": 0.0010683983474487034,
            "y": 11374,
            "seriesName": "某银行常州钟楼支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州珠江路支行",
        "data": [{
            "x": 0.00016989466530750936,
            "y": 1,
            "seriesName": "某银行常州珠江路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行常州邹区支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行常州邹区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行城北支行（汇总）",
        "data": [{
            "x": 0.0005324298160696999,
            "y": 11,
            "seriesName": "某银行城北支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行大丰港城支行",
        "data": [{
            "x": 0.00038577589176230694,
            "y": 28,
            "seriesName": "某银行大丰港城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行大丰支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 3,
            "seriesName": "某银行大丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行大丰支行(汇总)",
        "data": [{
            "x": 0.0000322375061065276,
            "y": 13,
            "seriesName": "某银行大丰支行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行丹阳丹金路支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行丹阳丹金路支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行丹阳后巷支行",
        "data": [{
            "x": -0.00013399534275646528,
            "y": -34,
            "seriesName": "某银行丹阳后巷支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行丹阳新市口支行",
        "data": [{
            "x": 0.00046425255338904364,
            "y": 3,
            "seriesName": "某银行丹阳新市口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行丹阳支行",
        "data": [{
            "x": 0.00019073049780659929,
            "y": 2,
            "seriesName": "某银行丹阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行丹阳支行（汇总）",
        "data": [{
            "x": 0.0007649646203863071,
            "y": 12,
            "seriesName": "某银行丹阳支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行东海牛山南路社区支行",
        "data": [{
            "x": 0.0006453694740238787,
            "y": 10,
            "seriesName": "某银行东海牛山南路社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行东海支行",
        "data": [{
            "x": 0.000384172109104879,
            "y": 1,
            "seriesName": "某银行东海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行东台金地支行",
        "data": [{
            "x": 0.000005564118115099347,
            "y": 4,
            "seriesName": "某银行东台金地支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行东台支行",
        "data": [{
            "x": -0.00044166666666666665,
            "y": -53,
            "seriesName": "某银行东台支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行丰县支行",
        "data": [{
            "x": 0.00085,
            "y": 17,
            "seriesName": "某银行丰县支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行阜宁新城支行",
        "data": [{
            "x": 0.00009228497600590623,
            "y": 1,
            "seriesName": "某银行阜宁新城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行阜宁支行",
        "data": [{
            "x": 0.0007,
            "y": 28,
            "seriesName": "某银行阜宁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行赣榆支行",
        "data": [{
            "x": -0.00017358097552508245,
            "y": -1,
            "seriesName": "某银行赣榆支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行高淳支行",
        "data": [{
            "x": 0.00044508734839212194,
            "y": 4,
            "seriesName": "某银行高淳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行高邮支行",
        "data": [{"x": 0.001, "y": 10, "seriesName": "某银行高邮支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行灌南支行",
        "data": [{
            "x": 0.00013489098355965042,
            "y": 34,
            "seriesName": "某银行灌南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行灌云江山花园社区支行",
        "data": [{
            "x": 0.0003333333333333333,
            "y": 1,
            "seriesName": "某银行灌云江山花园社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行灌云支行",
        "data": [{
            "x": -0.0002666666666666667,
            "y": -8,
            "seriesName": "某银行灌云支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行广陵(汇总)",
        "data": [{
            "x": 0.00014765051123989516,
            "y": 8,
            "seriesName": "某银行广陵(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行海安城区支行",
        "data": [{
            "x": 0.00045167118337850043,
            "y": 1,
            "seriesName": "某银行海安城区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行海安支行",
        "data": [{
            "x": 0.0003479229002852968,
            "y": 5,
            "seriesName": "某银行海安支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行海门城中支行",
        "data": [{
            "x": 0.0007,
            "y": 7,
            "seriesName": "某银行海门城中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行海门三星支行",
        "data": [{
            "x": -0.0007368421052631579,
            "y": -28,
            "seriesName": "某银行海门三星支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行海门支行",
        "data": [{
            "x": -0.00008,
            "y": -4,
            "seriesName": "某银行海门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行邗江(汇总)",
        "data": [{
            "x": -0.00007201180064346029,
            "y": -28,
            "seriesName": "某银行邗江(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州滨江小微企业专营支行",
        "data": [{
            "x": 0.00034526412705719877,
            "y": 3,
            "seriesName": "某银行杭州滨江小微企业专营支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州城东小微企业专营支行",
        "data": [{
            "x": 0.0011,
            "y": 55,
            "seriesName": "某银行杭州城东小微企业专营支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州分行(汇总)",
        "data": [{
            "x": 0.0008666666666666666,
            "y": 26,
            "seriesName": "某银行杭州分行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州分行清算中心",
        "data": [{
            "x": 0.000484027105517909,
            "y": 5,
            "seriesName": "某银行杭州分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州分行银行卡部",
        "data": [{
            "x": 0.0007655990812811025,
            "y": 6,
            "seriesName": "某银行杭州分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州分行营业部",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行杭州分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州富阳支行",
        "data": [{
            "x": -0.00018,
            "y": -9,
            "seriesName": "某银行杭州富阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州拱墅小微企业专营支行",
        "data": [{
            "x": -0.0004,
            "y": -4,
            "seriesName": "某银行杭州拱墅小微企业专营支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州上城小微企业专营支行",
        "data": [{
            "x": 0.00023386342376052386,
            "y": 1,
            "seriesName": "某银行杭州上城小微企业专营支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州西溪小微企业专营支行",
        "data": [{
            "x": -0.0006666666666666666,
            "y": -20,
            "seriesName": "某银行杭州西溪小微企业专营支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州萧山支行",
        "data": [{
            "x": 0.0006666666666666666,
            "y": 10,
            "seriesName": "某银行杭州萧山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行杭州余杭支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行杭州余杭支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行洪泽北京路小微支行",
        "data": [{
            "x": 0.0001766394347538088,
            "y": 4,
            "seriesName": "某银行洪泽北京路小微支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行洪泽支行",
        "data": [{
            "x": 0.00006453694740238787,
            "y": 1,
            "seriesName": "某银行洪泽支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安北京路支行",
        "data": [{
            "x": 0.0008,
            "y": 8,
            "seriesName": "某银行淮安北京路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安北京西路社区支行",
        "data": [{
            "x": 0.0005438723712835388,
            "y": 3,
            "seriesName": "某银行淮安北京西路社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安车站广场支行",
        "data": [{
            "x": -0.00022,
            "y": -22,
            "seriesName": "某银行淮安车站广场支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安城北支行",
        "data": [{
            "x": 0.00023485204321277596,
            "y": 5,
            "seriesName": "某银行淮安城北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安城南支行",
        "data": [{
            "x": 0.00047036688617121356,
            "y": 11,
            "seriesName": "某银行淮安城南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安楚秀园支行",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行淮安楚秀园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安楚州(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行淮安楚州(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安楚州支行",
        "data": [{
            "x": 0.0001,
            "y": 3,
            "seriesName": "某银行淮安楚州支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安大治路支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行淮安大治路支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安分行(汇总)",
        "data": [{
            "x": 0.00091,
            "y": 91,
            "seriesName": "某银行淮安分行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安分行国际业务部",
        "data": [{
            "x": 0.001,
            "y": 20,
            "seriesName": "某银行淮安分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安分行清算中心",
        "data": [{
            "x": 0.0007493443237167478,
            "y": 4,
            "seriesName": "某银行淮安分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安分行银行卡部",
        "data": [{
            "x": -0.000020700772915108718,
            "y": -6,
            "seriesName": "某银行淮安分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安分行营业部",
        "data": [{
            "x": 0.000036495184916540077,
            "y": 11,
            "seriesName": "某银行淮安分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安公园支行",
        "data": [{
            "x": 0.001472525769200961,
            "y": 19,
            "seriesName": "某银行淮安公园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安工农路支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行淮安工农路支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安工学院支行",
        "data": [{
            "x": -0.000075,
            "y": -3,
            "seriesName": "某银行淮安工学院支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安韩信路支行",
        "data": [{
            "x": 0.00052,
            "y": 26,
            "seriesName": "某银行淮安韩信路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安河北西路支行",
        "data": [{
            "x": 0.0007,
            "y": 7,
            "seriesName": "某银行淮安河北西路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安华淮(汇总)",
        "data": [{
            "x": 0.0005454545454545455,
            "y": 6,
            "seriesName": "某银行淮安华淮(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安华淮支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行淮安华淮支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安淮海东路支行",
        "data": [{
            "x": -0.0006017412888546231,
            "y": -63,
            "seriesName": "某银行淮安淮海东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安淮海南路社区支行",
        "data": [{
            "x": -0.00020081934291910997,
            "y": -5,
            "seriesName": "某银行淮安淮海南路社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安淮海南路支行",
        "data": [{
            "x": 0.0008923076923076924,
            "y": 116,
            "seriesName": "某银行淮安淮海南路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安淮海支行",
        "data": [{
            "x": -0.00016666666666666666,
            "y": -5,
            "seriesName": "某银行淮安淮海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安淮阴(汇总)",
        "data": [{
            "x": 0.00032954358213873783,
            "y": 4,
            "seriesName": "某银行淮安淮阴(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安淮阴支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行淮安淮阴支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安健康东路支行",
        "data": [{
            "x": 0.000484027105517909,
            "y": 5,
            "seriesName": "某银行淮安健康东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安交通路支行",
        "data": [{
            "x": -0.02452327772225704,
            "y": 10429,
            "seriesName": "某银行淮安交通路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安解放东路(汇总)",
        "data": [{
            "x": -0.00038,
            "y": -19,
            "seriesName": "某银行淮安解放东路(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安解放东路支行",
        "data": [{
            "x": -0.00007142857142857143,
            "y": -1,
            "seriesName": "某银行淮安解放东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安开发区支行",
        "data": [{
            "x": 0.00025,
            "y": 15,
            "seriesName": "某银行淮安开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安科技支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 6,
            "seriesName": "某银行淮安科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安昆仑支行",
        "data": [{
            "x": -0.000310077519379845,
            "y": -8,
            "seriesName": "某银行淮安昆仑支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安汽配商城支行",
        "data": [{
            "x": -0.00004368338284116722,
            "y": -1,
            "seriesName": "某银行淮安汽配商城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安清安支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 6,
            "seriesName": "某银行淮安清安支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安清河(汇总)",
        "data": [{
            "x": 0.00036913990402362494,
            "y": 4,
            "seriesName": "某银行淮安清河(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安清河支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -2,
            "seriesName": "某银行淮安清河支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安清江支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行淮安清江支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安清浦(汇总)",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行淮安清浦(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安清浦支行",
        "data": [{
            "x": -0.00015,
            "y": -3,
            "seriesName": "某银行淮安清浦支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安人民南路支行",
        "data": [{
            "x": 0.0003843197540353574,
            "y": 1,
            "seriesName": "某银行淮安人民南路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安水渡口支行",
        "data": [{
            "x": -0.0002,
            "y": -8,
            "seriesName": "某银行淮安水渡口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安水门桥支行",
        "data": [{
            "x": 0.0002371716654756072,
            "y": 79,
            "seriesName": "某银行淮安水门桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安天宇支行",
        "data": [{
            "x": 0.0002998921906555112,
            "y": 81,
            "seriesName": "某银行淮安天宇支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安王营支行",
        "data": [{
            "x": 0.0007647058823529412,
            "y": 26,
            "seriesName": "某银行淮安王营支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安翔宇大道支行",
        "data": [{
            "x": -0.0001345023569192079,
            "y": -40,
            "seriesName": "某银行淮安翔宇大道支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安小营支行",
        "data": [{
            "x": -0.0000970045010088468,
            "y": -26,
            "seriesName": "某银行淮安小营支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安新民东路支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -1,
            "seriesName": "某银行淮安新民东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安新区(汇总)",
        "data": [{
            "x": 0.00035,
            "y": 7,
            "seriesName": "某银行淮安新区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安新区支行",
        "data": [{
            "x": 0.0004857198367981348,
            "y": 5,
            "seriesName": "某银行淮安新区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安营业部(汇总)",
        "data": [{
            "x": 0.000045677868707188415,
            "y": 27,
            "seriesName": "某银行淮安营业部(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安越闸支行",
        "data": [{
            "x": 0.0008166666666666667,
            "y": 49,
            "seriesName": "某银行淮安越闸支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行淮安镇淮楼支行",
        "data": [{
            "x": -0.00046666666666666666,
            "y": -14,
            "seriesName": "某银行淮安镇淮楼支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行睢宁支行",
        "data": [{
            "x": 0.0005333333333333334,
            "y": 32,
            "seriesName": "某银行睢宁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行惠山(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行惠山(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行建湖双湖支行",
        "data": [{
            "x": -0.000204,
            "y": -102,
            "seriesName": "某银行建湖双湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行建湖支行",
        "data": [{
            "x": 0.0006,
            "y": 15,
            "seriesName": "某银行建湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江都(汇总)",
        "data": [{
            "x": 0.0008875,
            "y": 71,
            "seriesName": "某银行江都(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江都金仝支行",
        "data": [{
            "x": 0.0003134,
            "y": 1567,
            "seriesName": "某银行江都金仝支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江都龙川支行",
        "data": [{
            "x": 0.0005227492739593417,
            "y": 54,
            "seriesName": "某银行江都龙川支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江都支行",
        "data": [{
            "x": 0.0005,
            "y": 50,
            "seriesName": "某银行江都支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江宁支行（汇总）",
        "data": [{
            "x": 0.00048749064192071313,
            "y": 28,
            "seriesName": "某银行江宁支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江阴(汇总)",
        "data": [{
            "x": 0.0006344794247386549,
            "y": 21,
            "seriesName": "某银行江阴(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江阴朝阳路支行",
        "data": [{
            "x": 0.0006042296072507553,
            "y": 2,
            "seriesName": "某银行江阴朝阳路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江阴科技园支行",
        "data": [{
            "x": 0.00021103561869466748,
            "y": 43,
            "seriesName": "某银行江阴科技园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江阴临港支行",
        "data": [{
            "x": 0.0003902895948794005,
            "y": 10,
            "seriesName": "某银行江阴临港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江阴支行",
        "data": [{
            "x": 0.0005324298160696999,
            "y": 11,
            "seriesName": "某银行江阴支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行江阴周庄支行",
        "data": [{
            "x": 0.0005666666666666667,
            "y": 17,
            "seriesName": "某银行江阴周庄支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行金湖支行",
        "data": [{
            "x": -0.00027142857142857144,
            "y": -19,
            "seriesName": "某银行金湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行金坛支行",
        "data": [{"x": 0.0008, "y": 8, "seriesName": "某银行金坛支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行靖江支行",
        "data": [{
            "x": -0.0002,
            "y": -5,
            "seriesName": "某银行靖江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行句容（汇总）",
        "data": [{
            "x": -0.00005729458548262631,
            "y": -30,
            "seriesName": "某银行句容（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行句容长江路支行",
        "data": [{
            "x": 0.000556221119288037,
            "y": 13,
            "seriesName": "某银行句容长江路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行句容支行",
        "data": [{
            "x": -0.0002704248790888762,
            "y": -102,
            "seriesName": "某银行句容支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行开发区(汇总)",
        "data": [{
            "x": -0.0000909090909090909,
            "y": -1,
            "seriesName": "某银行开发区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行昆山花桥支行",
        "data": [{
            "x": 0.0006365372374283895,
            "y": 8,
            "seriesName": "某银行昆山花桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行昆山千灯支行",
        "data": [{
            "x": 0.0001936108422071636,
            "y": 2,
            "seriesName": "某银行昆山千灯支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行昆山玉山支行",
        "data": [{
            "x": 0.0006344602555968459,
            "y": 14,
            "seriesName": "某银行昆山玉山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行昆山支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -6,
            "seriesName": "某银行昆山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行溧水支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行溧水支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行溧阳支行",
        "data": [{
            "x": 0.0010695187165775401,
            "y": 2,
            "seriesName": "某银行溧阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行涟水安东路支行",
        "data": [{
            "x": -0.00005,
            "y": -1,
            "seriesName": "某银行涟水安东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行涟水支行",
        "data": [{
            "x": -0.00012,
            "y": -6,
            "seriesName": "某银行涟水支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行涟水支行(汇总)",
        "data": [{
            "x": -0.0449535,
            "y": 28028,
            "seriesName": "某银行涟水支行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港(汇总)",
        "data": [{
            "x": 0.0006578947368421052,
            "y": 1,
            "seriesName": "某银行连云港(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港苍梧(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行连云港苍梧(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港苍梧支行",
        "data": [{
            "x": 0.00018047471792148655,
            "y": 106,
            "seriesName": "某银行连云港苍梧支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港朝阳路支行",
        "data": [{
            "x": 0.0020721094073767096,
            "y": 5,
            "seriesName": "某银行连云港朝阳路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港城北支行",
        "data": [{
            "x": 0.00008752352194652313,
            "y": 2,
            "seriesName": "某银行连云港城北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港城西支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -1,
            "seriesName": "某银行连云港城西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港东城支行",
        "data": [{
            "x": 0.000550251100502201,
            "y": 71,
            "seriesName": "某银行连云港东城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港东海(汇总)",
        "data": [{
            "x": 0.00026609898882384245,
            "y": 2,
            "seriesName": "某银行连云港东海(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港分行国际业务部",
        "data": [{
            "x": -0.00013352026169971293,
            "y": -2,
            "seriesName": "某银行连云港分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港分行清算中心",
        "data": [{
            "x": 0.00009228497600590623,
            "y": 1,
            "seriesName": "某银行连云港分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港分行银行卡部",
        "data": [{
            "x": 0.00009621243706103075,
            "y": 6,
            "seriesName": "某银行连云港分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港分行营业部",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行连云港分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港港口支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行连云港港口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港光明支行",
        "data": [{
            "x": 0.00045589240939138365,
            "y": 2,
            "seriesName": "某银行连云港光明支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港海连西路小微支行",
        "data": [{
            "x": -0.0003333333333333333,
            "y": -2,
            "seriesName": "某银行连云港海连西路小微支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港海州(汇总)",
        "data": [{
            "x": 0.00015,
            "y": 6,
            "seriesName": "某银行连云港海州(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港海州支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行连云港海州支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港锦云(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行连云港锦云(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港锦云支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行连云港锦云支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港开发区(汇总)",
        "data": [{
            "x": 0.0003762227238525207,
            "y": 2,
            "seriesName": "某银行连云港开发区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港开发区支行",
        "data": [{
            "x": 0.0004919645785503444,
            "y": 9,
            "seriesName": "某银行连云港开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港连云(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行连云港连云(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港连云支行",
        "data": [{
            "x": 0.0006,
            "y": 6,
            "seriesName": "某银行连云港连云支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港陇海(汇总)",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行连云港陇海(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港陇海支行",
        "data": [{
            "x": -0.00023076923076923076,
            "y": -3,
            "seriesName": "某银行连云港陇海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港民主支行",
        "data": [{
            "x": 0.00103,
            "y": 103,
            "seriesName": "某银行连云港民主支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港浦中(汇总)",
        "data": [{
            "x": 0.0005908661188277216,
            "y": 66,
            "seriesName": "某银行连云港浦中(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港浦中支行",
        "data": [{
            "x": 0.0007876396270247907,
            "y": 302,
            "seriesName": "某银行连云港浦中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港新华(汇总)",
        "data": [{
            "x": -0.00010137913166896334,
            "y": -25,
            "seriesName": "某银行连云港新华(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港新华支行",
        "data": [{
            "x": 0.0007333333333333333,
            "y": 11,
            "seriesName": "某银行连云港新华支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港新浦(汇总)",
        "data": [{
            "x": 0.0002,
            "y": 2,
            "seriesName": "某银行连云港新浦(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港新浦支行",
        "data": [{
            "x": -0.0001,
            "y": -2,
            "seriesName": "某银行连云港新浦支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港新银(汇总)",
        "data": [{
            "x": 0.0005670031807495506,
            "y": 41,
            "seriesName": "某银行连云港新银(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港新银支行",
        "data": [{
            "x": 0.00004105834988731349,
            "y": 62,
            "seriesName": "某银行连云港新银支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港行政中心支行",
        "data": [{
            "x": 0.00009375,
            "y": 3,
            "seriesName": "某银行连云港行政中心支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港行政中心支行（已停用）",
        "data": [{
            "x": 0.0007733333333333333,
            "y": 116,
            "seriesName": "某银行连云港行政中心支行（已停用）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港幸福支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行连云港幸福支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港墟沟支行",
        "data": [{
            "x": 0.00025,
            "y": 5,
            "seriesName": "某银行连云港墟沟支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港盐河支行",
        "data": [{
            "x": 0.0005903187721369539,
            "y": 6,
            "seriesName": "某银行连云港盐河支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港营业部(汇总)",
        "data": [{
            "x": 0.0006765899864682003,
            "y": 2,
            "seriesName": "某银行连云港营业部(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行连云港中华支行",
        "data": [{
            "x": -0.00015,
            "y": -3,
            "seriesName": "某银行连云港中华支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行娄葑（汇总）",
        "data": [{
            "x": 0.0006,
            "y": 12,
            "seriesName": "某银行娄葑（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行木渎（汇总）",
        "data": [{
            "x": -0.0001,
            "y": -2,
            "seriesName": "某银行木渎（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京(分行汇总)",
        "data": [{
            "x": 0.00012688598717298022,
            "y": 11,
            "seriesName": "某银行南京(分行汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京保利香槟社区支行",
        "data": [{
            "x": 0.0004531858968548899,
            "y": 5,
            "seriesName": "某银行南京保利香槟社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京北京西路支行",
        "data": [{
            "x": -0.0006976964852841857,
            "y": -123,
            "seriesName": "某银行南京北京西路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京城北支行",
        "data": [{
            "x": 0.0012521992740414335,
            "y": 81,
            "seriesName": "某银行南京城北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京城东支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南京城东支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京城南支行",
        "data": [{
            "x": -0.000021497186939537625,
            "y": -5,
            "seriesName": "某银行南京城南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京城西支行",
        "data": [{
            "x": 0.0003807037580899549,
            "y": 7,
            "seriesName": "某银行南京城西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京城中支行",
        "data": [{
            "x": 0.00019884668920262477,
            "y": 3,
            "seriesName": "某银行南京城中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京高新技术产业开发区支行",
        "data": [{
            "x": 0.0002064929980101584,
            "y": 79,
            "seriesName": "某银行南京高新技术产业开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京鼓楼支行",
        "data": [{
            "x": 0.0004221071589374156,
            "y": 15,
            "seriesName": "某银行南京鼓楼支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京河西支行",
        "data": [{
            "x": 0.00042437300055028586,
            "y": 92,
            "seriesName": "某银行南京河西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京鸿福苑社区支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南京鸿福苑社区支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京江宁开发区支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南京江宁开发区支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京江宁支行",
        "data": [{
            "x": 0.0004922713399625873,
            "y": 5,
            "seriesName": "某银行南京江宁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京金马郦城社区支行",
        "data": [{
            "x": -0.0001818181818181818,
            "y": -2,
            "seriesName": "某银行南京金马郦城社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京龙江支行",
        "data": [{
            "x": -0.00009906896707679783,
            "y": -43,
            "seriesName": "某银行南京龙江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京迈皋桥支行",
        "data": [{
            "x": -0.00033,
            "y": -33,
            "seriesName": "某银行南京迈皋桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京浦口支行",
        "data": [{
            "x": -0.0006274270404096929,
            "y": -37,
            "seriesName": "某银行南京浦口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京清算中心",
        "data": [{
            "x": 0.00004300058050783686,
            "y": 4,
            "seriesName": "某银行南京清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京泰山路支行",
        "data": [{
            "x": 0.0009636363636363637,
            "y": 53,
            "seriesName": "某银行南京泰山路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京万江共和新城社区支行",
        "data": [{
            "x": 0.0005279831045406547,
            "y": 3,
            "seriesName": "某银行南京万江共和新城社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京西康路支行",
        "data": [{
            "x": 0.0003393990026890844,
            "y": 27,
            "seriesName": "某银行南京西康路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京下关支行",
        "data": [{
            "x": 0.0005,
            "y": 5,
            "seriesName": "某银行南京下关支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京新港支行",
        "data": [{
            "x": 0.0005151935254224587,
            "y": 55,
            "seriesName": "某银行南京新港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京新街口支行",
        "data": [{
            "x": 0.0003917727717923604,
            "y": 2,
            "seriesName": "某银行南京新街口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京雨花支行",
        "data": [{
            "x": 0.0013448,
            "y": 13448,
            "seriesName": "某银行南京雨花支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南京中央路支行",
        "data": [{
            "x": 0.0008714596949891067,
            "y": 4,
            "seriesName": "某银行南京中央路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通(汇总)",
        "data": [{
            "x": 0.0018042399639152007,
            "y": 4,
            "seriesName": "某银行南通(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通北城支行",
        "data": [{
            "x": -0.0002,
            "y": -2,
            "seriesName": "某银行南通北城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通北濠支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南通北濠支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通城南(汇总)",
        "data": [{
            "x": -0.00013139001248205117,
            "y": -27,
            "seriesName": "某银行南通城南(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通城南支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行南通城南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通城区(汇总)",
        "data": [{
            "x": 0.00002892960462873674,
            "y": 3,
            "seriesName": "某银行南通城区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通城区支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南通城区支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通城西(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南通城西(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通城西支行",
        "data": [{
            "x": -0.00044,
            "y": -22,
            "seriesName": "某银行南通城西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通崇川(汇总)",
        "data": [{
            "x": 0.000248015873015873,
            "y": 1,
            "seriesName": "某银行南通崇川(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通崇川支行",
        "data": [{
            "x": 0.00040666937779585197,
            "y": 1,
            "seriesName": "某银行南通崇川支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通帝奥社区支行",
        "data": [{
            "x": 0.0003981089823339139,
            "y": 72,
            "seriesName": "某银行南通帝奥社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通东方支行",
        "data": [{
            "x": 0.0009125,
            "y": 73,
            "seriesName": "某银行南通东方支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通端平桥支行",
        "data": [{
            "x": -0.000033333333333333335,
            "y": -1,
            "seriesName": "某银行南通端平桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通分行国际业务部",
        "data": [{
            "x": 0.0007363047319850776,
            "y": 30,
            "seriesName": "某银行南通分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通分行清算中心",
        "data": [{
            "x": 0.0006,
            "y": 120,
            "seriesName": "某银行南通分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通分行银行卡部",
        "data": [{
            "x": -0.0002,
            "y": -6,
            "seriesName": "某银行南通分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通分行营业部",
        "data": [{
            "x": 0.001,
            "y": 20,
            "seriesName": "某银行南通分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通港闸(汇总)",
        "data": [{
            "x": 0.0004318802362883216,
            "y": 211,
            "seriesName": "某银行南通港闸(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通港闸支行",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行南通港闸支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通工农(汇总)",
        "data": [{
            "x": 0.0006,
            "y": 18,
            "seriesName": "某银行南通工农(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通工农支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南通工农支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通观音山支行",
        "data": [{
            "x": 0.0007284500202347228,
            "y": 9,
            "seriesName": "某银行南通观音山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通国家高新技术产业开发区科技小微支行",
        "data": [{
            "x": 0.000663129973474801,
            "y": 4,
            "seriesName": "某银行南通国家高新技术产业开发区科技小微支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通海安(汇总)",
        "data": [{
            "x": 0.0002031694433157253,
            "y": 10,
            "seriesName": "某银行南通海安(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通海门(汇总)",
        "data": [{
            "x": 0.0004683402023229674,
            "y": 5,
            "seriesName": "某银行南通海门(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通濠南支行",
        "data": [{
            "x": -0.00013043478260869564,
            "y": -3,
            "seriesName": "某银行南通濠南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通虹桥支行",
        "data": [{
            "x": 0.0025869205298013247,
            "y": 25,
            "seriesName": "某银行南通虹桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通环南支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南通环南支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通汇丰支行",
        "data": [{
            "x": 0.0006142857142857142,
            "y": 43,
            "seriesName": "某银行南通汇丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通金信支行",
        "data": [{
            "x": 0.0005231767290990897,
            "y": 5,
            "seriesName": "某银行南通金信支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通静海(汇总)",
        "data": [{
            "x": 0.0008,
            "y": 32,
            "seriesName": "某银行南通静海(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通静海支行",
        "data": [{
            "x": -0.0002188758536158291,
            "y": -10,
            "seriesName": "某银行南通静海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通开发区(汇总)",
        "data": [{
            "x": -0.00005,
            "y": -1,
            "seriesName": "某银行南通开发区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通开发区支行",
        "data": [{
            "x": 0.0005650689138392747,
            "y": 23,
            "seriesName": "某银行南通开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通平潮支行",
        "data": [{
            "x": 0.00076,
            "y": 38,
            "seriesName": "某银行南通平潮支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通启东(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南通启东(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通如东(汇总)",
        "data": [{
            "x": 0.0004963518141658808,
            "y": 10,
            "seriesName": "某银行南通如东(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通如皋(汇总)",
        "data": [{
            "x": -0.0004714285714285714,
            "y": -33,
            "seriesName": "某银行南通如皋(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通唐闸支行",
        "data": [{
            "x": 0.001008290387631638,
            "y": 45,
            "seriesName": "某银行南通唐闸支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通桃坞支行",
        "data": [{
            "x": 0.0009214285714285714,
            "y": 129,
            "seriesName": "某银行南通桃坞支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通通州(汇总)",
        "data": [{
            "x": 0.0008333333333333334,
            "y": 20,
            "seriesName": "某银行南通通州(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通通州支行",
        "data": [{
            "x": 0.0005238998103981638,
            "y": 21,
            "seriesName": "某银行南通通州支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通锡通园区支行",
        "data": [{
            "x": 0.0005754981655995971,
            "y": 8,
            "seriesName": "某银行南通锡通园区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通新城支行",
        "data": [{
            "x": 0.0007739938080495357,
            "y": 1,
            "seriesName": "某银行南通新城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通新桥(汇总)",
        "data": [{
            "x": 0.0007,
            "y": 7,
            "seriesName": "某银行南通新桥(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通新桥支行",
        "data": [{
            "x": 0.0005353319057815846,
            "y": 2,
            "seriesName": "某银行南通新桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通学田支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行南通学田支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通壹城社区支行",
        "data": [{
            "x": 0.0006167129201356768,
            "y": 2,
            "seriesName": "某银行南通壹城社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通营业部(汇总)",
        "data": [{
            "x": -0.0007,
            "y": -14,
            "seriesName": "某银行南通营业部(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通跃龙(汇总)",
        "data": [{
            "x": 0.0003618599601954044,
            "y": 2,
            "seriesName": "某银行南通跃龙(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通跃龙科技支行",
        "data": [{
            "x": -0.00026265102433899493,
            "y": -3,
            "seriesName": "某银行南通跃龙科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通中南支行",
        "data": [{
            "x": 0.00025,
            "y": 15,
            "seriesName": "某银行南通中南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行南通紫琅支行",
        "data": [{
            "x": 0.0003872216844143272,
            "y": 6,
            "seriesName": "某银行南通紫琅支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行沛县支行",
        "data": [{
            "x": 0.00015463952182770467,
            "y": 47,
            "seriesName": "某银行沛县支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行邳州支行",
        "data": [{
            "x": 0.00023523294251649726,
            "y": 59,
            "seriesName": "某银行邳州支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行浦口支行（汇总）",
        "data": [{
            "x": -0.37448,
            "y": 6276,
            "seriesName": "某银行浦口支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行启东汇龙支行",
        "data": [{
            "x": -0.00008333333333333333,
            "y": -25,
            "seriesName": "某银行启东汇龙支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行启东支行",
        "data": [{
            "x": -0.0005,
            "y": -15,
            "seriesName": "某银行启东支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行如东文峰支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 3,
            "seriesName": "某银行如东文峰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行如东支行",
        "data": [{
            "x": -0.00008,
            "y": -4,
            "seriesName": "某银行如东支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行如皋港区支行",
        "data": [{
            "x": 0.00065,
            "y": 13,
            "seriesName": "某银行如皋港区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行如皋支行",
        "data": [{
            "x": 0.0003942404055886341,
            "y": 109,
            "seriesName": "某银行如皋支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海宝山支行",
        "data": [{
            "x": 0.0003769317753486619,
            "y": 2,
            "seriesName": "某银行上海宝山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海长宁支行",
        "data": [{
            "x": -0.00026265102433899493,
            "y": -3,
            "seriesName": "某银行上海长宁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海分行(汇总)",
        "data": [{
            "x": -0.0006802668561015536,
            "y": -875,
            "seriesName": "某银行上海分行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海分行清算中心",
        "data": [{
            "x": 0.00005003026831232896,
            "y": 20,
            "seriesName": "某银行上海分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海分行银行卡部",
        "data": [{
            "x": 0.0007334385838419751,
            "y": 180,
            "seriesName": "某银行上海分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海分行营业部",
        "data": [{
            "x": 0.0005890736342042755,
            "y": 65,
            "seriesName": "某银行上海分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海奉贤支行",
        "data": [{
            "x": -0.0036993407320147975,
            "y": -716,
            "seriesName": "某银行上海奉贤支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海黄浦支行",
        "data": [{
            "x": 0.0014074074074074073,
            "y": 4370,
            "seriesName": "某银行上海黄浦支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海惠南支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行上海惠南支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海嘉定支行",
        "data": [{
            "x": 0.0026259847442791045,
            "y": 21,
            "seriesName": "某银行上海嘉定支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海金桥支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行上海金桥支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海闵行支行",
        "data": [{
            "x": 0.0003872216844143272,
            "y": 2,
            "seriesName": "某银行上海闵行支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海南汇支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行上海南汇支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海普陀支行",
        "data": [{
            "x": -0.00002857142857142857,
            "y": -1,
            "seriesName": "某银行上海普陀支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海松江支行",
        "data": [{
            "x": 0.00025744001647616104,
            "y": 37,
            "seriesName": "某银行上海松江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海徐汇支行",
        "data": [{
            "x": 0.000468384074941452,
            "y": 3,
            "seriesName": "某银行上海徐汇支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海杨浦支行",
        "data": [{
            "x": 0.0006,
            "y": 6,
            "seriesName": "某银行上海杨浦支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海闸北支行",
        "data": [{
            "x": 0.0008032128514056225,
            "y": 3,
            "seriesName": "某银行上海闸北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行上海自贸区分账核算单元",
        "data": [{
            "x": -0.00035,
            "y": -14,
            "seriesName": "某银行上海自贸区分账核算单元",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行射阳沿海支行",
        "data": [{
            "x": 0.0008157346143388018,
            "y": 9,
            "seriesName": "某银行射阳沿海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行射阳支行",
        "data": [{
            "x": 0.000925,
            "y": 37,
            "seriesName": "某银行射阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳坂田支行",
        "data": [{
            "x": 0.0005,
            "y": 5,
            "seriesName": "某银行深圳坂田支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳宝安支行",
        "data": [{
            "x": 0.00019353590090961874,
            "y": 1,
            "seriesName": "某银行深圳宝安支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳车公庙支行",
        "data": [{
            "x": 0.0007460598713046722,
            "y": 8,
            "seriesName": "某银行深圳车公庙支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳分行(汇总)",
        "data": [{
            "x": 0.00004857198367981349,
            "y": 1,
            "seriesName": "某银行深圳分行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳分行清算中心",
        "data": [{
            "x": -0.00274,
            "y": -137,
            "seriesName": "某银行深圳分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳分行银行卡部",
        "data": [{
            "x": -0.0002387774594078319,
            "y": -3,
            "seriesName": "某银行深圳分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳分行营业部",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行深圳分行营业部", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳福田互联网金融产业园社区支行",
        "data": [{
            "x": -0.00004,
            "y": -2,
            "seriesName": "某银行深圳福田互联网金融产业园社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳横岗社区支行",
        "data": [{
            "x": -0.000033333333333333335,
            "y": -1,
            "seriesName": "某银行深圳横岗社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳华强南社区支行",
        "data": [{
            "x": 0.00002604166666666667,
            "y": 1,
            "seriesName": "某银行深圳华强南社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳科技支行",
        "data": [{
            "x": 0.0003444712366517396,
            "y": 1,
            "seriesName": "某银行深圳科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳龙岗支行",
        "data": [{
            "x": 0.0022000159037294247,
            "y": 83,
            "seriesName": "某银行深圳龙岗支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳罗湖支行",
        "data": [{
            "x": 0.0004683475138552806,
            "y": 6,
            "seriesName": "某银行深圳罗湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳民治社区支行",
        "data": [{
            "x": 0.00006523157208088715,
            "y": 2,
            "seriesName": "某银行深圳民治社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳南山支行",
        "data": [{
            "x": 0.0001845585973546601,
            "y": 3,
            "seriesName": "某银行深圳南山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳南山智园社区支行",
        "data": [{
            "x": 0.00055,
            "y": 11,
            "seriesName": "某银行深圳南山智园社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳坪山支行",
        "data": [{
            "x": 0.0005868458502662813,
            "y": 40,
            "seriesName": "某银行深圳坪山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳前海支行",
        "data": [{
            "x": 0.00032268473701193933,
            "y": 1,
            "seriesName": "某银行深圳前海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳清湖支行",
        "data": [{
            "x": -0.00008679952781056871,
            "y": -5,
            "seriesName": "某银行深圳清湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳沙井支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行深圳沙井支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳水贝社区支行",
        "data": [{
            "x": 0.0004354347089844695,
            "y": 9,
            "seriesName": "某银行深圳水贝社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳田贝支行",
        "data": [{
            "x": 0.00040335592126492415,
            "y": 25,
            "seriesName": "某银行深圳田贝支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行深圳中山公园社区支行",
        "data": [{
            "x": 0.0005855943782939684,
            "y": 3,
            "seriesName": "某银行深圳中山公园社区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行沭阳(汇总)",
        "data": [{
            "x": 0.0007090909090909091,
            "y": 39,
            "seriesName": "某银行沭阳(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行沭阳城中小微支行",
        "data": [{
            "x": 0.0005251181515841065,
            "y": 3,
            "seriesName": "某银行沭阳城中小微支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行沭阳支行",
        "data": [{
            "x": 0.00023652861996301553,
            "y": 11,
            "seriesName": "某银行沭阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泗洪支行",
        "data": [{"x": 0.0001, "y": 1, "seriesName": "某银行泗洪支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泗阳支行",
        "data": [{
            "x": 0.0009375,
            "y": 30,
            "seriesName": "某银行泗阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州(汇总)",
        "data": [{
            "x": -0.0002971326697370376,
            "y": -51,
            "seriesName": "某银行苏州(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州滨河路支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行苏州滨河路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州沧浪(汇总)",
        "data": [{
            "x": 0.00005,
            "y": 1,
            "seriesName": "某银行苏州沧浪(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州沧浪支行",
        "data": [{
            "x": 0.0004111305437364588,
            "y": 70,
            "seriesName": "某银行苏州沧浪支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州常熟(汇总）",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行苏州常熟(汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州长吴路支行",
        "data": [{
            "x": 0.00048624888162757227,
            "y": 25,
            "seriesName": "某银行苏州长吴路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州城北支行",
        "data": [{
            "x": 0.0006428571428571428,
            "y": 18,
            "seriesName": "某银行苏州城北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州城西(汇总）",
        "data": [{
            "x": -0.03438256658595642,
            "y": -71,
            "seriesName": "某银行苏州城西(汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州城西支行",
        "data": [{
            "x": 0.0625,
            "y": 23,
            "seriesName": "某银行苏州城西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州城中(汇总)",
        "data": [{
            "x": 0.0002578140402493213,
            "y": 36,
            "seriesName": "某银行苏州城中(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州城中支行",
        "data": [{
            "x": 0.0002664661664344281,
            "y": 172,
            "seriesName": "某银行苏州城中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州东港支行",
        "data": [{
            "x": 0.0006289392337646696,
            "y": 47,
            "seriesName": "某银行苏州东港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州分行国际业务部",
        "data": [{
            "x": 0.00015547982334130343,
            "y": 39,
            "seriesName": "某银行苏州分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州分行清算中心",
        "data": [{
            "x": -0.00006553885225084995,
            "y": -6,
            "seriesName": "某银行苏州分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州分行银行卡部",
        "data": [{
            "x": -0.09065363636363637,
            "y": 29231,
            "seriesName": "某银行苏州分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州分行营业部",
        "data": [{
            "x": 0.00006363636363636364,
            "y": 7,
            "seriesName": "某银行苏州分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州葑门支行",
        "data": [{
            "x": 0.00015231178832316906,
            "y": 53,
            "seriesName": "某银行苏州葑门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州工业园区科技支行",
        "data": [{
            "x": -0.00035714285714285714,
            "y": -5,
            "seriesName": "某银行苏州工业园区科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州工业园区支行",
        "data": [{
            "x": 0.0003872216844143272,
            "y": 8,
            "seriesName": "某银行苏州工业园区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州寒山支行",
        "data": [{
            "x": 0.0008125,
            "y": 65,
            "seriesName": "某银行苏州寒山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州虹桥支行",
        "data": [{
            "x": -0.0001625,
            "y": -13,
            "seriesName": "某银行苏州虹桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州浒关支行",
        "data": [{
            "x": 0.0002844825699717706,
            "y": 54,
            "seriesName": "某银行苏州浒关支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州黄埭支行",
        "data": [{
            "x": 0.0004247005860868088,
            "y": 5,
            "seriesName": "某银行苏州黄埭支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州解放路支行",
        "data": [{
            "x": 0.0012542854753741953,
            "y": 15,
            "seriesName": "某银行苏州解放路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州金阊(汇总)",
        "data": [{
            "x": -0.00003421038627327256,
            "y": -7,
            "seriesName": "某银行苏州金阊(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州金阊支行",
        "data": [{
            "x": -0.0008,
            "y": -8,
            "seriesName": "某银行苏州金阊支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州金门支行",
        "data": [{
            "x": -0.00013333333333333334,
            "y": -20,
            "seriesName": "某银行苏州金门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州景德路支行",
        "data": [{
            "x": -0.0005326029064901468,
            "y": -14,
            "seriesName": "某银行苏州景德路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州科技城支行",
        "data": [{
            "x": -0.00088,
            "y": -22,
            "seriesName": "某银行苏州科技城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州昆山(汇总）",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行苏州昆山(汇总）", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州濂溪坊支行",
        "data": [{
            "x": -0.0001619047619047619,
            "y": -17,
            "seriesName": "某银行苏州濂溪坊支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州娄葑支行",
        "data": [{
            "x": 0.00044285018378282625,
            "y": 10,
            "seriesName": "某银行苏州娄葑支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州木渎支行",
        "data": [{
            "x": 0.0007242273418174231,
            "y": 245,
            "seriesName": "某银行苏州木渎支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州南门支行",
        "data": [{
            "x": 0.00084375,
            "y": 270,
            "seriesName": "某银行苏州南门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州盘门支行",
        "data": [{
            "x": 0.00033967391304347825,
            "y": 1,
            "seriesName": "某银行苏州盘门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州平江(汇总)",
        "data": [{
            "x": -0.00014294563301091152,
            "y": -27,
            "seriesName": "某银行苏州平江(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州平江支行",
        "data": [{
            "x": 0.00007144547593850331,
            "y": 35,
            "seriesName": "某银行苏州平江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州盛泽支行",
        "data": [{
            "x": -0.000007277331839054529,
            "y": -1,
            "seriesName": "某银行苏州盛泽支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州狮山支行",
        "data": [{
            "x": -0.0005,
            "y": -5,
            "seriesName": "某银行苏州狮山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州市区(汇总)",
        "data": [{
            "x": 0.0003478048850451071,
            "y": 99,
            "seriesName": "某银行苏州市区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州吴江(汇总）",
        "data": [{
            "x": 0.0009,
            "y": 18,
            "seriesName": "某银行苏州吴江(汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州吴中(汇总)",
        "data": [{
            "x": 0.00122,
            "y": 61,
            "seriesName": "某银行苏州吴中(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州吴中开发区支行",
        "data": [{
            "x": 0.0004847309743092584,
            "y": 2,
            "seriesName": "某银行苏州吴中开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州吴中支行",
        "data": [{
            "x": 0.0004359928899621022,
            "y": 13,
            "seriesName": "某银行苏州吴中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州相城（汇总）",
        "data": [{
            "x": -0.0002,
            "y": -12,
            "seriesName": "某银行苏州相城（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州相城支行",
        "data": [{
            "x": -0.0006,
            "y": -18,
            "seriesName": "某银行苏州相城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州新区(汇总)",
        "data": [{
            "x": 0.000484027105517909,
            "y": 1,
            "seriesName": "某银行苏州新区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州新区支行",
        "data": [{
            "x": -0.0006666666666666666,
            "y": -1,
            "seriesName": "某银行苏州新区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州园区(汇总)",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行苏州园区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行苏州直属(汇总)",
        "data": [{
            "x": 0.000926,
            "y": 463,
            "seriesName": "某银行苏州直属(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁城中(汇总)",
        "data": [{
            "x": -0.00012105263157894736,
            "y": -23,
            "seriesName": "某银行宿迁城中(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁城中支行",
        "data": [{
            "x": 0.00014765051123989516,
            "y": 8,
            "seriesName": "某银行宿迁城中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁分行(汇总)",
        "data": [{
            "x": -0.0006057606359009212,
            "y": -81,
            "seriesName": "某银行宿迁分行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁分行清算中心",
        "data": [{
            "x": -0.00016020791427096497,
            "y": -86,
            "seriesName": "某银行宿迁分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁分行银行卡部",
        "data": [{
            "x": 0.000831914312825779,
            "y": 65,
            "seriesName": "某银行宿迁分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁分行营业部",
        "data": [{
            "x": -0.000261880768006789,
            "y": -78,
            "seriesName": "某银行宿迁分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁经济开发区科技支行",
        "data": [{
            "x": 0.00011290504685559444,
            "y": 5,
            "seriesName": "某银行宿迁经济开发区科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁宿城支行",
        "data": [{
            "x": 0.0012457482072058409,
            "y": 125,
            "seriesName": "某银行宿迁宿城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁宿豫支行",
        "data": [{
            "x": -0.005774325762550667,
            "y": 352,
            "seriesName": "某银行宿迁宿豫支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁西楚支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行宿迁西楚支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁洋河支行",
        "data": [{
            "x": 0.0001818181818181818,
            "y": 1,
            "seriesName": "某银行宿迁洋河支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宿迁义乌商城小微支行",
        "data": [{
            "x": 0.0010326086956521738,
            "y": 76,
            "seriesName": "某银行宿迁义乌商城小微支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行太仓（汇总）",
        "data": [{
            "x": 0.0008666666666666666,
            "y": 13,
            "seriesName": "某银行太仓（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行太仓沙溪支行",
        "data": [{
            "x": 0.000354735721887194,
            "y": 1,
            "seriesName": "某银行太仓沙溪支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行太仓支行",
        "data": [{
            "x": -0.00005,
            "y": -2,
            "seriesName": "某银行太仓支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰兴支行",
        "data": [{
            "x": 0.00022471910112359551,
            "y": 3,
            "seriesName": "某银行泰兴支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州分行(汇总)",
        "data": [{
            "x": -0.0002,
            "y": -2,
            "seriesName": "某银行泰州分行(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州分行清算中心",
        "data": [{
            "x": 0.0009066666666666666,
            "y": 272,
            "seriesName": "某银行泰州分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州分行银行卡部",
        "data": [{
            "x": 0.0005619810739277038,
            "y": 31,
            "seriesName": "某银行泰州分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州分行营业部",
        "data": [{
            "x": -0.00015384615384615385,
            "y": -2,
            "seriesName": "某银行泰州分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州高港(汇总)",
        "data": [{
            "x": 0.0004958349861166203,
            "y": 10,
            "seriesName": "某银行泰州高港(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州高港支行",
        "data": [{
            "x": 0.0004952947003467063,
            "y": 3,
            "seriesName": "某银行泰州高港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州海陵支行",
        "data": [{
            "x": 0.00082,
            "y": 41,
            "seriesName": "某银行泰州海陵支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州姜堰支行",
        "data": [{
            "x": 0.0005111105702533648,
            "y": 21,
            "seriesName": "某银行泰州姜堰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州金港南路小微支行",
        "data": [{
            "x": 0,
            "y": 0,
            "seriesName": "某银行泰州金港南路小微支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州新区支行",
        "data": [{
            "x": -0.00007392192788387921,
            "y": -13,
            "seriesName": "某银行泰州新区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行泰州营业部(汇总)",
        "data": [{
            "x": 0.00089,
            "y": 89,
            "seriesName": "某银行泰州营业部(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行唐城(汇总)",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行唐城(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行吴江汾湖支行",
        "data": [{
            "x": 0.0008158995815899582,
            "y": 39,
            "seriesName": "某银行吴江汾湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行吴江盛泽(汇总）",
        "data": [{
            "x": 0.0005043789261314136,
            "y": 11,
            "seriesName": "某银行吴江盛泽(汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行吴江支行",
        "data": [{
            "x": 0.00042068580737363763,
            "y": 145,
            "seriesName": "某银行吴江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡(汇总)",
        "data": [{
            "x": 0.0003872216844143272,
            "y": 4,
            "seriesName": "某银行无锡(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡(山军)北支行",
        "data": [{
            "x": -0.00017357595394451356,
            "y": -6,
            "seriesName": "某银行无锡(山军)北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡(山军)嶂支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡(山军)嶂支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡安镇支行",
        "data": [{
            "x": -0.00005,
            "y": -2,
            "seriesName": "某银行无锡安镇支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡宝光(汇总)",
        "data": [{
            "x": -0.00008666666666666667,
            "y": -13,
            "seriesName": "某银行无锡宝光(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡宝光支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行无锡宝光支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡北大街支行",
        "data": [{
            "x": 0.00012583892617449666,
            "y": 3,
            "seriesName": "某银行无锡北大街支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡北环路支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡北环路支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡曹张支行",
        "data": [{
            "x": 0.0005829132526559498,
            "y": 73,
            "seriesName": "某银行无锡曹张支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡长江北路支行",
        "data": [{
            "x": 0.0007,
            "y": 14,
            "seriesName": "某银行无锡长江北路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡朝阳(汇总)",
        "data": [{
            "x": null,
            "y": 6,
            "seriesName": "某银行无锡朝阳(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡朝阳支行",
        "data": [{
            "x": -0.00065,
            "y": -13,
            "seriesName": "某银行无锡朝阳支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡城郊支行",
        "data": [{
            "x": -0.0003,
            "y": -3,
            "seriesName": "某银行无锡城郊支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡诚业(汇总)",
        "data": [{
            "x": -0.0006626963531174585,
            "y": -224,
            "seriesName": "某银行无锡诚业(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡诚业支行",
        "data": [{
            "x": 0.00018703824932198634,
            "y": 2,
            "seriesName": "某银行无锡诚业支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡春潮支行",
        "data": [{
            "x": 0.00018235696375655346,
            "y": 4,
            "seriesName": "某银行无锡春潮支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡春江支行",
        "data": [{
            "x": 0.0001291655902867476,
            "y": 1,
            "seriesName": "某银行无锡春江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡大浮支行",
        "data": [{
            "x": 0.0005324298160696999,
            "y": 11,
            "seriesName": "某银行无锡大浮支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡大王基支行",
        "data": [{
            "x": 0.0008,
            "y": 16,
            "seriesName": "某银行无锡大王基支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡稻香支行",
        "data": [{
            "x": 0.0004278990158322636,
            "y": 3,
            "seriesName": "某银行无锡稻香支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡丁村支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行无锡丁村支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡东北塘支行",
        "data": [{
            "x": 0.0011295408790464821,
            "y": 317,
            "seriesName": "某银行无锡东北塘支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡东港支行",
        "data": [{
            "x": -0.00028389734272087216,
            "y": -25,
            "seriesName": "某银行无锡东港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡东林(汇总)",
        "data": [{
            "x": 0.0004892367906066536,
            "y": 3,
            "seriesName": "某银行无锡东林(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡东林支行",
        "data": [{
            "x": -0.0001,
            "y": -2,
            "seriesName": "某银行无锡东林支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡东门支行",
        "data": [{
            "x": -0.0001579520697167756,
            "y": -28,
            "seriesName": "某银行无锡东门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡东亭支行",
        "data": [{
            "x": 0.00038703434929850025,
            "y": 4,
            "seriesName": "某银行无锡东亭支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡鹅湖支行",
        "data": [{
            "x": -0.0005,
            "y": -10,
            "seriesName": "某银行无锡鹅湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡二泉支行",
        "data": [{
            "x": -0.0002,
            "y": -3,
            "seriesName": "某银行无锡二泉支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡分行国际业务部",
        "data": [{
            "x": 0.0010332713370531101,
            "y": 5,
            "seriesName": "某银行无锡分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡分行票据中心",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 6,
            "seriesName": "某银行无锡分行票据中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡分行清算中心",
        "data": [{
            "x": -0.004396778435239974,
            "y": -428,
            "seriesName": "某银行无锡分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡分行银行卡部",
        "data": [{
            "x": -0.000004319262615486284,
            "y": 0,
            "seriesName": "某银行无锡分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡分行营业部",
        "data": [{
            "x": 0.00012303906490310673,
            "y": 2,
            "seriesName": "某银行无锡分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡风光里支行",
        "data": [{
            "x": -0.0002,
            "y": -6,
            "seriesName": "某银行无锡风光里支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡凤宾路支行",
        "data": [{
            "x": -0.000014658457930225741,
            "y": -1,
            "seriesName": "某银行无锡凤宾路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡凤翔支行",
        "data": [{
            "x": 0.0004,
            "y": 2,
            "seriesName": "某银行无锡凤翔支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡富城湾支行",
        "data": [{
            "x": -0.000027272727272727273,
            "y": -3,
            "seriesName": "某银行无锡富城湾支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡广电支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡广电支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡广石路支行",
        "data": [{
            "x": 0.0008157346143388018,
            "y": 9,
            "seriesName": "某银行无锡广石路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡广益(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡广益(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡广益支行",
        "data": [{
            "x": 0.000025,
            "y": 1,
            "seriesName": "某银行无锡广益支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡河惠支行",
        "data": [{
            "x": 0.00021864173029701638,
            "y": 13,
            "seriesName": "某银行无锡河惠支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡河埒(汇总)",
        "data": [{
            "x": 0.00032268473701193933,
            "y": 1,
            "seriesName": "某银行无锡河埒(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡河埒支行",
        "data": [{
            "x": 0.0002,
            "y": 7,
            "seriesName": "某银行无锡河埒支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡荷花里支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -2,
            "seriesName": "某银行无锡荷花里支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡红光支行",
        "data": [{
            "x": -0.0006423515204460489,
            "y": -49,
            "seriesName": "某银行无锡红光支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡鸿山支行",
        "data": [{
            "x": -0.00017358097552508245,
            "y": -2,
            "seriesName": "某银行无锡鸿山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡湖滨路支行",
        "data": [{
            "x": -0.000122118760494581,
            "y": -4,
            "seriesName": "某银行无锡湖滨路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡华建支行",
        "data": [{
            "x": 0.000468384074941452,
            "y": 1,
            "seriesName": "某银行无锡华建支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡华侨城支行",
        "data": [{
            "x": -0.0003,
            "y": -3,
            "seriesName": "某银行无锡华侨城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡华庄支行",
        "data": [{
            "x": 0.00039032006245121,
            "y": 3,
            "seriesName": "某银行无锡华庄支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡槐古支行",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行无锡槐古支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡黄泥头支行",
        "data": [{
            "x": 0.001,
            "y": 35,
            "seriesName": "某银行无锡黄泥头支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡黄巷(汇总)",
        "data": [{
            "x": 0.0003,
            "y": 3,
            "seriesName": "某银行无锡黄巷(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡黄巷支行",
        "data": [{
            "x": -0.0000659152330103487,
            "y": -1,
            "seriesName": "某银行无锡黄巷支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡会龙支行",
        "data": [{
            "x": 0.00015384615384615385,
            "y": 2,
            "seriesName": "某银行无锡会龙支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡会西支行",
        "data": [{
            "x": 0.0008,
            "y": 8,
            "seriesName": "某银行无锡会西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡惠泉支行",
        "data": [{
            "x": 0.00088261253309797,
            "y": 2,
            "seriesName": "某银行无锡惠泉支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡惠山支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行无锡惠山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡健康路支行（撤销）",
        "data": [{
            "x": -0.00010415348056896643,
            "y": -62,
            "seriesName": "某银行无锡健康路支行（撤销）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡建筑路支行",
        "data": [{
            "x": -0.0001,
            "y": -2,
            "seriesName": "某银行无锡建筑路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡江海东路支行",
        "data": [{
            "x": 0.0003943217665615142,
            "y": 1,
            "seriesName": "某银行无锡江海东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡江溪支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡江溪支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡江溪支行旧",
        "data": [{
            "x": 0.000732410338798847,
            "y": 682,
            "seriesName": "某银行无锡江溪支行旧",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡金达支行",
        "data": [{
            "x": 0.0009736842105263158,
            "y": 222,
            "seriesName": "某银行无锡金达支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡金海支行",
        "data": [{
            "x": -0.0002,
            "y": -20,
            "seriesName": "某银行无锡金海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡金星支行",
        "data": [{
            "x": -0.0030293839787768817,
            "y": -865,
            "seriesName": "某银行无锡金星支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡景丽苑支行",
        "data": [{
            "x": 0.0010625,
            "y": 170,
            "seriesName": "某银行无锡景丽苑支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡靖海支行",
        "data": [{
            "x": 0.002,
            "y": 1,
            "seriesName": "某银行无锡靖海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡开发支行",
        "data": [{
            "x": -0.0005181200719715881,
            "y": -164,
            "seriesName": "某银行无锡开发支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡科技支行",
        "data": [{
            "x": -0.000608,
            "y": -152,
            "seriesName": "某银行无锡科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡空港园支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡空港园支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡梨庄支行",
        "data": [{
            "x": 0.0003486142583231654,
            "y": 2,
            "seriesName": "某银行无锡梨庄支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡蠡湖支行",
        "data": [{
            "x": 0.00021,
            "y": 21,
            "seriesName": "某银行无锡蠡湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡蠡溪路支行",
        "data": [{
            "x": 0.0004837929366231253,
            "y": 5,
            "seriesName": "某银行无锡蠡溪路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡蠡园(汇总)",
        "data": [{
            "x": 0.0003872216844143272,
            "y": 4,
            "seriesName": "某银行无锡蠡园(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡蠡园开发区支行",
        "data": [{
            "x": 0.00028697149416491294,
            "y": 3,
            "seriesName": "某银行无锡蠡园开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡蠡园支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行无锡蠡园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡梁河支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -3,
            "seriesName": "某银行无锡梁河支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡梁青路支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡梁青路支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡梁溪(汇总)",
        "data": [{
            "x": 0.000690480574479838,
            "y": 15,
            "seriesName": "某银行无锡梁溪(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡梁溪支行",
        "data": [{
            "x": -0.0003333333333333333,
            "y": -5,
            "seriesName": "某银行无锡梁溪支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡刘潭支行",
        "data": [{
            "x": -0.0004,
            "y": -2,
            "seriesName": "某银行无锡刘潭支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡芦村支行",
        "data": [{
            "x": 0.0008948464151356879,
            "y": 50,
            "seriesName": "某银行无锡芦村支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡洛社支行",
        "data": [{
            "x": -0.00010331349331619095,
            "y": -17,
            "seriesName": "某银行无锡洛社支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡梅村支行",
        "data": [{
            "x": 0.0008,
            "y": 48,
            "seriesName": "某银行无锡梅村支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡梅园支行",
        "data": [{
            "x": 0.0009166666666666666,
            "y": 55,
            "seriesName": "某银行无锡梅园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡民丰支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行无锡民丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡南禅寺支行",
        "data": [{
            "x": -0.0006257542573637867,
            "y": -27,
            "seriesName": "某银行无锡南禅寺支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡南长街支行",
        "data": [{
            "x": 0.00063,
            "y": 63,
            "seriesName": "某银行无锡南长街支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡南泉(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡南泉(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡南泉支行",
        "data": [{
            "x": 0.0009,
            "y": 18,
            "seriesName": "某银行无锡南泉支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡南苑支行",
        "data": [{
            "x": 0.00084,
            "y": 42,
            "seriesName": "某银行无锡南苑支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡南站(汇总)",
        "data": [{
            "x": -0.0007919746568109821,
            "y": -30,
            "seriesName": "某银行无锡南站(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡南站支行",
        "data": [{
            "x": 0.00032733734451476136,
            "y": 85,
            "seriesName": "某银行无锡南站支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡宁海支行",
        "data": [{
            "x": 0.0002,
            "y": 4,
            "seriesName": "某银行无锡宁海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡藕塘支行",
        "data": [{
            "x": 0.0005438230762258679,
            "y": 6,
            "seriesName": "某银行无锡藕塘支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡前洲支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 6,
            "seriesName": "某银行无锡前洲支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡钱桥支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -2,
            "seriesName": "某银行无锡钱桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡勤新支行",
        "data": [{
            "x": -0.00046666666666666666,
            "y": -28,
            "seriesName": "某银行无锡勤新支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡沁园支行",
        "data": [{
            "x": -0.0002,
            "y": -2,
            "seriesName": "某银行无锡沁园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡青祁支行",
        "data": [{
            "x": 0.000579058518972682,
            "y": 34,
            "seriesName": "某银行无锡青祁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡人民西路支行",
        "data": [{
            "x": 0.0005333333333333334,
            "y": 16,
            "seriesName": "某银行无锡人民西路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡人民中路支行",
        "data": [{
            "x": -0.00024319066147859923,
            "y": -5,
            "seriesName": "某银行无锡人民中路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡任钱路支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行无锡任钱路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡荣巷支行",
        "data": [{
            "x": 0.0004932912391475928,
            "y": 5,
            "seriesName": "某银行无锡荣巷支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡瑞星支行",
        "data": [{
            "x": 0.002,
            "y": 1,
            "seriesName": "某银行无锡瑞星支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡山北(汇总)",
        "data": [{
            "x": 0.00102,
            "y": 51,
            "seriesName": "某银行无锡山北(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡山北支行",
        "data": [{
            "x": 0.000785071562292409,
            "y": 26,
            "seriesName": "某银行无锡山北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡山水城支行",
        "data": [{
            "x": -0.0003,
            "y": -12,
            "seriesName": "某银行无锡山水城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡上马墩支行",
        "data": [{
            "x": 0.0026,
            "y": 26,
            "seriesName": "某银行无锡上马墩支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡盛岸支行",
        "data": [{
            "x": 0.00043750911477322444,
            "y": 6,
            "seriesName": "某银行无锡盛岸支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡胜利(汇总)",
        "data": [{
            "x": 0.000763725379027901,
            "y": 233,
            "seriesName": "某银行无锡胜利(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡胜利支行",
        "data": [{
            "x": -0.00021796317173994738,
            "y": -55,
            "seriesName": "某银行无锡胜利支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡双河支行",
        "data": [{
            "x": 0.0006623978803267829,
            "y": 6,
            "seriesName": "某银行无锡双河支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡硕放支行",
        "data": [{
            "x": 0.00038703434929850025,
            "y": 4,
            "seriesName": "某银行无锡硕放支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡苏锡路支行",
        "data": [{
            "x": 0.0005214851898206091,
            "y": 5,
            "seriesName": "某银行无锡苏锡路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡太科园(汇总)",
        "data": [{
            "x": -0.00002,
            "y": -2,
            "seriesName": "某银行无锡太科园(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡太科园支行",
        "data": [{
            "x": -0.000056646472199327605,
            "y": -37,
            "seriesName": "某银行无锡太科园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡泰康支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行无锡泰康支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡天安支行",
        "data": [{
            "x": 0.00076,
            "y": 76,
            "seriesName": "某银行无锡天安支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡天河支行",
        "data": [{
            "x": 0.0006333333333333333,
            "y": 38,
            "seriesName": "某银行无锡天河支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡天湖支行",
        "data": [{
            "x": -0.0003502013657853266,
            "y": -4,
            "seriesName": "某银行无锡天湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡天惠支行",
        "data": [{
            "x": 0.0007,
            "y": 7,
            "seriesName": "某银行无锡天惠支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡通惠路支行",
        "data": [{
            "x": -0.0006428571428571428,
            "y": -18,
            "seriesName": "某银行无锡通惠路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡五爱北路支行",
        "data": [{
            "x": 0.00045155463811121145,
            "y": 14,
            "seriesName": "某银行无锡五爱北路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡五爱支行",
        "data": [{
            "x": 0.0006,
            "y": 30,
            "seriesName": "某银行无锡五爱支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡五星支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡五星支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡溪海(汇总)",
        "data": [{
            "x": -0.00025,
            "y": -5,
            "seriesName": "某银行无锡溪海(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡溪海支行",
        "data": [{
            "x": 0.0006042296072507553,
            "y": 2,
            "seriesName": "某银行无锡溪海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡溪海支行（停用）",
        "data": [{
            "x": 0.0005324298160696999,
            "y": 11,
            "seriesName": "某银行无锡溪海支行（停用）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡锡澄路支行",
        "data": [{
            "x": 0.0005817335660267597,
            "y": 1,
            "seriesName": "某银行无锡锡澄路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡锡海支行",
        "data": [{
            "x": 0.0004110658938627862,
            "y": 20,
            "seriesName": "某银行无锡锡海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡锡沪路支行",
        "data": [{
            "x": -0.0005,
            "y": -3,
            "seriesName": "某银行无锡锡沪路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡锡山(汇总)",
        "data": [{
            "x": 0.00007098743522396536,
            "y": 1,
            "seriesName": "某银行无锡锡山(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡锡山支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行无锡锡山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡锡西(汇总)",
        "data": [{
            "x": 0.00055,
            "y": 11,
            "seriesName": "某银行无锡锡西(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡锡西支行",
        "data": [{
            "x": 0.00094,
            "y": 47,
            "seriesName": "某银行无锡锡西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡香榭支行",
        "data": [{
            "x": -0.0004072283023670145,
            "y": -8,
            "seriesName": "某银行无锡香榭支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡小桃园支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡小桃园支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡新光支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行无锡新光支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡新联支行",
        "data": [{
            "x": 0.0008,
            "y": 8,
            "seriesName": "某银行无锡新联支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡新区(汇总)",
        "data": [{
            "x": 0.00043716908728809445,
            "y": 18,
            "seriesName": "某银行无锡新区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡新区支行",
        "data": [{
            "x": -0.0002,
            "y": -2,
            "seriesName": "某银行无锡新区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡新生路支行",
        "data": [{
            "x": 0.0006,
            "y": 6,
            "seriesName": "某银行无锡新生路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡新世纪支行",
        "data": [{
            "x": 0.0004556868143091945,
            "y": 149,
            "seriesName": "某银行无锡新世纪支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡鑫联支行",
        "data": [{
            "x": 0.00035341933203746247,
            "y": 2,
            "seriesName": "某银行无锡鑫联支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡叙丰支行",
        "data": [{
            "x": 0.0008731691119501187,
            "y": 143,
            "seriesName": "某银行无锡叙丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡学前东路支行",
        "data": [{
            "x": -0.000125,
            "y": -3,
            "seriesName": "某银行无锡学前东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡雪浪支行",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行无锡雪浪支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡扬名(汇总)",
        "data": [{
            "x": -0.00006364157067396423,
            "y": -3,
            "seriesName": "某银行无锡扬名(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡扬名支行",
        "data": [{
            "x": 0.0008461538461538462,
            "y": 11,
            "seriesName": "某银行无锡扬名支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡阳光支行",
        "data": [{
            "x": -0.0002,
            "y": -6,
            "seriesName": "某银行无锡阳光支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡阳山支行",
        "data": [{
            "x": 0.0009333333333333333,
            "y": 14,
            "seriesName": "某银行无锡阳山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡银光(汇总)",
        "data": [{
            "x": -0.00008652465087303373,
            "y": -38,
            "seriesName": "某银行无锡银光(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡银光支行",
        "data": [{
            "x": 0.0004,
            "y": 24,
            "seriesName": "某银行无锡银光支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡永丰支行",
        "data": [{
            "x": 0.00013333333333333334,
            "y": 2,
            "seriesName": "某银行无锡永丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡有为支行",
        "data": [{
            "x": 0.0007,
            "y": 35,
            "seriesName": "某银行无锡有为支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡渔港支行",
        "data": [{
            "x": 0.0045045045045045045,
            "y": 3,
            "seriesName": "某银行无锡渔港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡玉祁支行",
        "data": [{
            "x": -0.0003333333333333333,
            "y": -1,
            "seriesName": "某银行无锡玉祁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡袁家湾支行",
        "data": [{
            "x": -0.0001,
            "y": -1,
            "seriesName": "某银行无锡袁家湾支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡振华(汇总)",
        "data": [{
            "x": 0.00084,
            "y": 84,
            "seriesName": "某银行无锡振华(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡振华支行",
        "data": [{
            "x": -0.00004,
            "y": -2,
            "seriesName": "某银行无锡振华支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡职教园支行",
        "data": [{
            "x": 0.0018181818181818182,
            "y": 2,
            "seriesName": "某银行无锡职教园支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡中桥支行",
        "data": [{
            "x": 0.00072,
            "y": 36,
            "seriesName": "某银行无锡中桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡中山路支行",
        "data": [{
            "x": 0.0003938170719700699,
            "y": 4,
            "seriesName": "某银行无锡中山路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行无锡珠宝城支行",
        "data": [{
            "x": -0.00025,
            "y": -3,
            "seriesName": "某银行无锡珠宝城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行响水支行",
        "data": [{
            "x": 0.00038749677086024286,
            "y": 9,
            "seriesName": "某银行响水支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行新沂支行",
        "data": [{
            "x": 0.00012303906490310673,
            "y": 2,
            "seriesName": "某银行新沂支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行兴化戴南支行",
        "data": [{
            "x": -0.0001590852597564007,
            "y": -45,
            "seriesName": "某银行兴化戴南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行兴化支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行兴化支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盱眙淮河东路社区支行　",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行盱眙淮河东路社区支行　",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盱眙支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行盱眙支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州(汇总)",
        "data": [{
            "x": -0.000272,
            "y": -34,
            "seriesName": "某银行徐州(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州滨湖支行",
        "data": [{
            "x": 0.00030232216103149055,
            "y": 76,
            "seriesName": "某银行徐州滨湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州博爱支行",
        "data": [{
            "x": 0.0006,
            "y": 36,
            "seriesName": "某银行徐州博爱支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州城北支行",
        "data": [{
            "x": 0.0008666666666666666,
            "y": 26,
            "seriesName": "某银行徐州城北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州城东支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行徐州城东支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州城南支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 9,
            "seriesName": "某银行徐州城南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州分行国际业务部",
        "data": [{
            "x": 0.0009445472076823173,
            "y": 25,
            "seriesName": "某银行徐州分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州分行清算中心",
        "data": [{
            "x": -0.000725,
            "y": -29,
            "seriesName": "某银行徐州分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州分行银行卡部",
        "data": [{
            "x": 0.00004449823786978036,
            "y": 12,
            "seriesName": "某银行徐州分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州分行营业部",
        "data": [{
            "x": 0.00023801677102786627,
            "y": 54,
            "seriesName": "某银行徐州分行营业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州工业园区支行",
        "data": [{
            "x": -0.0001,
            "y": -2,
            "seriesName": "某银行徐州工业园区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州鼓楼支行",
        "data": [{
            "x": 0.0005858230814294083,
            "y": 8,
            "seriesName": "某银行徐州鼓楼支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州河清支行",
        "data": [{
            "x": -0.000058371274661863546,
            "y": -12,
            "seriesName": "某银行徐州河清支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州贾汪(汇总)",
        "data": [{
            "x": -0.000033333333333333335,
            "y": -1,
            "seriesName": "某银行徐州贾汪(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州贾汪支行",
        "data": [{
            "x": 0.0003,
            "y": 9,
            "seriesName": "某银行徐州贾汪支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州建西支行",
        "data": [{
            "x": -0.0003128844766874549,
            "y": -58,
            "seriesName": "某银行徐州建西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州解放桥支行",
        "data": [{
            "x": -0.0002,
            "y": -7,
            "seriesName": "某银行徐州解放桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州经济开发区支行",
        "data": [{
            "x": -0.00003979127943502505,
            "y": -47,
            "seriesName": "某银行徐州经济开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州开明支行",
        "data": [{
            "x": -0.00018689365750887744,
            "y": -75,
            "seriesName": "某银行徐州开明支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州科技支行",
        "data": [{
            "x": 0.0005365466058307462,
            "y": 134,
            "seriesName": "某银行徐州科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州奎山支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行徐州奎山支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州彭城支行",
        "data": [{
            "x": 0.00023446251639066664,
            "y": 56,
            "seriesName": "某银行徐州彭城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州泉山支行",
        "data": [{
            "x": -0.0006435855968843856,
            "y": -38,
            "seriesName": "某银行徐州泉山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州人民广场支行",
        "data": [{
            "x": 0.0007513853667699821,
            "y": 8,
            "seriesName": "某银行徐州人民广场支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州苏堤支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行徐州苏堤支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州苏苑支行",
        "data": [{
            "x": -0.0008258928571428572,
            "y": -73,
            "seriesName": "某银行徐州苏苑支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州天桥东支行",
        "data": [{
            "x": -0.0005,
            "y": -3,
            "seriesName": "某银行徐州天桥东支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州铜山支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行徐州铜山支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州新城区支行",
        "data": [{
            "x": -0.00015738342895568942,
            "y": -32,
            "seriesName": "某银行徐州新城区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州宣武支行",
        "data": [{
            "x": 0.0010666666666666667,
            "y": 32,
            "seriesName": "某银行徐州宣武支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州永康支行",
        "data": [{
            "x": -0.00019730676268929118,
            "y": -4,
            "seriesName": "某银行徐州永康支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行徐州云龙支行",
        "data": [{
            "x": -0.0005708697573058918,
            "y": -113,
            "seriesName": "某银行徐州云龙支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城(汇总)",
        "data": [{
            "x": -0.0003125,
            "y": -1,
            "seriesName": "某银行盐城(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城板桥支行",
        "data": [{
            "x": 0.0002720485253962336,
            "y": 56,
            "seriesName": "某银行盐城板桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城北支行",
        "data": [{
            "x": 0.0000461403589719928,
            "y": 1,
            "seriesName": "某银行盐城城北支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城东(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行盐城城东(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城东支行",
        "data": [{
            "x": 0.00075,
            "y": 15,
            "seriesName": "某银行盐城城东支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城南(汇总)",
        "data": [{
            "x": 0.00008756759123448411,
            "y": 4,
            "seriesName": "某银行盐城城南(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城南支行",
        "data": [{
            "x": 0.0005427899402931065,
            "y": 6,
            "seriesName": "某银行盐城城南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城西支行",
        "data": [{
            "x": 0.00034071550255536625,
            "y": 2,
            "seriesName": "某银行盐城城西支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城中(汇总)",
        "data": [{
            "x": -0.00013333333333333334,
            "y": -8,
            "seriesName": "某银行盐城城中(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城城中支行",
        "data": [{
            "x": 0.0016,
            "y": 16,
            "seriesName": "某银行盐城城中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城登瀛(汇总)",
        "data": [{
            "x": -0.0006666666666666666,
            "y": -40,
            "seriesName": "某银行盐城登瀛(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城登瀛支行",
        "data": [{
            "x": -0.00013157894736842105,
            "y": -5,
            "seriesName": "某银行盐城登瀛支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城东升(汇总)",
        "data": [{
            "x": 0.0004920942239432075,
            "y": 61,
            "seriesName": "某银行盐城东升(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城东升支行",
        "data": [{
            "x": 0.0003827018752391887,
            "y": 13,
            "seriesName": "某银行盐城东升支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城东台(汇总)",
        "data": [{
            "x": -0.000375,
            "y": -6,
            "seriesName": "某银行盐城东台(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城东闸支行",
        "data": [{
            "x": 0.0006,
            "y": 6,
            "seriesName": "某银行盐城东闸支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城分行国际业务部",
        "data": [{
            "x": 0.0001845614358879712,
            "y": 4,
            "seriesName": "某银行盐城分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城分行清算中心",
        "data": [{
            "x": 0.001,
            "y": 60,
            "seriesName": "某银行盐城分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城分行银行卡部",
        "data": [{
            "x": 0.0008563573263622842,
            "y": 19,
            "seriesName": "某银行盐城分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城分行营业部",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行盐城分行营业部", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城阜宁(汇总)",
        "data": [{
            "x": 0.0005911330049261083,
            "y": 3,
            "seriesName": "某银行盐城阜宁(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城国贸支行",
        "data": [{
            "x": -0.00013333333333333334,
            "y": -4,
            "seriesName": "某银行盐城国贸支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城环城支行",
        "data": [{
            "x": 0.002566380416543283,
            "y": 130,
            "seriesName": "某银行盐城环城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城黄海(汇总)",
        "data": [{
            "x": -0.00046031746031746033,
            "y": -29,
            "seriesName": "某银行盐城黄海(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城黄海支行",
        "data": [{
            "x": 0.0018093407214746127,
            "y": 32,
            "seriesName": "某银行盐城黄海支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城建湖(汇总)",
        "data": [{
            "x": 0.0028,
            "y": 14,
            "seriesName": "某银行盐城建湖(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城建军(汇总)",
        "data": [{
            "x": 0.0006326530612244898,
            "y": 31,
            "seriesName": "某银行盐城建军(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城建军支行",
        "data": [{
            "x": 0.0003989515337068916,
            "y": 224,
            "seriesName": "某银行盐城建军支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城解放路支行",
        "data": [{
            "x": 0.0004130524576621231,
            "y": 2,
            "seriesName": "某银行盐城解放路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城金穗(汇总)",
        "data": [{
            "x": 0.00032979189713832474,
            "y": 81,
            "seriesName": "某银行盐城金穗(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城金穗支行",
        "data": [{
            "x": 0.00016666666666666666,
            "y": 1,
            "seriesName": "某银行盐城金穗支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城开发(汇总)",
        "data": [{
            "x": 0.0005704506560182544,
            "y": 3,
            "seriesName": "某银行盐城开发(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城开发支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -2,
            "seriesName": "某银行盐城开发支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城开元支行",
        "data": [{
            "x": -0.00030642619506216076,
            "y": -14,
            "seriesName": "某银行盐城开元支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城南洋支行",
        "data": [{
            "x": -0.0006182188799376299,
            "y": -603,
            "seriesName": "某银行盐城南洋支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城前进支行",
        "data": [{
            "x": 0.000484027105517909,
            "y": 5,
            "seriesName": "某银行盐城前进支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城青墩支行",
        "data": [{
            "x": 0.00018733608092918696,
            "y": 2,
            "seriesName": "某银行盐城青墩支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城射阳(汇总)",
        "data": [{
            "x": 0.0005354957160342717,
            "y": 7,
            "seriesName": "某银行盐城射阳(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城双元支行",
        "data": [{
            "x": 0.0005229099941172626,
            "y": 16,
            "seriesName": "某银行盐城双元支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城泰山支行",
        "data": [{
            "x": 0.0006224324660774306,
            "y": 5,
            "seriesName": "某银行盐城泰山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城亭湖(汇总)",
        "data": [{
            "x": 0.00075,
            "y": 15,
            "seriesName": "某银行盐城亭湖(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城亭湖支行",
        "data": [{
            "x": -0.00015622016628768812,
            "y": -18,
            "seriesName": "某银行盐城亭湖支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城文峰(汇总)",
        "data": [{
            "x": -0.00016,
            "y": -4,
            "seriesName": "某银行盐城文峰(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城文峰支行",
        "data": [{
            "x": 0.0004356243949661181,
            "y": 9,
            "seriesName": "某银行盐城文峰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城五星支行",
        "data": [{
            "x": 0.0005166623611469904,
            "y": 2,
            "seriesName": "某银行盐城五星支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城先锋支行",
        "data": [{
            "x": 0.0003812428516965307,
            "y": 1,
            "seriesName": "某银行盐城先锋支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城新兴(汇总)",
        "data": [{
            "x": -0.00011666666666666667,
            "y": -7,
            "seriesName": "某银行盐城新兴(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城新兴支行",
        "data": [{
            "x": 0.0008333333333333334,
            "y": 25,
            "seriesName": "某银行盐城新兴支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城新洋(汇总)",
        "data": [{
            "x": 0.0004697040864255519,
            "y": 4,
            "seriesName": "某银行盐城新洋(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城新洋支行",
        "data": [{
            "x": -0.000339,
            "y": -339,
            "seriesName": "某银行盐城新洋支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城旭日支行",
        "data": [{
            "x": 0.00014473684210526317,
            "y": 55,
            "seriesName": "某银行盐城旭日支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城盐都(汇总)",
        "data": [{
            "x": 0.00014380040503780752,
            "y": 39,
            "seriesName": "某银行盐城盐都(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城盐都支行",
        "data": [{
            "x": -0.0001343341775635712,
            "y": -74,
            "seriesName": "某银行盐城盐都支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城营业部(汇总)",
        "data": [{
            "x": 0.0004652605459057072,
            "y": 12,
            "seriesName": "某银行盐城营业部(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城迎宾支行",
        "data": [{
            "x": 0.0004683292354525231,
            "y": 4,
            "seriesName": "某银行盐城迎宾支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城永丰支行",
        "data": [{
            "x": 0.0005,
            "y": 1,
            "seriesName": "某银行盐城永丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城园区支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行盐城园区支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城袁庄支行",
        "data": [{
            "x": 0.00075,
            "y": 15,
            "seriesName": "某银行盐城袁庄支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城越河(汇总)",
        "data": [{
            "x": 0.0003,
            "y": 6,
            "seriesName": "某银行盐城越河(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城越河支行",
        "data": [{
            "x": 0.00040562466197944835,
            "y": 6,
            "seriesName": "某银行盐城越河支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行盐城招商支行",
        "data": [{
            "x": 0.0002606639483140628,
            "y": 57,
            "seriesName": "某银行盐城招商支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬中（汇总）",
        "data": [{
            "x": 0.0003152485735002049,
            "y": 10,
            "seriesName": "某银行扬中（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬中支行",
        "data": [{
            "x": 0.0004194901581155211,
            "y": 13,
            "seriesName": "某银行扬中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州(汇总)",
        "data": [{
            "x": -0.00014285714285714287,
            "y": -10,
            "seriesName": "某银行扬州(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州城南支行",
        "data": [{
            "x": 0.0001979004326468011,
            "y": 305,
            "seriesName": "某银行扬州城南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州东城支行",
        "data": [{
            "x": 0.0003981078169640766,
            "y": 17,
            "seriesName": "某银行扬州东城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州东关支行",
        "data": [{
            "x": 0.000525,
            "y": 21,
            "seriesName": "某银行扬州东关支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州渡江桥支行",
        "data": [{
            "x": 0.00075,
            "y": 15,
            "seriesName": "某银行扬州渡江桥支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州方圈门支行",
        "data": [{
            "x": 0.0003089757454039858,
            "y": 2,
            "seriesName": "某银行扬州方圈门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州分行国际业务部",
        "data": [{
            "x": 0.000696217219772569,
            "y": 9,
            "seriesName": "某银行扬州分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州分行清算中心",
        "data": [{
            "x": -0.0006252420738117634,
            "y": -224,
            "seriesName": "某银行扬州分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州分行银行卡部",
        "data": [{
            "x": 0.0005864529371517936,
            "y": 6,
            "seriesName": "某银行扬州分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州分行营业部",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行扬州分行营业部", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州甘泉支行",
        "data": [{
            "x": 0.00015380356209049803,
            "y": 5,
            "seriesName": "某银行扬州甘泉支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州广陵支行",
        "data": [{
            "x": 0.0007204301075268817,
            "y": 67,
            "seriesName": "某银行扬州广陵支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州邗江支行",
        "data": [{
            "x": 0.0005356499381567798,
            "y": 168,
            "seriesName": "某银行扬州邗江支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州杭集杭府街小微支行",
        "data": [{
            "x": -0.00046666666666666666,
            "y": -7,
            "seriesName": "某银行扬州杭集杭府街小微支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州开发区支行",
        "data": [{
            "x": 0.0005,
            "y": 5,
            "seriesName": "某银行扬州开发区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州联谊支行",
        "data": [{
            "x": 0.0009,
            "y": 9,
            "seriesName": "某银行扬州联谊支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州梅岭支行",
        "data": [{
            "x": 0.00028587719298245616,
            "y": 6518,
            "seriesName": "某银行扬州梅岭支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州琼花支行",
        "data": [{
            "x": -0.0007,
            "y": -7,
            "seriesName": "某银行扬州琼花支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州润扬支行",
        "data": [{
            "x": -0.00045,
            "y": -36,
            "seriesName": "某银行扬州润扬支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州市区(汇总)",
        "data": [{
            "x": -0.00026097736021400144,
            "y": -4,
            "seriesName": "某银行扬州市区(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州蜀冈支行",
        "data": [{
            "x": 0.00022222222222222223,
            "y": 20,
            "seriesName": "某银行扬州蜀冈支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州唐城支行",
        "data": [{
            "x": 0.0008,
            "y": 40,
            "seriesName": "某银行扬州唐城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州文昌阁支行",
        "data": [{
            "x": 0.00088,
            "y": 22,
            "seriesName": "某银行扬州文昌阁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州新城支行",
        "data": [{
            "x": 0.0008,
            "y": 8,
            "seriesName": "某银行扬州新城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行扬州扬子江路支行",
        "data": [{
            "x": 0.003745318352059925,
            "y": 1,
            "seriesName": "某银行扬州扬子江路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行仪征支行",
        "data": [{"x": 0.0009, "y": 9, "seriesName": "某银行仪征支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宜兴(汇总)",
        "data": [{
            "x": 0.0004133085348212441,
            "y": 2,
            "seriesName": "某银行宜兴(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宜兴城东支行",
        "data": [{
            "x": 0.0005194805194805195,
            "y": 7,
            "seriesName": "某银行宜兴城东支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宜兴城中支行",
        "data": [{
            "x": 0.00015106881184379485,
            "y": 2,
            "seriesName": "某银行宜兴城中支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宜兴丁蜀支行",
        "data": [{
            "x": 0.0009,
            "y": 18,
            "seriesName": "某银行宜兴丁蜀支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宜兴万石支行",
        "data": [{
            "x": -0.00008852208966606744,
            "y": -11,
            "seriesName": "某银行宜兴万石支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行宜兴支行",
        "data": [{
            "x": -0.00006993006993006993,
            "y": -1,
            "seriesName": "某银行宜兴支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行营业部",
        "data": [{"x": 0.0015, "y": 9, "seriesName": "某银行营业部", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行营业部（汇总）",
        "data": [{
            "x": 0.0007959259384169168,
            "y": 133,
            "seriesName": "某银行营业部（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行张家港(汇总）",
        "data": [{
            "x": 0.0004,
            "y": 20,
            "seriesName": "某银行张家港(汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行张家港保税港区支行",
        "data": [{
            "x": 0.0008833333333333333,
            "y": 106,
            "seriesName": "某银行张家港保税港区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行张家港锦丰支行",
        "data": [{
            "x": -0.00002,
            "y": -1,
            "seriesName": "某银行张家港锦丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行张家港塘桥支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行张家港塘桥支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行张家港支行",
        "data": [{
            "x": 0.0007267441860465116,
            "y": 18,
            "seriesName": "某银行张家港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江(汇总)",
        "data": [{
            "x": -0.0011,
            "y": -11,
            "seriesName": "某银行镇江(汇总)",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江包家湾支行",
        "data": [{
            "x": -0.00006666666666666667,
            "y": -2,
            "seriesName": "某银行镇江包家湾支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江北固支行",
        "data": [{
            "x": 0.00009228497600590623,
            "y": 1,
            "seriesName": "某银行镇江北固支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江城南支行",
        "data": [{
            "x": -0.00011610114493590622,
            "y": -36,
            "seriesName": "某银行镇江城南支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江大港支行",
        "data": [{
            "x": 0.0005808325266214908,
            "y": 6,
            "seriesName": "某银行镇江大港支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江大港支行（汇总）",
        "data": [{
            "x": 0.0001,
            "y": 1,
            "seriesName": "某银行镇江大港支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江大市口支行",
        "data": [{
            "x": 0.00011889475436343748,
            "y": 5,
            "seriesName": "某银行镇江大市口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江大西路支行",
        "data": [{
            "x": -0.00027456647398843933,
            "y": -19,
            "seriesName": "某银行镇江大西路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江丹丰支行",
        "data": [{
            "x": -0.0005,
            "y": -5,
            "seriesName": "某银行镇江丹丰支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江丹徒新区支行",
        "data": [{
            "x": -0.0008237754261453647,
            "y": -51,
            "seriesName": "某银行镇江丹徒新区支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江丹徒支行",
        "data": [{
            "x": 0.0008735259250015598,
            "y": 14,
            "seriesName": "某银行镇江丹徒支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江电力路支行",
        "data": [{
            "x": -0.002467008191883661,
            "y": -833,
            "seriesName": "某银行镇江电力路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江丁卯支行",
        "data": [{
            "x": -0.000014336951820673407,
            "y": -8,
            "seriesName": "某银行镇江丁卯支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江东门支行",
        "data": [{
            "x": 0.00006751121877606131,
            "y": 19,
            "seriesName": "某银行镇江东门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江东吴支行",
        "data": [{
            "x": -0.0006,
            "y": -6,
            "seriesName": "某银行镇江东吴支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江分行国际业务部",
        "data": [{
            "x": -0.000029836496001909535,
            "y": -1,
            "seriesName": "某银行镇江分行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江分行清算中心",
        "data": [{
            "x": 0.00092,
            "y": 46,
            "seriesName": "某银行镇江分行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江分行银行卡部",
        "data": [{
            "x": 0.000525164113785558,
            "y": 6,
            "seriesName": "某银行镇江分行银行卡部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江分行营业部",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行镇江分行营业部", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江甘露支行",
        "data": [{
            "x": -0.00009228497600590623,
            "y": -1,
            "seriesName": "某银行镇江甘露支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江港口支行",
        "data": [{
            "x": -0.00016,
            "y": -8,
            "seriesName": "某银行镇江港口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江官塘支行",
        "data": [{
            "x": 0.00009444444444444444,
            "y": 17,
            "seriesName": "某银行镇江官塘支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江花山湾支行",
        "data": [{
            "x": 0.00005471911311261467,
            "y": 65,
            "seriesName": "某银行镇江花山湾支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江华电支行",
        "data": [{
            "x": 0.0006,
            "y": 120,
            "seriesName": "某银行镇江华电支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江华光支行",
        "data": [{
            "x": 0.00018456313904986895,
            "y": 10,
            "seriesName": "某银行镇江华光支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江环城支行",
        "data": [{
            "x": 0.0003890485301106326,
            "y": 166,
            "seriesName": "某银行镇江环城支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江环城支行（汇总）",
        "data": [{
            "x": 0.00021164648264710489,
            "y": 52,
            "seriesName": "某银行镇江环城支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江黄山支行",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行镇江黄山支行", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江谏壁支行",
        "data": [{
            "x": 0.002,
            "y": 2,
            "seriesName": "某银行镇江谏壁支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江江滨支行",
        "data": [{
            "x": 0.00017434642634915577,
            "y": 61,
            "seriesName": "某银行镇江江滨支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江蒋乔支行",
        "data": [{
            "x": -0.000028037383177570094,
            "y": -3,
            "seriesName": "某银行镇江蒋乔支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江金山支行",
        "data": [{
            "x": 0.00153825,
            "y": 6153,
            "seriesName": "某银行镇江金山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江京口支行",
        "data": [{
            "x": -0.0008,
            "y": -4,
            "seriesName": "某银行镇江京口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江京口支行（汇总）",
        "data": [{
            "x": -0.0005727005532503458,
            "y": -52,
            "seriesName": "某银行镇江京口支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江科技支行",
        "data": [{
            "x": 0.00055,
            "y": 55,
            "seriesName": "某银行镇江科技支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江龙门支行",
        "data": [{
            "x": -0.00005,
            "y": -1,
            "seriesName": "某银行镇江龙门支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江梦溪支行",
        "data": [{
            "x": 0.0000983477576711251,
            "y": 1,
            "seriesName": "某银行镇江梦溪支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江南郊支行",
        "data": [{
            "x": 0.00022605000226050002,
            "y": 5,
            "seriesName": "某银行镇江南郊支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江汝山支行",
        "data": [{
            "x": -0.00003214813862277374,
            "y": -43,
            "seriesName": "某银行镇江汝山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江润扬支行",
        "data": [{
            "x": 0.000484027105517909,
            "y": 5,
            "seriesName": "某银行镇江润扬支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江润州支行",
        "data": [{
            "x": 0.0016071428571428571,
            "y": 9,
            "seriesName": "某银行镇江润州支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江润州支行（汇总）",
        "data": [{
            "x": 0.0005438230762258679,
            "y": 12,
            "seriesName": "某银行镇江润州支行（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江山门口支行",
        "data": [{
            "x": 0.001,
            "y": 10,
            "seriesName": "某银行镇江山门口支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江太古山支行",
        "data": [{
            "x": 0.0007,
            "y": 28,
            "seriesName": "某银行镇江太古山支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江桃花坞支行",
        "data": [{
            "x": 0.000591833831270451,
            "y": 104,
            "seriesName": "某银行镇江桃花坞支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江西府支行",
        "data": [{
            "x": -0.00005,
            "y": -1,
            "seriesName": "某银行镇江西府支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江兴业支行",
        "data": [{
            "x": -0.000002085575326809654,
            "y": 1,
            "seriesName": "某银行镇江兴业支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江一泉支行",
        "data": [{
            "x": -0.00025,
            "y": -4,
            "seriesName": "某银行镇江一泉支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江营业部（汇总）",
        "data": [{
            "x": 0.0010545454545454545,
            "y": 29,
            "seriesName": "某银行镇江营业部（汇总）",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江招商支行",
        "data": [{
            "x": 0.00085,
            "y": 17,
            "seriesName": "某银行镇江招商支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江振兴支行",
        "data": [{
            "x": 0.00013334315861870523,
            "y": 40,
            "seriesName": "某银行镇江振兴支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行镇江中山东路支行",
        "data": [{
            "x": -0.000002079585746519293,
            "y": 1,
            "seriesName": "某银行镇江中山东路支行",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行自贸区分账核算单元",
        "data": [{
            "x": -0.0002845386097005778,
            "y": -13,
            "seriesName": "某银行自贸区分账核算单元",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行总行（汇总）",
        "data": [{"x": 0, "y": 0, "seriesName": "某银行总行（汇总）", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行总行放款中心",
        "data": [{
            "x": 0.0007814535035165407,
            "y": 15,
            "seriesName": "某银行总行放款中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行总行国际业务部",
        "data": [{
            "x": 0.0007166666666666667,
            "y": 43,
            "seriesName": "某银行总行国际业务部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行总行金融市场部",
        "data": [{
            "x": -0.0003,
            "y": -3,
            "seriesName": "某银行总行金融市场部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行总行金融同业部",
        "data": [{
            "x": 0.00015611827520529552,
            "y": 5,
            "seriesName": "某银行总行金融同业部",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行总行清算中心",
        "data": [{
            "x": -0.00004,
            "y": -2,
            "seriesName": "某银行总行清算中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "某银行总行信用卡中心",
        "data": [{
            "x": 0.0004917629702483403,
            "y": 10,
            "seriesName": "某银行总行信用卡中心",
            "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "盱眙支行(汇总)",
        "data": [{"x": 0, "y": 0, "seriesName": "盱眙支行(汇总)", "targetIds": ["462e3830690b7e9d", "42ac056eeaeacca5"]}],
        "yAxis": 0
    }]
}
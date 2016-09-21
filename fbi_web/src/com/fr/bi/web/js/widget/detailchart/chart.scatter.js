/**
 * 图表控件
 * @class BI.ScatterChart
 * @extends BI.Widget
 */
BI.ScatterChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.ScatterChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scatter-chart"
        })
    },

    _init: function () {
        BI.ScatterChart.superclass._init.apply(this, arguments);
        var self = this;
        this.xAxis = [{
            type: "value",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE,
            position: "bottom",
            gridLineWidth: 0
        }];
        this.yAxis = [{
            type: "value",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE,
            position: "left",
            gridLineWidth: 0
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.ScatterChart.EVENT_CHANGE, obj);
        });
    },


    _formatConfig: function (config, items) {
        var self = this;
        config.colors = this.config.chart_color;
        config.style = formatChartStyle();
        config.plotOptions.marker = {"symbol": "circle", "radius": 4.5, "enabled": true};
        formatCordon();
        this.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.plotOptions.dataLabels.formatter.identifier = "${X}${Y}";

        config.yAxis = this.yAxis;
        config.xAxis = this.xAxis;

        config.yAxis[0].formatter = self.formatTickInXYaxis(this.config.left_y_axis_style, this.config.left_y_axis_number_level, this.config.num_separators);
        formatNumberLevelInYaxis(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = getXYAxisUnit(this.config.left_y_axis_number_level, this.constants.LEFT_AXIS);
        config.yAxis[0].title.text = this.config.show_left_y_axis_title === true ? this.config.left_y_axis_title + config.yAxis[0].title.text : config.yAxis[0].title.text;
        config.yAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.yAxis[0].title.rotation = this.constants.ROTATION;

        config.xAxis[0].formatter = self.formatTickInXYaxis(this.config.x_axis_style, this.config.x_axis_number_level, this.config.right_num_separators);
        formatNumberLevelInXaxis(this.config.x_axis_number_level, this.constants.X_AXIS);
        config.xAxis[0].title.text = getXYAxisUnit(this.config.x_axis_number_level, this.constants.X_AXIS);
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? this.config.x_axis_title + config.xAxis[0].title.text : config.xAxis[0].title.text;
        config.xAxis[0].title.align = "center";
        config.xAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.chartType = "scatter";

        if (BI.isNotEmptyArray(this.config.tooltip)) {
            config.plotOptions.tooltip.formatter = function () {
                var y = self.formatTickInXYaxis(self.config.left_y_axis_style, self.config.left_y_axis_number_level, self.config.num_separators)(this.y);
                var x = self.formatTickInXYaxis(self.config.x_axis_style, self.config.x_axis_number_level, self.config.right_num_separators)(this.x);
                return this.seriesName + '<div>(X)' + self.config.tooltip[0]
                    + ':' + x + '</div><div>(Y)' + self.config.tooltip[1] + ':' + y + '</div>'
            };
        }

        if (config.plotOptions.dataLabels.enabled === true) {
            BI.each(items, function (idx, item) {
                item.dataLabels = {
                    "style": self.constants.FONT_STYLE,
                    "align": "outside",
                    enabled: true,
                    formatter: {
                        identifier: "${X}${Y}",
                        "XFormat": function () {
                            return BI.contentFormat(arguments[0], '#.##')
                        },
                        "YFormat": function () {
                            return BI.contentFormat(arguments[0], '#.##')
                        }
                    }
                };
                item.dataLabels.formatter.XFormat = config.xAxis[0].formatter;
                item.dataLabels.formatter.YFormat = config.yAxis[0].formatter;
            });
        }

        //全局样式图表文字
        config.yAxis[0].title.style = config.yAxis[0].labelStyle = this.config.chart_font;
        config.xAxis[0].title.style = config.xAxis[0].labelStyle = this.config.chart_font;
        config.plotOptions.dataLabels.style = this.config.chart_font;
        config.plotOptions.legend.style = this.config.chart_font;

        return [items, config];

        function formatChartStyle() {
            switch (self.config.chart_style) {
                case BICst.CHART_STYLE.STYLE_GRADUAL:
                    return "gradual";
                case BICst.CHART_STYLE.STYLE_NORMAL:
                default:
                    return "normal";
            }
        }

        function formatCordon() {
            BI.each(self.config.cordon, function (idx, cor) {
                if (idx === 0 && self.xAxis.length > 0) {
                    var magnify = self.calcMagnify(self.config.x_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": self.config.chart_font,
                                "text": t.text,
                                "align": "top"
                            }
                        });
                    });
                }
                if (idx > 0 && self.yAxis.length >= idx) {
                    var magnify = 1;
                    switch (idx - 1) {
                        case self.constants.LEFT_AXIS:
                            magnify = self.calcMagnify(self.config.left_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS:
                            magnify = self.calcMagnify(self.config.right_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS_SECOND:
                            magnify = self.calcMagnify(self.config.right_y_axis_second_number_level);
                            break;
                    }
                    self.yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": self.config.chart_font,
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }

        function formatNumberLevelInXaxis(type) {
            var magnify = self.calcMagnify(type);
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                    da.x = self.formatXYDataWithMagnify(da.x, magnify);
                })
            })
        }

        function formatNumberLevelInYaxis(type, position) {
            var magnify = self.calcMagnify(type);
            BI.each(items, function (idx, item) {
                BI.each(item.data, function (id, da) {
                    if (position === item.yAxis) {
                        da.y = self.formatXYDataWithMagnify(da.y, magnify);
                    }
                })
            });
            if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                config.plotOptions.tooltip.formatter.valueFormat = function () {
                    return BI.contentFormat(arguments[0], '#0%')
                };
            }
        }

        function getXYAxisUnit(numberLevelType, position) {
            var unit = "";
            switch (numberLevelType) {
                case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    unit = "";
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                    unit = BI.i18nText("BI-Wan");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                    unit = BI.i18nText("BI-Million");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                    unit = BI.i18nText("BI-Yi");
                    break;
            }
            if (position === self.constants.X_AXIS) {
                self.config.x_axis_unit !== "" && (unit = unit + self.config.x_axis_unit)
            }
            if (position === self.constants.LEFT_AXIS) {
                self.config.left_y_axis_unit !== "" && (unit = unit + self.config.left_y_axis_unit)
            }
            if (position === self.constants.RIGHT_AXIS) {
                self.config.right_y_axis_unit !== "" && (unit = unit + self.config.right_y_axis_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
        }
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            chart_color: options.chart_color || [],
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            x_axis_style: options.x_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            x_axis_number_level: options.x_axis_number_level || c.NORMAL,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            cordon: options.cordon || [],
            tooltip: options.tooltip || [],
            num_separators: options.num_separators || false,
            right_num_separators: options.right_num_separators || false,
            chart_font: options.chart_font || c.FONT_STYLE
        };
        this.options.items = items;
        var types = [];
        BI.each(items, function (idx, axisItems) {
            var type = [];
            BI.each(axisItems, function (id, item) {
                type.push(BICst.WIDGET.SCATTER);
            });
            types.push(type);
        });
        this.combineChart.populate(items, types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function () {
        this.combineChart.magnify();
    }
});
BI.ScatterChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.scatter_chart', BI.ScatterChart);
var a = {
    "title": "",
    "chartType": "scatter",
    "plotOptions": {
        "rotatable": false,
        "startAngle": 0,
        "borderRadius": 0,
        "endAngle": 360,
        "innerRadius": "0.0%",
        "layout": "horizontal",
        "hinge": "rgb(101,107,109)",
        "dataLabels": {
            "autoAdjust": true,
            "style": {"fontFamily": "inherit", "color": "inherit", "fontSize": "12px"},
            "formatter": {"identifier": "${X}${Y}"},
            "align": "outside",
            "enabled": false
        },
        "percentageLabel": {
            "formatter": {"identifier": "${PERCENT}"},
            "style": {"fontFamily": "inherit", "color": "inherit", "fontSize": "12px"},
            "align": "bottom",
            "enabled": true
        },
        "valueLabel": {
            "formatter": {"identifier": "${SERIES}${VALUE}"},
            "backgroundColor": "rgb(255,255,0)",
            "style": {"fontFamily": "inherit", "color": "inherit", "fontSize": "12px"},
            "align": "inside",
            "enabled": true
        },
        "hingeBackgroundColor": "rgb(220,242,249)",
        "seriesLabel": {
            "formatter": {"identifier": "${CATEGORY}"},
            "style": {"fontFamily": "inherit", "color": "inherit", "fontSize": "12px"},
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
            "shared": false,
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
        "maxSize": 80,
        "fillColorOpacity": 1,
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
            "maxSize": 80,
            "minSize": 15,
            "lineWidth": 0,
            "animation": true,
            "fillColorOpacity": 0.699999988079071,
            "marker": {"symbol": "circle", "radius": 28.39695010101295, "enabled": true}
        },
        "marker": {"symbol": "circle", "radius": 4.5, "enabled": true}
    },
    "dTools": {
        "enabled": false,
        "style": {"fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#1a1a1a", "fontSize": "12px"},
        "backgroundColor": "white"
    },
    "dataSheet": {
        "enabled": false,
        "borderColor": "rgb(0,0,0)",
        "borderWidth": 1,
        "style": {"fontFamily": "inherit", "color": "inherit", "fontSize": "12px"}
    },
    "borderColor": "rgb(238,238,238)",
    "shadow": false,
    "legend": {
        "borderColor": "rgb(204,204,204)",
        "borderRadius": 0,
        "shadow": false,
        "borderWidth": 0,
        "visible": true,
        "style": {"fontFamily": "inherit", "color": "inherit", "fontSize": "12px"},
        "position": "bottom",
        "enabled": true,
        "maxHeight": 80
    },
    "rangeLegend": {
        "range": {
            "min": 0,
            "color": [[0, "rgb(182,226,255)"], [0.5, "rgb(109,196,255)"], [1, "rgb(36,167,255)"]],
            "max": 266393
        }, "enabled": false
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
    "colors": ["#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed"],
    "borderRadius": 0,
    "borderWidth": 0,
    "style": "normal",
    "plotShadow": false,
    "plotBorderRadius": 0,
    "xAxis": [{
        "type": "value",
        "title": {"style": {"color": "purple"}, "text": "省", "align": "center"},
        "labelStyle": {"color": "purple"},
        "position": "bottom",
        "gridLineWidth": 1,
        "plotLines": []
    }],
    "yAxis": [{
        "type": "value",
        "title": {"style": {"color": "purple"}, "text": "城市地区维度表记录数", "rotation": -90},
        "labelStyle": {"color": "purple"},
        "position": "left",
        "gridLineWidth": 1,
        "plotLines": []
    }],
    "series": [{
        "type": "scatter",
        "name": "安徽省",
        "data": [{"x": "83", "y": "83", "seriesName": "安徽省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "北京市",
        "data": [{"x": "3", "y": "3", "seriesName": "北京市", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "福建省",
        "data": [{"x": "69", "y": "69", "seriesName": "福建省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "甘肃省",
        "data": [{"x": "81", "y": "81", "seriesName": "甘肃省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "广东省",
        "data": [{"x": "89", "y": "89", "seriesName": "广东省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "广西壮族自治区",
        "data": [{
            "x": "93",
            "y": "93",
            "seriesName": "广西壮族自治区",
            "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]
        }],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "贵州省",
        "data": [{"x": "80", "y": "80", "seriesName": "贵州省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "海南省",
        "data": [{"x": "19", "y": "19", "seriesName": "海南省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "河北省",
        "data": [{"x": "152", "y": "152", "seriesName": "河北省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "河南省",
        "data": [{"x": "128", "y": "128", "seriesName": "河南省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "黑龙江省",
        "data": [{"x": "78", "y": "78", "seriesName": "黑龙江省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "湖北省",
        "data": [{"x": "79", "y": "79", "seriesName": "湖北省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }, {
        "type": "scatter",
        "name": "湖南省",
        "data": [{"x": "101", "y": "101", "seriesName": "湖南省", "targetIds": ["b43ac0fcada2fc6c", "29c89ecc2a0b96a0"]}],
        "yAxis": 0
    }]
}
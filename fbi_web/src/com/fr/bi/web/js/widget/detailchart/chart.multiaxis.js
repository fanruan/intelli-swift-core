/**
 * 组合图
 * @class BI.MultiAxisChart
 * @extends BI.Widget
 * leftYxis 左值轴属性
 * rightYxis 右值轴属性
 * xAxis    分类轴属性
 */
BI.MultiAxisChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.MultiAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-axis-chart"
        })
    },

    _init: function () {
        BI.MultiAxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.xAxis = [{
            type: "category",
            title: {
                style: {"fontFamily": "inherit", "color": "#808080", "fontSize": "12px", "fontWeight": ""}
            },
            labelStyle: {
                "fontFamily": "inherit", "color": "#808080", "fontSize": "12px"
            },
            position: "bottom",
            gridLineWidth: 0
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            popupItemsGetter: o.popupItemsGetter,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.MultiAxisChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function (config, items) {
        var self = this, o = this.options;
        config.colors = this.config.chartColor;
        config.plotOptions.style = this.formatChartStyle();
        this.formatCordon();
        this.formatChartLegend(config, this.config.legend);
        config.plotOptions.dataLabels.enabled = this.config.showDataLabel;
        config.dataSheet.enabled = this.config.showDataTable;
        config.xAxis[0].showLabel = !config.dataSheet.enabled;
        this.formatZoom(config, this.config.showZoom);

        config.yAxis = this.yAxis;
        config.plotOptions.connectNulls = this.config.nullContinuity;
        BI.each(config.yAxis, function (idx, axis) {
            switch (axis.axisIndex) {
                case self.constants.LEFT_AXIS:
<<<<<<< HEAD
                    BI.extend(axis, self.leftAxisSetting(self.config));
                    self.formatNumberLevelInYaxis(config, items, self.config.leftYNumberLevel, idx, axis.formatter);
                    break;
                case self.constants.RIGHT_AXIS:
                    BI.extend(axis, self.rightAxisSetting(self.config));
                    self.formatNumberLevelInYaxis(config, items, self.config.rightYNumberLevel, idx, axis.formatter);
                    break;
                case self.constants.RIGHT_AXIS_SECOND:
                    BI.extend(axis, self.right2AxisSetting(self.config));
                    self.formatNumberLevelInYaxis(config, items, self.config.rightY2NumberLevel, idx, axis.formatter);
=======
                    title = self.getXYAxisUnit(self.config.left_y_axis_number_level, self.constants.LEFT_AXIS);
                    axis.title.text = self.config.show_left_y_axis_title === true ? self.config.left_y_axis_title + title : title;
                    axis.title.rotation = self.constants.ROTATION;
                    BI.extend(axis, {
                        lineWidth: self.config.line_width,
                        showLabel: self.config.show_label,
                        enableTick: self.config.enable_tick,
                        reversed: self.config.left_y_axis_reversed,
                        enableMinorTick: self.config.enable_minor_tick,
                        gridLineWidth: self.config.show_grid_line === true ? 1 : 0,
                        formatter: self.formatTickInXYaxis(self.config.left_y_axis_style, self.config.left_y_axis_number_level, self.config.num_separators)
                    });
                    self.formatNumberLevelInYaxis(config, items, self.config.left_y_axis_number_level, idx, axis.formatter);
                    break;
                case self.constants.RIGHT_AXIS:
                    title = self.getXYAxisUnit(self.config.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                    axis.title.text = self.config.show_right_y_axis_title === true ? self.config.right_y_axis_title + title : title;
                    axis.title.rotation = self.constants.ROTATION;
                    BI.extend(axis, {
                        lineWidth: self.config.line_width,
                        showLabel: self.config.show_label,
                        enableTick: self.config.enable_tick,
                        reversed: self.config.right_y_axis_reversed,
                        enableMinorTick: self.config.enable_minor_tick,
                        gridLineWidth: self.config.show_grid_line === true ? 1 : 0,
                        formatter: self.formatTickInXYaxis(self.config.right_y_axis_style, self.config.right_y_axis_number_level, self.config.right_num_separators)
                    });
                    self.formatNumberLevelInYaxis(config, items, self.config.right_y_axis_number_level, idx, axis.formatter);
                    break;
                case self.constants.RIGHT_AXIS_SECOND:
                    title = self.getXYAxisUnit(self.config.right_y_axis_second_number_level, self.constants.RIGHT_AXIS_SECOND);
                    axis.title.text = self.config.show_right_y_axis_second_title === true ? self.config.right_y_axis_second_title + title : title;
                    axis.title.rotation = self.constants.ROTATION;
                    BI.extend(axis, {
                        lineWidth: self.config.line_width,
                        showLabel: self.config.show_label,
                        enableTick: self.config.enable_tick,
                        reversed: self.config.right_y_axis_second_reversed,
                        enableMinorTick: self.config.enable_minor_tick,
                        gridLineWidth: self.config.show_grid_line === true ? 1 : 0,
                        formatter: self.formatTickInXYaxis(self.config.right_y_axis_second_style, self.config.right_y_axis_second_number_level, self.config.right2_num_separators)
                    });
                    self.formatNumberLevelInYaxis(config, items, self.config.right_y_axis_second_number_level, idx, axis.formatter);
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
                    break;
                default:
                    break;
            }
        });

        BI.extend(config.xAxis[0], self.catSetting(this.config));

        config.legend.style = BI.extend(this.config.legendStyle, {
            fontSize: this.config.legendStyle.fontSize + "px"
        });

        var lineItem = [];
        var otherItem = [];
        BI.each(items, function (idx, item) {
            if(config.yAxis[item.yAxis]) {
                config.yAxis[item.yAxis].lineColor = self.config.chartColor[0];
                config.yAxis[item.yAxis].tickColor = self.config.chartColor[0];
            }
            item.color = [self.config.chartColor[idx % self.config.chartColor.length]];
            if (item.type === "line") {
                config.chartType = "line";
                lineItem.push(item);
            } else if(item.type === "area") {
                config.chartType = "area";
                otherItem.push(item);
            }else {
                otherItem.push(item);
            }
        });

        self.formatDataLabelForAxis(items, config, this.config);

        //全局样式的图表文字
        if (config.dataSheet) {
            config.dataSheet.style = this.config.chartFont;
        }
        config.plotOptions.dataLabels.style = this.config.chartFont;

        return [BI.concat(otherItem, lineItem), config];
    },

    formatChartStyle: function () {
        switch (this.config.chartStyle) {
            case BICst.CHART_STYLE.STYLE_GRADUAL:
                return "gradual";
            case BICst.CHART_STYLE.STYLE_NORMAL:
            default:
                return "normal";
        }
    },

    formatCordon: function () {
        var self = this;
        var magnify = 1;
        BI.each(this.config.cordon, function (idx, cor) {
            if (idx === 0 && self.xAxis.length > 0) {
                magnify = self.calcMagnify(1);
                self.xAxis[0].plotLines = BI.map(cor, function (i, t) {
                    return BI.extend(t, {
                        value: t.value.div(magnify),
                        width: 1,
                        label: {
                            "style": {
                                "fontFamily": "inherit",
                                "color": "#808080",
                                "fontSize": "12px",
                                "fontWeight": ""
                            },
                            "text": t.text,
                            "align": "top"
                        }
                    });
                });
            }
            if (idx > 0 && self.yAxis.length >= idx) {
                magnify = 1;
                switch (idx - 1) {
                    case self.constants.LEFT_AXIS:
                        magnify = self.calcMagnify(self.config.leftYNumberLevel);
                        break;
                    case self.constants.RIGHT_AXIS:
                        magnify = self.calcMagnify(self.config.rightYNumberLevel);
                        break;
                    case self.constants.RIGHT_AXIS_SECOND:
                        magnify = self.calcMagnify(self.config.rightY2NumberLevel);
                        break;
                    default:
                        break;
                }
                self.yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                    return BI.extend(t, {
                        value: t.value.div(magnify),
                        width: 1,
                        label: {
                            "style": {
                                "fontFamily": "inherit",
                                "color": "#808080",
                                "fontSize": "12px",
                                "fontWeight": ""
                            },
                            "text": t.text,
                            "align": "left"
                        }
                    });
                });
            }
        })
    },

    formatSeriesAccumulation: function (items, accumulationObj) {
        var accumulations = accumulationObj.items,
            type = accumulationObj.type,
            result = [];
        if(BI.isEmpty(accumulationObj) || BI.isEmpty(items) || type !== BICst.SERIES_ACCUMULATION.EXIST) {
            return items;
        }
        BI.each(accumulations, function (idx, accumulation) {
            accumulation.stack = idx + BI.UUID();
        });
        BI.each(items[0], function (idx, item) {
            BI.any(accumulations.slice(1), function (i, accumulation) {
                if(BI.contains(accumulation.items, item.name)) {
                    switch (accumulation.type) {
                        case BICst.ACCUMULATE_TYPE.COLUMN:
                            item.type = "column";
                            break;
                        case BICst.ACCUMULATE_TYPE.AREA_NORMAL:
                            item.type = "area";
                            break;
                        case BICst.ACCUMULATE_TYPE.AREA_CURVE:
                            item.type = "area";
                            item.curve = true;
                            break;
                        case BICst.ACCUMULATE_TYPE.AREA_RIGHT_ANGLE:
                            item.type = "area";
                            item.step = true;
                    };
                    item.flag = true;
                    item.stack = accumulation.stack;
                }
            });
            if(!item.flag) {
                switch (accumulations[0].type) {
                    case BICst.ACCUMULATE_TYPE.COLUMN:
                        item.type = "column";
                        break;
                    case BICst.ACCUMULATE_TYPE.AREA_NORMAL:
                        item.type = "area";
                        break;
                    case BICst.ACCUMULATE_TYPE.AREA_CURVE:
                        item.type = "area";
                        item.curve = true;
                        break;
                    case BICst.ACCUMULATE_TYPE.AREA_RIGHT_ANGLE:
                        item.type = "area";
                        item.step = true;
                }
                item.stack = accumulations[0].stack;
            }
            result.push(item);
        })
        return [result];
    },

    populate: function (items, options, types) {
        //按照系列分组堆积
        items = this.formatSeriesAccumulation(items, options.seriesAccumulation);
        var self = this, c = this.constants;
        this.config = self.getChartConfig(options);
        this.options.items = items;
        this.yAxis = [];
        BI.each(types, function (idx, type) {
            if (BI.isEmptyArray(type)) {
                return;
            }
            var newYAxis = {
                type: "value",
                title: {
                    style: {"fontFamily": "inherit", "color": "#808080", "fontSize": "12px", "fontWeight": ""}
                },
                labelStyle: {
                    "fontFamily": "inherit", "color": "#808080", "fontSize": "12px"
                },
                position: idx > 0 ? "right" : "left",
                lineWidth: 1,
                axisIndex: idx,
                gridLineWidth: 0
            };
            self.yAxis.push(newYAxis);
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
BI.MultiAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.multi_axis_chart', BI.MultiAxisChart);

/**
 * 图表控件
 * @class BI.AbstractChart
 * @extends BI.Widget
 */
BI.AbstractChart = BI.inherit(BI.Widget, {

    constants: {
        INNER: 1,
        OUTER: 2,
        CENTER: 3,
        REVERSE: false,
        SHOW_AXIS_LABEL: true,
        SEPARATOR: false,
        LEFT_AXIS: 0,
        RIGHT_AXIS: 1,
        RIGHT_AXIS_SECOND: 2,
        X_AXIS: 3,
        ROTATION: -90,
        NORMAL: 1,
        LEGEND_BOTTOM: 4,
        ZERO2POINT: 2,
        ONE2POINT: 3,
        TWO2POINT: 4,
        MINLIMIT: 1e-5,
        LEGEND_HEIGHT: 80,
        LEGEND_WIDTH: "30.0%",
        FIX_COUNT: 6,
        STYLE_NORMAL: 21,
        NO_PROJECT: 16,
        DASHBOARD_AXIS: 4,
        ONE_POINTER: 1,
        MULTI_POINTER: 2,
        HALF_DASHBOARD: 9,
        PERCENT_DASHBOARD: 10,
        PERCENT_SCALE_SLOT: 11,
        VERTICAL_TUBE: 12,
        HORIZONTAL_TUBE: 13,
        LNG_FIRST: 3,
        LAT_FIRST: 4,
        theme_color: "#65bce7",
        auto_custom: 1,
        POLYGON: 7,
        AUTO_CUSTOM: 1,
        AUTO: 1,
        NOT_SHOW: 2,
        LINE_WIDTH: 1,
        BUBBLE_MIN_SIZE: 15,
        BUBBLE_MAX_SIZE: 80,
        RULE_DISPLAY: 1,
        NUM_SEPARATORS: false,
        FONT_STYLE: {
            "fontFamily": "inherit",
            "color": "inherit",
            "fontSize": "12px"
        },
        CUSTOM_SCALE: {
            maxScale: {
                scale: null
            },
            minScale: {
                scale: null
            },
            interval: {
                scale: null
            }
        },
        LEFT_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        },
        RIGHT_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        },
        RIGHT2_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        },
        CAT_LABEL_STYLE: {
            text_direction: 0,
            text_style: {}
        },
        DATA_SETTING_STYLE: {
            showCategoryName: true,
            showSeriesName: true,
            showValue: true,
            showPercentage: false,
            position: BICst.DATA_LABEL.POSITION_OUTER,
            textStyle: {}
        }
    },

    _defaultConfig: function () {
        return BI.extend(BI.AbstractChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-abstract-chart",
            popupItemsGetter: BI.emptyFn
        })
    },

    _init: function () {
        BI.AbstractChart.superclass._init.apply(this, arguments);
    },

    formatZoom: function (config, show_zoom) {
        config.zoom.zoomTool.enabled = this.config.showZoom;
        if (show_zoom === true) {
            delete config.dataSheet;
            config.zoom.zoomType = "";
        }
    },

    /**
     * 格式化坐标轴数量级及其所影响的系列的各项属性
     * @param config  配置信息
     * @param items  系列数据
     * @param type  坐标轴数量级
     * @param position 坐标轴位置
     * @param formatter 系列tooltip格式化内容
     */
    formatNumberLevelInYaxis: function (config, items, type, position, formatter) {
        var magnify = this.calcMagnify(type);
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                if (position === item.yAxis) {
                    if (BI.isNotNull(da.y) && !BI.isNumber(da.y)) {
                        da.y = BI.parseFloat(da.y);
                    }
                    if (BI.isNotNull(da.y)) {
                        da.y = BI.contentFormat(BI.parseFloat(da.y.div(magnify).toFixed(4)), "#.####;-#.####");
                    }
                }
            });
            if (position === item.yAxis) {
                item.tooltip = BI.deepClone(config.plotOptions.tooltip);
                item.tooltip.formatter.valueFormat = formatter;
            }
        });
    },

    formatNumberLevelInXaxis: function (items, type) {
        var magnify = this.calcMagnify(type);
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                if (BI.isNotNull(da.x) && !BI.isNumber(da.x)) {
                    da.x = BI.parseFloat(da.x);
                }
                if (BI.isNotNull(da.x)) {
                    da.x = BI.contentFormat(BI.parseFloat(da.x.div(magnify).toFixed(4)), "#.####;-#.####");
                }
            });
        })
    },

    formatXYDataWithMagnify: function (number, magnify) {
        if (BI.isNull(number)) {
            return null
        }
        if (!BI.isNumber(number)) {
            number = BI.parseFloat(number);
        }
        if (BI.isNotNull(number)) {
            return BI.contentFormat(BI.parseFloat(number.div(magnify).toFixed(4)), "#.####;-#.####");
        }
    },

    formatToolTipAndDataLabel: function (type, numberLevel, unit, separators) {
        var formatter = this.formatNumberLevelAndSeparators(type, separators);
        formatter += this.getXYAxisUnit(numberLevel, unit);

        return formatter;
    },

    calcMagnify: function (type) {
        var magnify = 1;
        switch (type) {
            case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                magnify = 1;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                magnify = 10000;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                magnify = 1000000;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                magnify = 100000000;
                break;
        }
        return magnify;
    },

    formatChartLegend: function (config, chartLegend) {
        switch (chartLegend) {
            case BICst.CHART_LEGENDS.BOTTOM:
                config.legend.enabled = true;
                config.legend.position = "bottom";
                config.legend.maxHeight = this.constants.LEGEND_HEIGHT;
                break;
            case BICst.CHART_LEGENDS.RIGHT:
                config.legend.enabled = true;
                config.legend.position = "right";
                config.legend.maxWidth = this.constants.LEGEND_WIDTH;
                break;
            case BICst.CHART_LEGENDS.NOT_SHOW:
            default:
                config.legend.enabled = false;
                break;
        }
    },

    getXYAxisUnit: function (numberLevelType, axisUnit) {
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
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                unit += '%';
                break;
        }
        return (BI.isEmptyString(unit) && BI.isEmptyString(axisUnit)) ? unit : (unit + axisUnit);
    },

    formatTickInXYaxis: function (type, numberLevel, separators) {
        var formatter = this.formatNumberLevelAndSeparators(type, separators);
        if (numberLevel === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
            formatter += '%';
        }
        formatter += ";-" + formatter;
        return function () {
            return BI.contentFormat(arguments[0], formatter)
        }
    },

    formatTickForRadar: function (type, numberLevel, separators, unit) {
        var formatter = this.formatNumberLevelAndSeparators(type, separators);
        formatter += this.getXYAxisUnit(numberLevel, unit);
        formatter += ";-" + formatter;
        return function () {
            return BI.contentFormat(arguments[0], formatter)
        }
    },

    formatNumberLevelAndSeparators: function (type, separators) {
        var formatter;
        switch (type) {
            case this.constants.NORMAL:
                formatter = '#.##';
                if (separators) {
                    formatter = '#,###.##'
                }
                break;
            case this.constants.ZERO2POINT:
                formatter = '#0';
                if (separators) {
                    formatter = '#,###'
                }
                break;
            case this.constants.ONE2POINT:
                formatter = '#0.0';
                if (separators) {
                    formatter = '#,###.0'
                }
                break;
            case this.constants.TWO2POINT:
                formatter = '#0.00';
                if (separators) {
                    formatter = '#,###.00'
                }
                break;
        }
        return formatter
    },

    setDataLabelContent: function (chartOptions) {
        var setting = chartOptions.dataLabelSetting, identifier = '';
        if(setting.showCategoryName) {
            identifier += '${CATEGORY}'
        }
        if(setting.showSeriesName) {
            identifier += '${SERIES}'
        }
        if(setting.showValue) {
            identifier += '${VALUE}'
        }
        if(setting.showPercentage) {
            identifier += '${PERCENT}'
        }
        return identifier
    },

    setDataLabelPosition: function (chartOptions) {
        var setting = chartOptions.dataLabelSetting || {};
        switch (setting.position || this.constants.OUTER) {
            case this.constants.INNER:
                return 'center';
            case this.constants.CENTER:
                return 'inside';
            case this.constants.OUTER:
                return 'outside'
        }
    },
    
    formatDataLabelForAxis: function (items, config, chartOptions) {
        var self = this;
        if (config.plotOptions.dataLabels.enabled === true) {
            BI.each(items, function (idx, item) {
                var format;
                if(config.xAxis[0] && (config.xAxis[0].type === 'value')) {
                    format = config.xAxis[item.xAxis].formatter;
                }
                format = config.yAxis[item.yAxis].formatter;

                item.dataLabels = {
                    align: self.setDataLabelPosition(chartOptions),
                    autoAdjust: true,
                    style: chartOptions.dataLabelSetting.textStyle,
                    enabled: true,
                    formatter: {
                        identifier: self.setDataLabelContent(chartOptions),
                        valueFormat: format,
                    }
                };
                self.formatDataLabelForEachData(item.data, format, chartOptions);
            });
        }
    },

    formatDataLabelForOthers: function (state, items, format, chartOptions) {
        var self = this;
        if (state === true) {
            BI.each(items, function (idx, item) {
                item.dataLabels = {
                    align: self.setDataLabelPosition(chartOptions),
                    autoAdjust: true,
                    style: chartOptions.dataLabelSetting.textStyle,
                    enabled: true,
                    formatter: {
                        identifier: self.setDataLabelContent(chartOptions),
                        valueFormat: format
                    }
                };
            });
        }
    },

    formatDataLabelForEachData: function (items, format, chartOptions) {
        var self = this;
        BI.each(items, function (idx, item) {
            if (item.dataLabels) {
                var styleSetting = item.dataLabels.styleSetting || {};
                item.dataLabels.formatter = {
                    identifier: chartOptions ? self.setDataLabelContent(chartOptions) : '${VALUE}',
                    valueFormat: format
                };
                item.dataLabels.enabled = true;
                item.dataLabels.autoAdjust = true;
                item.dataLabels.align = chartOptions ? self.setDataLabelPosition(chartOptions) : 'outside';
                item.dataLabels.style = {
                    "fontFamily": "inherit",
                    "color": "#808080",
                    "fontSize": "12px"
                };
                switch (styleSetting.type) {
                    case BICst.DATA_LABEL_STYLE_TYPE.TEXT:
                        item.dataLabels.style = BI.clone(styleSetting.textStyle);
                        item.dataLabels.style.overflow = "visible";
                        item.dataLabels.style.fontSize += "px";
                        break;
                    case BICst.DATA_LABEL_STYLE_TYPE.IMG:
                        item.dataLabels.useHtml = true;
                        item.dataLabels.formatter = "function(){return '<img width=\"20px\" height=\"20px\" src=\"" + BI.Func.getCompleteImageUrl(styleSetting.imgStyle.src) + "\">';}";
                        break;
                }
            }
        })
    },

    catSetting: function (config) {
        return BI.extend({
            maxHeight: '40%',
            maxWidth: '40%',
            enableTick: config.enableTick,
            lineWidth: config.lineWidth,
            lineColor: config.catLineColor,
            gridLineColor: config.vGridLineColor,
            gridLineWidth: config.vShowGridLine === true ? 1 : 0,
            showLabel: config.catShowLabel,
            labelRotation: config.catLabelStyle && config.catLabelStyle.textDirection,
            labelStyle: BI.extend({}, config.catLabelStyle && config.catLabelStyle.textStyle, {
                fontSize: config.catLabelStyle && config.catLabelStyle.textStyle && config.catLabelStyle.textStyle.fontSize + "px"
            }),
        }, {
            title: {
                align: "center",
                text: config.catShowTitle ? config.catTitle : "",
                style: BI.extend({}, config.catTitleStyle, {
                    fontSize: config.catTitleStyle && config.catTitleStyle.fontSize + "px"
                })
            }
        })
    },

    leftAxisSetting: function (config) {
        var unit = this.getXYAxisUnit(config.leftYNumberLevel, config.leftYUnit);
        return BI.extend({
            lineWidth: config.lineWidth,
            lineColor: config.leftYLineColor,
            tickColor: config.leftYLineColor,
            gridLineWidth: config.hShowGridLine === true ? 1 : 0,
            gridLineColor: config.hGridLineColor,
            showLabel: config.leftYShowLabel,
            labelStyle: BI.extend({}, config.leftYLabelStyle && config.leftYLabelStyle.textStyle, {
                fontSize: config.leftYLabelStyle && config.leftYLabelStyle.textStyle && config.leftYLabelStyle.textStyle.fontSize + "px"
            }),
            labelRotation: config.leftYLabelStyle && config.leftYLabelStyle.textDirection,
            enableTick: config.enableTick,
            reversed: config.leftYReverse,
            enableMinorTick: config.enableMinorTick,
            min: config.leftYCustomScale.minScale.scale || null,
            max: config.leftYCustomScale.maxScale.scale || null,
            tickInterval: BI.isNumber(config.leftYCustomScale.interval.scale) && config.leftYCustomScale.interval.scale > 0 ?
                config.leftYCustomScale.interval.scale : null,
            formatter: this.formatTickInXYaxis(config.leftYNumberFormat, config.leftYNumberLevel, config.leftYSeparator)
        }, {
            title: {
                text: config.leftYShowTitle ? config.leftYTitle + unit : '',
                rotation: this.constants.ROTATION,
                style: BI.extend({}, config.leftYTitleStyle, {
                    fontSize: config.leftYTitleStyle && config.leftYTitleStyle.fontSize + "px"
                })
            }
        })
    },

    rightAxisSetting: function (config) {
        var unit = this.getXYAxisUnit(config.rightYNumberLevel, config.rightYUnit);
        return BI.extend({
            lineWidth: config.lineWidth,
            lineColor: config.rightYLineColor,
            tickColor: config.rightYLineColor,
            gridLineWidth: config.hShowGridLine === true ? 1 : 0,
            gridLineColor: config.hGridLineColor,
            showLabel: config.rightYShowLabel,
            labelStyle: BI.extend({}, config.rightYLabelStyle && config.rightYLabelStyle.textStyle, {
                fontSize: config.rightYLabelStyle && config.rightYLabelStyle.textStyle && config.rightYLabelStyle.textStyle.fontSize + "px"
            }),
            labelRotation: config.rightYLabelStyle.textDirection,
            reversed: config.rightYReverse,
            enableTick: config.enableTick,
            enableMinorTick: config.enableMinorTick,
            min: config.rightYCustomScale.minScale.scale || null,
            max: config.rightYCustomScale.maxScale.scale || null,
            tickInterval: BI.isNumber(config.rightYCustomScale.interval.scale) && config.rightYCustomScale.interval.scale > 0 ?
                config.rightYCustomScale.interval.scale : null,
            formatter: this.formatTickInXYaxis(config.rightYNumberFormat, config.rightYNumberLevel, config.rightYSeparator)
        }, {
            title: {
                text: config.rightYShowTitle ? config.rightYTitle + unit : '',
                rotation: this.constants.ROTATION,
                style: BI.extend({}, config.rightYTitleStyle, {
                    fontSize: config.rightYTitleStyle && config.rightYTitleStyle.fontSize + "px"
                })
            }
        })
    },

    right2AxisSetting: function (config) {
        var unit = this.getXYAxisUnit(config.rightY2NumberLevel, config.rightY2Unit);
        return BI.extend({
            lineWidth: config.lineWidth,
            lineColor: config.rightY2LineColor,
            tickColor: config.rightY2LineColor,
            gridLineWidth: config.hShowGridLine === true ? 1 : 0,
            gridLineColor: config.hGridLineColor,
            showLabel: config.rightY2ShowLabel,
            labelStyle: BI.extend({}, config.rightY2LabelStyle.textStyle, {
                fontSize: config.rightY2LabelStyle && config.rightY2LabelStyle.text_style && config.rightY2LabelStyle.textStyle.fontSize + "px"
            }),
            labelRotation: config.rightY2LabelStyle && config.rightY2LabelStyle.textDirection,
            reversed: config.rightY2Reverse,
            enableTick: config.enableTick,
            enableMinorTick: config.enableMinorTick,
            min: config.rightY2CustomScale.minScale.scale || null,
            max: config.rightY2CustomScale.maxScale.scale || null,
            tickInterval: BI.isNumber(config.rightY2CustomScale.interval.scale) && config.rightY2CustomScale.interval.scale > 0 ?
                config.rightY2CustomScale.interval.scale : null,
            formatter: this.formatTickInXYaxis(config.rightY2NumberFormat, config.rightY2NumberLevel, config.rightY2Separator)
        }, {
            title: {
                text: config.rightY2ShowTitle ? config.rightY2Title + unit : '',
                rotation: this.constants.ROTATION,
                style: BI.extend({}, config.rightY2TitleStyle, {
                    fontSize: config.rightY2TitleStyle && config.rightY2TitleStyle.fontSize + "px"
                })
            }
        })
    },

    getChartConfig: function (options) {
        var c = this.constants;
        return {
            chartColor: options.chartColor || [],
            chartStyle: options.chartStyle || c.STYLE_NORMAL,
            lienAreaChartType: options.lienAreaChartType || c.NORMAL,
            pieChartType: options.pieChartType || c.NORMAL,
            radarChartType: options.radarChartType || c.NORMAL,
            dashboardChartType: options.dashboardChartType || c.NORMAL,
            innerRadius: options.innerRadius || 0,
            totalAngle: options.totalAngle || BICst.PIE_ANGLES.TOTAL,
            dashboardPointer: options.dashboardPointer || c.ONE_POINTER,
            dashboardStyles: options.dashboardStyles || [],
            //y左值轴
            leftYNumberFormat: options.leftYNumberFormat || c.NORMAL,
            leftYUnit: options.leftYUnit || '',
            leftYNumberLevel: options.leftYNumberLevel || c.NORMAL,
            leftYShowTitle: options.leftYShowTitle || false,
            leftYTitle: options.leftYTitle || '',
            leftYReverse: options.leftYReverse || c.REVERSE,
            leftYShowLabel: options.leftYShowLabel,
            leftYLabelStyle: options.leftYLabelStyle || c.FONT_STYLE,
            leftYLineColor: options.leftYLineColor || '',
            leftYSeparator: options.leftYSeparator || c.SEPARATOR,
            leftYTitleStyle: options.leftYTitleStyle || c.FONT_STYLE,
            leftYCustomScale: options.leftYCustomScale || c.CUSTOM_SCALE,
            //y右值轴
            rightYNumberFormat: options.rightYNumberFormat || c.NORMAL,
            rightYNumberLevel: options.rightYNumberLevel || c.NORMAL,
            rightYUnit: options.rightYUnit || '',
            rightYReverse: options.rightYReverse || c.REVERSE,
            rightYShowTitle: options.rightYShowTitle || false,
            rightYTitleStyle: options.rightYTitleStyle || c.FONT_STYLE,
            rightYTitle: options.rightYTitle,
            rightYSeparator: options.rightYSeparator,
            rightYCustomScale: options.rightYCustomScale || c.CUSTOM_SCALE,
            rightYShowLabel: options.rightYShowLabel,
            rightYLabelStyle: options.rightYLabelStyle || c.FONT_STYLE,
            rightYLineColor: options.rightYLineColor,
            //y2右值轴
            rightY2NumberFormat: options.rightY2NumberFormat,
            rightY2NumberLevel: options.rightY2NumberLevel,
            rightY2Unit: options.rightY2Unit,
            rightY2ShowTitle: options.rightY2ShowTitle || false,
            rightY2Title: options.rightY2Title,
            rightY2Reverse: options.rightY2Reverse,
            rightY2Separator: options.rightY2Separator,
            rightY2ShowLabel: options.rightY2ShowLabel,
            rightY2LabelStyle: options.rightY2LabelStyle || c.FONT_STYLE,
            rightY2LineColor: options.rightY2LineColor,
            rightY2TitleStyle: options.rightY2TitleStyle,
            rightY2CustomScale: options.rightY2CustomScale || c.CUSTOM_SCALE,
            rightY2ShowCustomScale: options.rightY2ShowCustomScale,
            //分类轴
            catShowTitle: options.catShowTitle || false,
            catTitle: options.catTitle,
            catShowLabel: options.catShowLabel,
            catLabelStyle: options.catLabelStyle || c.FONT_STYLE,
            catLineColor: options.catLineColor,
            catTitleStyle: options.catTitleStyle || c.FONT_STYLE,
            //其他元素
            lineWidth: options.lineWidth || 1,
            legend: options.legend,
            legendStyle: options.legendStyle || {},
            showDataLabel: options.showDataLabel,
            showDataTable: options.showDataTable || false,
            showZoom: options.showZoom,
            styleRadio: options.styleRadio,
            themeColor: options.themeColor,
            mapStyles: options.mapStyles || [],
            displayRules: options.displayRules,
            bubbleStyle: options.bubbleStyle,
            maxScale: options.maxScale,
            minScale: options.minScale,
            showPercentage: options.showPercentage,
            bubbleSizeFrom: options.bubbleSizeFrom,
            bubbleSizeTo: options.bubbleSizeTo,
            gradientStyle: options.gradientStyle,
            fixedStyle: options.fixedStyle,
            isShowBackgroundLayer: options.isShowBackgroundLayer,
            hShowGridLine: options.hShowGridLine,
            hGridLineColor: options.hGridLineColor,
            vShowGridLine: options.vShowGridLine,
            vGridLineColor: options.vGridLineColor,
            tooltipStyle: options.tooltipStyle,
            chartFont: options.chartFont && BI.extend({}, options.chartFont, {
                fontSize:  options.chartFont.fontSize ? options.chartFont.fontSize + "px" : ""
            }),
            nullContinuity: options.nullContinuity,
            backgroundLayerInfo: MapConst.WMS_INFO[options.backgroundLayerInfo],
            transferFilter: options.transferFilter,
            bigDataMode: options.bigDataMode || false,
            geo: options.geo,
            initDrillPath: options.initDrillPath || [],
            cordon: options.cordon || [],
            tooltip: options.tooltip || [],
            lnglat: options.lnglat,
            dataLabelSetting: options.dataLabelSetting ? BI.extend(options.dataLabelSetting, {
                    textStyle: BI.extend(options.dataLabelSetting.textStyle, {
                            fontSize: options.dataLabelSetting.textStyle.fontSize + 'px'
                    })
            }) : c.DATA_SETTING_STYLE,
        }
    },

    setFontStyle: function (fontStyle, config) {
        if (config.dataSheet) {
            config.dataSheet.style = fontStyle;
        }
    },

    _formatItems: function (items) {
        return items;
    },

    populate: function (items, options) {
    },

    resize: function () {
    },

    magnify: function () {
    }
});

BI.AbstractChart.EVENT_CHANGE = "EVENT_CHANGE";
BI.AbstractChart.EVENT_ITEM_CLICK = "EVENT_ITEM_CLICK";

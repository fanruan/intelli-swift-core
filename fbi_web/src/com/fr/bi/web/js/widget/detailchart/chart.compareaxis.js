/**
 * 图表控件
 * @class BI.CompareAxisChart
 * @extends BI.Widget
 */
BI.CompareAxisChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.CompareAxisChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-compare-axis-chart"
        })
    },

    _init: function () {
        BI.CompareAxisChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.xAxis = [{
            type: "category",
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE,
            position: "bottom",
            gridLineWidth: 0
        }, {
            title: {
                style: this.constants.FONT_STYLE
            },
            labelStyle: this.constants.FONT_STYLE,
            position: "top",
            gridLineWidth: 0,
            type: "category",
            showLabel: false
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            popupItemsGetter: o.popupItemsGetter,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CompareAxisChart.EVENT_CHANGE, obj);
        });
        this.combineChart.on(BI.CombineChart.EVENT_ITEM_CLICK, function (obj) {
            self.fireEvent(BI.AbstractChart.EVENT_ITEM_CLICK, obj)
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        config.colors = this.config.chart_color;
        config.plotOptions.style = formatChartStyle();
        formatChartLineStyle();
        formatCordon();
        this.formatChartLegend(config, this.config.chart_legend);
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.dataSheet.enabled = this.config.show_data_table;
        config.xAxis[0].showLabel = !config.dataSheet.enabled;
        config.zoom.zoomTool.enabled = this.config.show_zoom;
        if(this.config.show_zoom === true){
            delete config.dataSheet;
            delete config.zoom.zoomType;
        }

        config.yAxis = this.yAxis;
        BI.each(config.yAxis, function(idx, axis){
            var unit = '';
            switch (axis.axisIndex){
                case self.constants.LEFT_AXIS:
                    unit = getXYAxisUnit(self.config.left_y_axis_number_level, self.constants.LEFT_AXIS);
                    axis.title.rotation = self.constants.ROTATION;
                    axis.title.text = self.config.show_left_y_axis_title === true ? self.config.left_y_axis_title + unit : unit;
                    BI.extend(axis, {
                        reversed: false,
                        lineWidth: self.config.line_width,
                        showLabel: self.config.show_label,
                        enableTick: self.config.enable_tick,
                        enableMinorTick: self.config.enable_minor_tick,
                        gridLineWidth: self.config.show_grid_line === true ? 1 : 0,
                        formatter: self.formatTickInXYaxis(self.config.left_y_axis_style, self.config.left_y_axis_number_level, self.config.num_separators)
                    });
                    formatNumberLevelInYaxis(self.config.left_y_axis_number_level, idx, axis.formatter);
                    break;
                case self.constants.RIGHT_AXIS:
                    unit = getXYAxisUnit(self.config.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                    axis.title.rotation = self.constants.ROTATION;
                    axis.title.text = self.config.show_right_y_axis_title === true ? self.config.right_y_axis_title + unit : unit;
                    BI.extend(axis, {
                        reversed: true,
                        lineWidth: self.config.line_width,
                        showLabel: self.config.show_label,
                        enableTick: self.config.enable_tick,
                        enableMinorTick: self.config.enable_minor_tick,
                        gridLineWidth: self.config.show_grid_line === true ? 1 : 0,
                        formatter: self.formatTickInXYaxis(self.config.right_y_axis_style, self.config.right_y_axis_number_level, self.config.right_num_separators)
                    });
                    formatNumberLevelInYaxis(self.config.right_y_axis_number_level, idx, axis.formatter);
                    break;
            }
            var res = _calculateValueNiceDomain(0, self.maxes[idx]);
            axis.max = res[1].mul(2);
            axis.min = res[0].mul(2);
            axis.tickInterval = BI.parseFloat((BI.parseFloat(axis.max).sub(BI.parseFloat(axis.min)))).div(5);
        });

        config.xAxis[0].title.align = "center";
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? this.config.x_axis_title : "";
        BI.extend(config.xAxis[0], {
            lineWidth: this.config.line_width,
            enableTick: this.config.enable_tick,
            labelRotation: this.config.text_direction,
            enableMinorTick: this.config.enable_minor_tick,
            gridLineWidth: this.config.show_grid_line === true ? 1 : 0,
            maxHeight: '40%'
        });

        BI.extend(config.xAxis[1], {
            lineWidth: this.config.line_width,
            enableTick: this.config.enable_tick,
            enableMinorTick: this.config.enable_minor_tick
        });

        //为了给数据标签加个%,还要遍历所有的系列，唉
        this.formatDataLabel(config.plotOptions.dataLabels.enabled, items, config, this.config.chart_font);

        //全局样式的图表文字
        this.setFontStyle(this.config.chart_font, config);

        return [items, config];

        function formatChartStyle(){
            switch (self.config.chart_style) {
                case BICst.CHART_STYLE.STYLE_GRADUAL:
                    return "gradual";
                case BICst.CHART_STYLE.STYLE_NORMAL:
                default:
                    return "normal";
            }
        }

        function formatCordon(){
            BI.each(self.config.cordon, function(idx, cor){
                if(idx === 0 && self.xAxis.length > 0){
                    var magnify = self.calcMagnify(self.config.left_y_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function(i, t){
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style" : self.config.chart_font,
                                "text": t.text,
                                "align": "top"
                            }
                        });
                    });
                }
                if(idx > 0 && self.yAxis.length >= idx){
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
                    self.yAxis[idx - 1].plotLines = BI.map(cor, function(i, t){
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style" : self.config.chart_font,
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }

        function formatChartLineStyle(){
            switch (self.config.chart_line_type) {
                case BICst.CHART_SHAPE.RIGHT_ANGLE:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = true;
                    break;
                case BICst.CHART_SHAPE.CURVE:
                    config.plotOptions.curve = true;
                    config.plotOptions.step = false;
                    break;
                case BICst.CHART_SHAPE.NORMAL:
                default:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = false;
                    break;
            }
        }

        function formatNumberLevelInYaxis(type, position, formatter){
            var magnify = self.calcMagnify(type);
            BI.each(items, function (idx, item) {
                var max = null;
                BI.each(item.data, function (id, da) {
                    if (position === item.yAxis) {
                        da.y = self.formatXYDataWithMagnify(da.y, magnify);
                        if((BI.isNull(max) || BI.parseFloat(da.y) > BI.parseFloat(max))){
                            max = da.y;
                        }
                    }
                });
                if(position === item.yAxis){
                    item.tooltip = BI.deepClone(config.plotOptions.tooltip);
                    item.tooltip.formatter.valueFormat = formatter;
                }
                if(BI.isNotNull(max)){
                    self.maxes.push(max);
                }
            });
        }

        function getXYAxisUnit(numberLevelType, position){
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
            if(position === self.constants.X_AXIS){
                self.config.x_axis_unit !== "" && (unit = unit + self.config.x_axis_unit)
            }
            if(position === self.constants.LEFT_AXIS){
                self.config.left_y_axis_unit !== "" && (unit = unit + self.config.left_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS){
                self.config.right_y_axis_unit !== "" && (unit = unit + self.config.right_y_axis_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
        }

        function _calculateValueNiceDomain(minValue, maxValue){

            minValue = Math.min(0, minValue);

            var tickInterval = _linearTickInterval(minValue, maxValue);

            return _linearNiceDomain(minValue, maxValue, tickInterval);
        }

        function _linearTickInterval(minValue, maxValue, m){

            m = m || 5;
            var span = maxValue - minValue;
            var step = Math.pow(10, Math.floor(Math.log(span / m) / Math.LN10));
            var err = m / span * step;

            if (err <= .15) step *= 10; else if (err <= .35) step *= 5; else if (err <= .75) step *= 2;

            return step;
        }

        function _linearNiceDomain(minValue, maxValue, tickInterval){

            minValue = VanUtils.accMul(Math.floor(minValue / tickInterval), tickInterval);

            maxValue = VanUtils.accMul(Math.ceil(maxValue / tickInterval), tickInterval);

            return [minValue, maxValue];
        }
    },

    _formatItems: function(items){
        var self = this;
        this.maxes = [];
        BI.each(items, function(idx, item){
            BI.each(item, function(id, it){
                if(idx > 0){
                    BI.extend(it, {reversed: true, xAxis: 1});
                }else{
                    BI.extend(it, {reversed: false, xAxis: 0});
                }
            });
        });
        return items;
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            right_y_axis_title: options.right_y_axis_title || "",
            chart_color: options.chart_color || [],
            chart_style: options.chart_style || c.NORMAL,
            chart_line_type: options.chart_line_type || c.NORMAL,
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            right_y_axis_style: options.right_y_axis_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            show_right_y_axis_title: options.show_right_y_axis_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
            right_y_axis_reversed: options.right_y_axis_reversed || false,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            right_y_axis_number_level:  options.right_y_axis_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            right_y_axis_unit: options.right_y_axis_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_data_table: options.show_data_table || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            show_zoom: options.show_zoom || false,
            text_direction: options.text_direction || 0,
            cordon: options.cordon || [],
            line_width: BI.isNull(options.line_width) ? 1 : options.line_width,
            show_label: BI.isNull(options.show_label) ? true : options.show_label,
            enable_tick: BI.isNull(options.enable_tick) ? true : options.enable_tick,
            enable_minor_tick: BI.isNull(options.enable_minor_tick) ? true : options.enable_minor_tick,
            num_separators: options.num_separators || false,
            right_num_separators: options.right_num_separators || false,
            chart_font: options.chart_font || c.FONT_STYLE
        };
        this.options.items = items;

        this.yAxis = [];

        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.AXIS);
            });
            types.push(type);
        });

        BI.each(types, function(idx, type){
            if(BI.isEmptyArray(type)){
                return;
            }
            var newYAxis = {
                type: "value",
                title: {
                    style: self.constants.FONT_STYLE
                },
                labelStyle: self.constants.FONT_STYLE,
                position: idx > 0 ? "right" : "left",
                lineWidth: 1,
                axisIndex: idx,
                gridLineWidth: 0,
                reversed: idx > 0
            };
            self.yAxis.push(newYAxis);
        });

        this.combineChart.populate(this._formatItems(items), types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function(){
        this.combineChart.magnify();
    }
});
BI.CompareAxisChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.compare_axis_chart', BI.CompareAxisChart);
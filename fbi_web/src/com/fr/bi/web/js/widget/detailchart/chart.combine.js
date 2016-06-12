/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    constants: {
        LEFT_AXIS: 0,
        RIGHT_AXIS: 1,
        RIGHT_AXIS_SECOND: 2,
        X_AXIS: 3,
        DASHBOARD_AXIS:4,
        ROTATION: -90,

        NORMAL: 1,
        ZERO2POINT: 2,
        ONE2POINT: 3,
        TWO2POINT: 4
    },

    _defaultConfig: function () {
        return BI.extend(BI.CombineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart",
            items:[["",[{},{},{}]],[[{},{},{}]]],
            types: [[],[]]
        })
    },

    _init: function () {
        BI.CombineChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //图可配置属性
        this.colors = [];
        this.CombineChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.CombineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, obj);
        });
    },

    _axisConfig: function(){
        return {
            "enableMinorTick": false,
            "gridLineColor": "rgb(196,196,196)",
            "minorTickColor": "rgb(176,176,176)",
            "tickColor": "rgb(176,176,176)",
            "showArrow": false,
            "lineColor": "rgb(176,176,176)",
            "plotLines": [],
            "type": "value",
            "lineWidth": 0,
            "showLabel": true,
            title: {},
            //"formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
            "enableTick": true,
            "gridLineWidth": 0,
            "labelStyle": {"fontFamily": "Verdana", "color": "rgba(102,102,102,1.0)", "fontSize": "11pt", "fontWeight": ""},
            "plotBands": [],
            "labelRotation": 0,
            "reversed": false
        };
    },

    _formatItems: function(items){
        var result = [], self = this, o = this.options;
        var yAxisIndex = 0;
        BI.each(items, function(i, belongAxisItems){
            var combineItems = BI.ChartCombineFormatItemFactory.combineItems(o.types[i], belongAxisItems);
            BI.isEmptyArray(combineItems) && --yAxisIndex;
            BI.each(combineItems, function(j, axisItems){
                if(BI.isArray(axisItems)){
                    result = BI.concat(result, axisItems);
                }else{
                    result.push(BI.extend(axisItems, {"yAxis": yAxisIndex}));
                }
            });
            yAxisIndex++;
        });
        var typess=[];
        BI.each(o.types, function(idx, types){
            typess = BI.concat(typess, types);
        });
        var config = BI.ChartCombineFormatItemFactory.combineConfig(typess[0]);
        config.plotOptions.click = function(){
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, BI.extend(this.pointOption, {seriesName: this.seriesName}));
        };
        switch (typess[0]){
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                config.xAxis = [];
                var newxAxis  = this._axisConfig();
                newxAxis.position = "bottom";
                newxAxis.gridLineWidth = 0;
                newxAxis.type = "category";
                config.xAxis.push(newxAxis);
                config.yAxis = [];
                BI.each(o.types, function(idx, type){
                    if(BI.isEmptyArray(type)){
                        return;
                    }
                    var newYAxis = self._axisConfig();
                    newYAxis.position = idx > 0 ? "right" : "left";
                    newYAxis.gridLineWidth = idx > 0 ? 0 : 1;
                    if(BI.isNotEmptyArray(items)){
                        newYAxis.reversed = items[idx][0].reversed || false;
                        if(items[idx][0].name === ""){
                            config.legend.enabled = false;
                        }
                    }
                    config.yAxis.push(newYAxis);
                });
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                config.xAxis = [];
                config.yAxis = [];
                var newxAxis = this._axisConfig();
                var newyAxis = this._axisConfig();
                newxAxis.position = "bottom";
                newxAxis.gridLineWidth = 0;
                config.xAxis.push(newxAxis);
                newxAxis.type = "value";
                newxAxis.formatter = "function(){if(this>0) return this; else return this*(-1); }";
                newyAxis.position = "left";
                newyAxis.type = "category";
                delete newyAxis.formatter;
                config.yAxis.push(newyAxis);
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DONUT:
                config.plotOptions.dataLabels.align = "outside";
                config.plotOptions.dataLabels.connectorWidth = "outside";
                config.plotOptions.dataLabels.formatter.identifier = "${VALUE}${PERCENT}";
                break;
        }
        addOptionsToConfig();
        return [result, config];

        function addOptionsToConfig(){
            if(BI.isNotEmptyArray(self.chart_color)){
                config.colors = self.chart_color;
            }
            formatChartStyle();
            formatChartLineStyle();
            formatChartPieStyle();
            formatChartRadarStyle();
            formatChartDashboardStyle();
            formatElementAttrs();
            if(BI.has(config, "yAxis") && config.yAxis.length > 0){
                config.yAxis[0].reversed = self.left_y_axis_reversed ? !config.yAxis[0].reversed : config.yAxis[0].reversed;
                if(config.chartType !== "bar"){
                    config.yAxis[0].formatter = formatTickInXYaxis(self.left_y_axis_style, self.constants.LEFT_AXIS);
                }
                formatNumberLevelInYaxis(self.left_y_axis_number_level, self.constants.LEFT_AXIS);
                config.yAxis[0].title.text = getXYAxisUnit(self.left_y_axis_number_level, self.constants.LEFT_AXIS);
                config.yAxis[0].title.text = self.show_left_y_axis_title === true ? self.left_y_axis_title + config.yAxis[0].title.text : config.yAxis[0].title.text;
                config.yAxis[0].gridLineWidth = self.show_grid_line === true ? 1 : 0;
                config.yAxis[0].title.rotation = self.constants.ROTATION;

            }
            if(BI.has(config, "yAxis") && config.yAxis.length > 1){
                config.yAxis[1].formatter = formatTickInXYaxis(self.right_y_axis_style, self.constants.RIGHT_AXIS);
                config.yAxis[1].reversed = self.right_y_axis_reversed ? !config.yAxis[1].reversed : config.yAxis[1].reversed;
                formatNumberLevelInYaxis(self.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                config.yAxis[1].title.text = getXYAxisUnit(self.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                config.yAxis[1].title.text = self.show_right_y_axis_title === true ? self.right_y_axis_title + config.yAxis[1].title.text : config.yAxis[1].title.text;
                config.yAxis[1].gridLineWidth = self.show_grid_line === true ? 1 : 0;
                config.yAxis[1].title.rotation = self.constants.ROTATION;
            }
            if(BI.has(config, "yAxis") && config.yAxis.length > 2){
                config.yAxis[2].formatter = formatTickInXYaxis(self.right_y_axis_second_style, self.constants.RIGHT_AXIS_SECOND);
                config.yAxis[2].reversed = self.right_y_axis_second_reversed ? !config.yAxis[2].reversed : config.yAxis[2].reversed;
                formatNumberLevelInYaxis(self.right_y_axis_second_number_level, self.constants.RIGHT_AXIS_SECOND);
                config.yAxis[2].title.text = getXYAxisUnit(self.right_y_axis_second_number_level, self.constants.RIGHT_AXIS);
                config.yAxis[2].title.text = self.show_right_y_axis_second_title === true ? self.right_y_axis_second_title + config.yAxis[2].title.text : config.yAxis[2].title.text;
                config.yAxis[2].gridLineWidth = self.show_grid_line === true ? 1 : 0;
                config.yAxis[2].title.rotation = self.constants.ROTATION;
            }
            if(BI.has(config, "xAxis") && config.xAxis.length > 0){
                if(config.chartType === "bar"){
                    config.yAxis[0].labelRotation = self.text_direction;
                    config.xAxis[0].formatter = formatTickInXYaxis(self.x_axis_style, self.constants.X_AXIS);
                    formatNumberLevelInXaxis(self.x_axis_number_level);
                    config.xAxis[0].title.text = self.x_axis_title + getXYAxisUnit(self.x_axis_number_level, self.constants.X_AXIS);
                }else{
                    config.xAxis[0].title.text = self.x_axis_title;
                    config.xAxis[0].labelRotation = self.text_direction;
                }
                config.xAxis[0].title.text = self.show_x_axis_title === true ? config.xAxis[0].title.text : "";
                config.xAxis[0].title.align = "center";
                config.xAxis[0].gridLineWidth = self.show_grid_line === true ? 1 : 0;
            }
        }

        function formatTickInXYaxis(type, position){
            var formatter = '#.##';
            switch (type) {
                case self.constants.NORMAL:
                    formatter = '#.##';
                    break;
                case self.constants.ZERO2POINT:
                    formatter = '#0';
                    break;
                case self.constants.ONE2POINT:
                    formatter = '#0.0';
                    break;
                case self.constants.TWO2POINT:
                    formatter = '#0.00';
                    break;
            }
            if(position === self.constants.X_AXIS){
                if(self.x_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            if(position === self.constants.LEFT_AXIS){
                if(self.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            if(position === self.constants.RIGHT_AXIS){
                if(self.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            if(position === self.constants.RIGHT_AXIS_SECOND){
                if(self.right_y_axis_second_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            return "function(){if(this>0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
        }

        function formatNumberLevelInXaxis(type){
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
            if(magnify > 1){
                BI.each(result, function(idx, item){
                    BI.each(item.data, function(id, da){
                        da.x = da.x.div(magnify);
                    })
                })
            }
        }

        function formatNumberLevelInYaxis(type, position){
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
            if(magnify > 1){
                BI.each(result, function(idx, item){
                    BI.each(item.data, function(id, da){
                        if(position === item.yAxis){
                            if(item.type === "bar"){
                                da.x = da.x || 0;
                                da.x = da.x.div(magnify);
                            }else{
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                            }
                        }
                    })
                })
            }
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
                self.x_axis_unit !== "" && (unit = unit + self.x_axis_unit)
            }
            if(position === self.constants.LEFT_AXIS){
                self.left_y_axis_unit !== "" && (unit = unit + self.left_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS){
                self.right_y_axis_unit !== "" && (unit = unit + self.right_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS_SECOND){
                self.right_y_axis_second_unit !== "" && (unit = unit + self.right_y_axis_second_unit)
            }
            if(position === self.constants.DASHBOARD_AXIS){
                self.dashboard_unit !== "" && (unit = unit + self.dashboard_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
        }

        function formatChartStyle(){
            switch (self.chart_style) {
                case BICst.CHART_STYLE.STYLE_GRADUAL:
                    config.style = "gradual";
                    break;
                case BICst.CHART_STYLE.STYLE_NORMAL:
                default:
                    config.style = "normal";
                    break;
            }
        }

        function formatChartLineStyle(){
            switch (self.chart_line_type) {
                case BICst.CHART_STYLE.RIGHT_ANGLE:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = true;
                    break;
                case BICst.CHART_STYLE.CURVE:
                    config.plotOptions.curve = true;
                    config.plotOptions.step = false;
                    break;
                case BICst.CHART_STYLE.NORMAL:
                default:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = false;
                    break;
            }
        }

        function formatChartDashboardStyle(){
            switch (self.chart_dashboard_type) {
                case BICst.CHART_STYLE.HALF_DASHBOARD:
                    config.plotOptions.style = "pointer_semi";
                    break;
                case BICst.CHART_STYLE.PERCENT_DASHBOARD:
                    config.plotOptions.style = "ring";
                    break;
                case BICst.CHART_STYLE.PERCENT_SCALE_SLOT:
                    config.plotOptions.style = "slot";
                    break;
                case BICst.CHART_STYLE.HORIZONTAL_TUBE:
                    config.plotOptions.style = "thermometer";
                    config.plotOptions.thermometerLayout = "horizontal";
                    config.plotOptions.valueLabel.formatter.identifier = "${CATEGORY}${VALUE}";
                    config.plotOptions.valueLabel.align = "bottom";
                    config.plotOptions.percentageLabel.align = "bottom";
                    break;
                case BICst.CHART_STYLE.VERTICAL_TUBE:
                    config.plotOptions.style = "thermometer";
                    config.plotOptions.thermometerLayout = "vertical";
                    config.plotOptions.valueLabel.formatter.identifier = "${CATEGORY}${VALUE}";
                    config.plotOptions.valueLabel.align = "left";
                    config.plotOptions.percentageLabel.align = "left";
                    break;
                case BICst.CHART_STYLE.NORMAL:
                default:
                    config.plotOptions.style = "pointer";
                    break;
            }
            formatNumberLevelInYaxis(self.dashboard_number_level, self.constants.LEFT_AXIS);
            config.plotOptions.valueLabel.formatter = function(){
                return getXYAxisUnit(self.dashboard_number_level, self.constants.DASHBOARD_AXIS);
            };
        }

        function formatChartPieStyle(){
            switch (self.chart_pie_type){
                case BICst.CHART_STYLE.EQUAL_ARC_ROSE:
                    config.plotOptions.roseType = "sameArc";
                    break;
                case BICst.CHART_STYLE.NOT_EQUAL_ARC_ROSE:
                    config.plotOptions.roseType = "differentArc";
                    break;
                case BICst.CHART_STYLE.NORMAL:
                default:
                    delete config.plotOptions.roseType;
                    break;
            }
            typess[0] === BICst.WIDGET.PIE && (config.plotOptions.innerRadius = self.chart_inner_radius);
            config.plotOptions.endAngle = self.chart_total_angle;
        }

        function formatChartRadarStyle(){
            switch (self.chart_radar_type) {
                case BICst.CHART_STYLE.POLYGON:
                    config.plotOptions.shape = "polygon";
                    break;
                case BICst.CHART_STYLE.CIRCLE:
                    config.plotOptions.shape = "circle";
                    break;
            }
        }

        function formatElementAttrs(){
            switch (self.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    config.legend.enabled = true;
                    config.legend.position = "bottom";
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    config.legend.enabled = true;
                    config.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    config.legend.enabled = false;
                    break;
            }
            config.plotOptions.dataLabels.enabled = self.show_data_label;
            if(BI.has(config, "dataSheet")){
                config.dataSheet.enabled = self.show_data_table;
                if(config.dataSheet.enabled === true){
                    config.xAxis[0].showLabel = false;
                }
            }
            config.zoom.zoomTool.visible = self.show_zoom;
            self.show_zoom === true && delete config.dataSheet;
            config.plotOptions.connectNulls = self.null_continue;
        }
    },

    setTypes: function(types){
        this.options.types = types||[[]];
    },

    setOptions: function(options){
        this.left_y_axis_title = options.left_y_axis_title || "";
        this.right_y_axis_title = options.right_y_axis_title || "";
        this.right_y_axis_second_title = options.right_y_axis_second_title || "";
        this.chart_color = options.chart_color || [];
        this.chart_style = options.chart_style || BICst.CHART_STYLE.STYLE_NORMAL;
        this.chart_line_type = options.chart_line_type || BICst.CHART_STYLE.NORMAL;
        this.chart_pie_type = options.chart_pie_type || BICst.CHART_STYLE.NORMAL;
        this.chart_dashboard_type = options.chart_dashboard_type || BICst.CHART_STYLE.NORMAL;
        this.chart_inner_radius = options.chart_inner_radius || 0;
        this.chart_total_angle = options.chart_total_angle || BICst.PIE_ANGLES.TOTAL;
        this.chart_radar_type = options.chart_radar_type || BICst.CHART_STYLE.NORMAL;
        this.left_y_axis_style = options.left_y_axis_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.x_axis_style = options.x_axis_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.right_y_axis_style = options.right_y_axis_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.right_y_axis_second_style = options.right_y_axis_second_style || BICst.TARGET_STYLE.FORMAT.NORMAL;
        this.show_x_axis_title = options.show_x_axis_title || false;
        this.show_left_y_axis_title = options.show_left_y_axis_title || false;
        this.show_right_y_axis_title = options.show_right_y_axis_title || false;
        this.show_right_y_axis_second_title = options.show_right_y_axis_second_title || false;
        this.left_y_axis_reversed = options.left_y_axis_reversed || false;
        this.right_y_axis_reversed = options.right_y_axis_reversed || false;
        this.right_y_axis_second_reversed = options.right_y_axis_second_reversed || false;
        this.x_axis_number_level = options.x_axis_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.left_y_axis_number_level = options.left_y_axis_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.dashboard_number_level = options.dashboard_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.right_y_axis_number_level = options.right_y_axis_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.right_y_axis_second_number_level = options.right_y_axis_second_number_level || BICst.TARGET_STYLE.NUM_LEVEL.NORMAL;
        this.x_axis_unit = options.x_axis_unit || "";
        this.dashboard_unit = options.dashboard_unit || "";
        this.left_y_axis_unit = options.left_y_axis_unit || "";
        this.right_y_axis_unit = options.right_y_axis_unit || "";
        this.right_y_axis_second_unit = options.right_y_axis_second_unit || "";
        this.x_axis_title = options.x_axis_title || "";
        this.chart_legend = options.chart_legend || BICst.CHART_LEGENDS.BOTTOM;
        this.show_data_label = options.show_data_label || false;
        this.show_data_table = options.show_data_table || false;
        this.show_grid_line = options.show_grid_line;
        this.show_zoom = options.show_zoom || false;
        this.null_continue = options.null_continue;
        this.text_direction = options.text_direction;
    },

    populate: function (items, options, types) {
        if(BI.isNotNull(types)){
            this.setTypes(types);
        }
        var opts = this._formatItems(items);
        BI.extend(opts[1], options);
        this.CombineChart.populate(opts[0], opts[1]);
    },

    resize: function(){
        this.CombineChart.resize();
    }
});
BI.CombineChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.combine_chart', BI.CombineChart);
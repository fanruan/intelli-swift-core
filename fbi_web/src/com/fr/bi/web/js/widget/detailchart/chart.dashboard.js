/**
 * 图表控件
 * @class BI.DashboardChart
 * @extends BI.Widget
 */
BI.DashboardChart = BI.inherit(BI.Widget, {

    constants: {
        LEFT_AXIS: 0,
        RIGHT_AXIS: 1,
        RIGHT_AXIS_SECOND: 2,
        X_AXIS: 3,
        DASHBOARD_AXIS:4,
        ROTATION: -90,
        NORMAL: 1,
        LEGEND_BOTTOM: 4,
        ZERO2POINT: 2,
        ONE2POINT: 3,
        TWO2POINT: 4,
        STYLE_NORMAL: 21,
        MINLIMIT: 1e-3,
        ONE_POINTER: 1,
        MULTI_POINTER: 2,
        HALF_DASHBOARD: 9,
        PERCENT_DASHBOARD: 10,
        PERCENT_SCALE_SLOT: 11,
        VERTICAL_TUBE: 12,
        HORIZONTAL_TUBE: 13
    },

    _defaultConfig: function () {
        return BI.extend(BI.DashboardChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-chart"
        })
    },

    _init: function () {
        BI.DashboardChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.gaugeAxis = [{
            "minorTickColor": "rgb(226,226,226)",
            "tickColor": "rgb(186,186,186)",
            labelStyle: {
                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
            },
            "showLabel": true
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.DashboardChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        formatChartDashboardStyle();
        config.chartType = "dashboard";
        delete config.xAxis;
        delete config.yAxis;
        return [items, config];

        function formatChartDashboardStyle(){
            config.gaugeAxis = self.gaugeAxis;
            switch (self.config.chart_dashboard_type) {
                case BICst.CHART_SHAPE.HALF_DASHBOARD:
                    config.plotOptions.style = "pointer_semi";
                    break;
                case BICst.CHART_SHAPE.PERCENT_DASHBOARD:
                    config.plotOptions.style = "ring";
                    break;
                case BICst.CHART_SHAPE.PERCENT_SCALE_SLOT:
                    config.plotOptions.style = "slot";
                    break;
                case BICst.CHART_SHAPE.HORIZONTAL_TUBE:
                    config.plotOptions.style = "thermometer";
                    config.plotOptions.thermometerLayout = "horizontal";
                    config.plotOptions.valueLabel.formatter.identifier = "${CATEGORY}${VALUE}";
                    config.plotOptions.valueLabel.align = "bottom";
                    config.plotOptions.percentageLabel.align = "bottom";
                    config.plotOptions.layout = "vertical";
                    break;
                case BICst.CHART_SHAPE.VERTICAL_TUBE:
                    config.plotOptions.style = "thermometer";
                    config.plotOptions.thermometerLayout = "vertical";
                    config.plotOptions.valueLabel.formatter.identifier = "${CATEGORY}${VALUE}";
                    config.plotOptions.valueLabel.align = "left";
                    config.plotOptions.percentageLabel.align = "left";
                    config.plotOptions.layout = "vertical";
                    break;
                case BICst.CHART_SHAPE.NORMAL:
                default:
                    config.plotOptions.style = "pointer";
                    break;
            }
            formatNumberLevelInYaxis(self.config.dashboard_number_level, self.constants.LEFT_AXIS);
            config.gaugeAxis[0].formatter = function(){
                return this + getXYAxisUnit(self.config.dashboard_number_level, self.constants.DASHBOARD_AXIS);
            };
        }

        function formatNumberLevelInYaxis(type, position){
            var magnify = calcMagnify(type);
            if(magnify > 1){
                BI.each(items, function(idx, item){
                    BI.each(item.data, function(id, da){
                        if (position === item.yAxis) {
                            da.y = da.y || 0;
                            da.y = da.y.div(magnify);
                            if(self.constants.MINLIMIT.sub(da.y) > 0){
                                da.y = 0;
                            }
                        }
                    })
                })
            }
            if(type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                config.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
            }
        }

        function calcMagnify(type){
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
            if(position === self.constants.DASHBOARD_AXIS){
                self.dashboard_unit !== "" && (unit = unit + self.config.dashboard_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
        }
    },

    _formatItems: function(items){
        if(items.length === 0){
            return [];
        }
        var c = this.constants;
        if(this.config.chart_dashboard_type === c.NORMAL || this.config.chart_dashboard_type === c.HALF_DASHBOARD){
            var result = [];
            if(this.config.number_of_pointer === c.ONE_POINTER && items[0].length === 1){//单个系列
                BI.each(items[0][0].data, function(idx, da){
                    result.push({
                        data: [{
                            x: items[0][0].name,
                            y: da.y
                        }],
                        name: da.x
                    })
                });
                return [result];
            }
            if(this.config.number_of_pointer === c.MULTI_POINTER && items[0].length > 1){//多个系列
                BI.each(items, function(idx, item){
                    BI.each(item, function(id, it){
                        var data = it.data[0];
                        data.x = it.name;
                        result.push(data);
                    })
                });
                return [[{
                    data: result,
                    name: ""
                }]];
            }
        }
        return items;
    },

    populate: function (items, options) {
        var self = this, c = this.constants, o = this.options;
        this.config = {
            dashboard_number_level: options.dashboard_number_level || c.NORMAL,
            dashboard_unit: options.dashboard_unit || "",
            chart_dashboard_type: options.chart_dashboard_type || c.NORMAL,
            number_of_pointer: options.number_of_pointer || c.ONE_POINTER
        };
        o.items = this._formatItems(items);
        var types = [];
        BI.each(o.items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.DASHBOARD);
            });
            types.push(type);
        });

        this.combineChart.populate(o.items, types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function(){
        this.combineChart.magnify();
    }
});
BI.DashboardChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.dashboard_chart', BI.DashboardChart);
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
        STYLE_NORMAL: 21
    },

    _defaultConfig: function () {
        return BI.extend(BI.DashboardChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-chart"
        })
    },

    _init: function () {
        BI.DashboardChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
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
            switch (self.config.chart_dashboard_type) {
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
            formatNumberLevelInYaxis(self.config.dashboard_number_level, self.constants.LEFT_AXIS);
            config.plotOptions.valueLabel.formatter = function(){
                return getXYAxisUnit(self.config.dashboard_number_level, self.constants.DASHBOARD_AXIS);
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

    populate: function (items, options) {
        var self = this, c = this.constants;
        this.config = {
            dashboard_number_level: options.dashboard_number_level || c.NORMAL,
            dashboard_unit: options.dashboard_unit || "",
            chart_dashboard_type: options.chart_dashboard_type || c.NORMAL
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.DASHBOARD);
            });
            types.push(type);
        });

        this.combineChart.populate(items, types);
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
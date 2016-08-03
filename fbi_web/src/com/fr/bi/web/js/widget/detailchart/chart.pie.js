/**
 * 图表控件
 * @class BI.PieChart
 * @extends BI.Widget
 */
BI.PieChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.PieChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-pie-chart"
        })
    },

    _init: function () {
        BI.PieChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.PieChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;

        config.colors = this.config.chart_color;
        config.style = formatChartStyle();
        formatChartPieStyle();

        switch (this.config.chart_legend){
            case BICst.CHART_LEGENDS.BOTTOM:
                config.legend.enabled = true;
                config.legend.position = "bottom";
                config.legend.maxHeight = self.constants.LEGEND_HEIGHT;
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

        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.plotOptions.tooltip.formatter.identifier = "${CATEGORY}${SERIES}${VALUE}${PERCENT}";

        config.chartType = "pie";
        delete config.xAxis;
        delete config.yAxis;
        config.plotOptions.dataLabels.align = "outside";
        config.plotOptions.dataLabels.connectorWidth = "outside";
        config.plotOptions.dataLabels.formatter.identifier = "${VALUE}${PERCENT}";
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                da.y = da.y || 0;
                da.y = FR.contentFormat(BI.parseFloat(da.y.toFixed(4)), "#.####");
            })
        });
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

        function formatChartPieStyle(){
            switch (self.config.chart_pie_type){
                case BICst.CHART_SHAPE.EQUAL_ARC_ROSE:
                    config.plotOptions.roseType = "sameArc";
                    break;
                case BICst.CHART_SHAPE.NOT_EQUAL_ARC_ROSE:
                    config.plotOptions.roseType = "differentArc";
                    break;
                case BICst.CHART_SHAPE.NORMAL:
                default:
                    delete config.plotOptions.roseType;
                    break;
            }
            config.plotOptions.innerRadius = self.config.chart_inner_radius;
            config.plotOptions.endAngle = self.config.chart_total_angle;
        }

    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            chart_color: options.chart_color || [],
            chart_style: options.chart_style || c.NORMAL,
            chart_pie_type: options.chart_pie_type || c.NORMAL,
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            chart_inner_radius: options.chart_inner_radius || 0,
            chart_total_angle: options.chart_total_angle || BICst.PIE_ANGLES.TOTAL
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.PIE);
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
BI.PieChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.pie_chart', BI.PieChart);


/**
 * 图表控件
 * @class BI.MapChart
 * @extends BI.Widget
 */
BI.MapChart = BI.inherit(BI.Widget, {

    constants: {
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
        STYLE_NORMAL: 21
    },

    _defaultConfig: function () {
        return BI.extend(BI.MapChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-map-chart"
        })
    },

    _init: function () {
        BI.MapChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.MapChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        config.plotOptions.tooltip.formatter = this.config.tooltip;
        formatRangeLegend();
        delete config.legend;
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.geo = this.config.geo;
        config.plotOptions.tooltip.shared = true;

        config.chartType = "areaMap";
        delete config.xAxis;
        delete config.yAxis;
        return [items, config];

        function formatRangeLegend(){
            switch (self.config.chart_legend){
                case BICst.CHART_LEGENDS.BOTTOM:
                    config.rangeLegend.enabled = true;
                    config.rangeLegend.position = "bottom";
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    config.rangeLegend.enabled = true;
                    config.rangeLegend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    config.rangeLegend.enabled = false;
                    break;
            }
            config.rangeLegend.range.max = self.max;

        }
    },

    _formatDrillItems: function(items){
        var self = this;
        BI.each(items.series, function(idx, da){
            BI.each(da.data, function(idx, data){
                if(BI.has(da, "type") && da.type == "bubble"){
                    data.name = data.x;
                    data.size = data.y;
                }else{
                    data.name = data.x;
                    data.value = data.y;
                }
                if(BI.has(data, "drilldown")){
                    self._formatDrillItems(data.drilldown);
                }
            })
        })
    },

    _formatItems: function(items){
        var self = this;
        this.max = null;
        BI.each(items, function(idx, item){
            BI.each(item, function(id, it){
                BI.each(it.data, function(i, da){
                    if((BI.isNull(self.max) || da.y > self.max) && id === 0){
                        self.max = da.y;
                    }
                    if(BI.has(it, "type") && it.type == "bubble"){
                        da.name = da.x;
                        da.size = da.y;
                    }else{
                        da.name = da.x;
                        da.value = da.y;
                    }
                    if(BI.has(da, "drilldown")){
                        self._formatDrillItems(da.drilldown);
                    }
                })
            })
        });
        return items;
    },

    populate: function (items, options) {
        var self = this, c = this.constants;
        this.config = {
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            geo: options.geo || {data: BICst.MAP_PATH[BICst.MAP_TYPE.CHINA]},
            tooltip: options.tooltip || ""
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.MAP);
            });
            types.push(type);
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
BI.MapChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.map_chart', BI.MapChart);
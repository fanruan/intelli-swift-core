/**
 * 图表控件
 * @class BI.GISMapChart
 * @extends BI.Widget
 */
BI.GISMapChart = BI.inherit(BI.Widget, {

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
        return BI.extend(BI.GISMapChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-map-chart"
        })
    },

    _init: function () {
        BI.GISMapChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.GISMapChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        delete config.dataSheet;
        delete config.legend;
        delete config.zoom;
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.plotOptions.dataLabels.formatter = function() {
            //return "<div style = padding:>" + this.name + "," + this.value + "</div>";
            //"padding": 5,
            //    "backgroundColor": "rgba(0,0,0,0.4980392156862745)",
            //    "borderColor": "rgb(0,0,0)",
            //    "shadow": false,
            //    "borderRadius": 2,
            //    "borderWidth": 0,
        };
        config.plotOptions.tooltip.shared = true;
        config.plotOptions.tooltip.formatter = "function(){console.log(this.points); var tip = BI.isArray(this.name) ? '' : this.name; BI.each(this.points, function(idx, point){tip += ('<div>' + point.seriesName + ':' + (point.size || point.y) + '</div>');});return tip; }";
        config.geo = {
            "tileLayer": "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}"
        };
        config.chartType = "pointMap";
        config.plotOptions.icon = {
            iconUrl: BICst.GIS_ICON_PATH,
            iconSize: [24, 24]
        };

        config.plotOptions.marker = {
            symbol: BICst.GIS_ICON_PATH,
            width: 24,
            height: 24,
            enable: true
        };
        delete config.xAxis;
        delete config.yAxis;
        return [items, config];

    },

    _formatItems: function(items){
        BI.each(items, function(idx, item){
            BI.each(item, function(id, it){
                BI.each(it.data, function(i, da){
                    da.lnglat = da.x.split(",");
                    da.value = da.y;
                    da.name = BI.isNotNull(da.z) ? da.z : da.lnglat;
                })
            })
        });
        return items;
    },

    populate: function (items, options) {
        var self = this, c = this.constants;
        this.config = {
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(){
                type.push(BICst.WIDGET.GIS_MAP);
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
BI.GISMapChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.gis_map_chart', BI.GISMapChart);
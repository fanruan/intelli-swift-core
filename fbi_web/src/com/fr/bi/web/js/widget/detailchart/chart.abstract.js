/**
 * 图表控件
 * @class BI.AbstractChart
 * @extends BI.Widget
 */
BI.AbstractChart = BI.inherit(BI.Widget, {

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
        MINLIMIT: 1e-5,
        LEGEND_HEIGHT: 80,
        FIX_COUNT: 6,
        STYLE_NORMAL: 21,
        NO_PROJECT: 16,
        DASHBOARD_AXIS:4,
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
        SHOW: 2,
        FONT_STYLE: {
            "fontFamily": "inherit",
            "color": "#808080",
            "fontSize": "12px"
        }
    },

    _defaultConfig: function () {
        return BI.extend(BI.AbstractChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-abstract-chart"
        })
    },

    _init: function () {
        BI.AbstractChart.superclass._init.apply(this, arguments);
    },

    formatNumberLevelInYaxis: function (config, items, type, position, formatter) {
        var self = this;
        var magnify = this.calcMagnify(type);
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                if (position === item.yAxis) {
                    if (!BI.isNumber(da.y)) {
                        da.y = BI.parseFloat(da.y);
                    }
                    da.y = da.y || 0;
                    da.y = BI.contentFormat(BI.parseFloat(da.y.div(magnify).toFixed(4)), "#.####");
                }
            });
            if (position === item.yAxis && type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                item.tooltip = BI.deepClone(config.plotOptions.tooltip);
                item.tooltip.formatter.valueFormat = formatter;
            }
        });
    },

    formatNumberLevelInXaxis: function(items, type){
        var magnify = this.calcMagnify(type);
        BI.each(items, function (idx, item) {
            BI.each(item.data, function (id, da) {
                if(!BI.isNumber(da.x)){
                    da.x = BI.parseFloat(da.x);
                }
                da.x = da.x || 0;
                da.x = BI.contentFormat(BI.parseFloat(da.x.div(magnify).toFixed(4)), "#.####");
            });
        })
    },

    formatXYDataWithMagnify: function(number, magnify){
        if(!BI.isNumber(number)){
            number = BI.parseFloat(number);
        }
        number = number || 0;
        return BI.contentFormat(BI.parseFloat(number.div(magnify).toFixed(4)), "#.####");
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
/**
 * 一个区域
 *
 * Created by Kary on 2016/4/11.
 * @extends BI.AbstractRegion
 */
BI.DimensionRegionShow = BI.inherit(BI.AbstractRegion, {

    _defaultConfig: function () {
        var conf = BI.DimensionRegionShow.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: 'bi-dimension-region',
            regionType: BICst.REGION.DIMENSION1
        })
    },

    _init: function () {
        BI.DimensionRegionShow.superclass._init.apply(this, arguments);
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this.options.regionType, options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "dimension-container",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: dim,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        return container;
    },

    getValue: function () {
        var result = [];
        var dimensions = $(".dimension-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    }
});
$.shortcut("bi.dimension_region_show", BI.DimensionRegionShow);

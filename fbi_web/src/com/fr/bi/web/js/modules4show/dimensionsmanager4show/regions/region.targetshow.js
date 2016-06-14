/**
 * 一个区域
 *
 * Created by Kary on 2016/4/11.
 * @class BI.TargetRegionShow
 * @extends BI.AbstractRegion
 */
BI.TargetRegionShow = BI.inherit(BI.AbstractRegion, {

    _defaultConfig: function () {
        var conf = BI.TargetRegionShow.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: 'bi-target-region',
            regionType: BICst.REGION.TARGET1
        })
    },

    _init: function () {
        BI.TargetRegionShow.superclass._init.apply(this, arguments);
        var o = this.options, self = this;

        BI.createWidget({
            type: "bi.absolute",
            element: this.element
        })
    },

    _getRegionConnect: function () {
        return "targets-container";
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this.options.regionType, options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "target-container",
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
        var dimensions = $(".target-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    }
});
$.shortcut("bi.target_region_show", BI.TargetRegionShow);

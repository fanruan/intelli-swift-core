/**
 * Created by zcf on 2016/11/7.
 */
BI.DimensionSwitchRegionShow = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DimensionSwitchRegionShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-switch-region-show",
            dimensionType: BICst.REGION.DIMENSION1,
            dimensionCreator: BI.emptyFn(),
            cls: ""
        })
    },

    _init: function () {
        BI.DimensionSwitchRegionShow.superclass._init.apply(this, arguments);

        var o = this.options;
        this.dimensionType = o.dimensionType;
        this.center = BI.createWidget({
            type: "bi.vertical",
            cls: o.cls,
            scrolly: true,
            width: 260,
            height: "100%",
            hgap: 5,
            vgap: 5
        });
        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [this.center]
        })
    },

    _getWrapperCls: function () {
        var dType = [BICst.REGION.DIMENSION1, BICst.REGION.DIMENSION2];
        if (BI.contains(dType, this.options.dimensionType)) {
            return " dimension-wrapper"
        } else {
            return " target-wrapper"
        }
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, o.dimensionType, options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: o.cls + this._getWrapperCls(),
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
        var o = this.options;
        var result = [];
        var dimensions = $("." + o.cls, this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    },

    getDimensionType: function () {
        return this.dimensionType;
    },

    getSortableCenter: function () {
        return this.center;
    },

    populate: function (dimensions) {
        var self = this;
        BI.each(dimensions, function (i, did) {
            self.center.addItem(self._createDimension(did));
        })
    }
});
$.shortcut("bi.dimension_switch_region_show", BI.DimensionSwitchRegionShow);
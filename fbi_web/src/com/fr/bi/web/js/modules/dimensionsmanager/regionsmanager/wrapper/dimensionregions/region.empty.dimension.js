/**
 * Created by GUY on 2016/3/16.
 * @class BI.DimensionEmptyRegion
 * @extends BI.DimensionRegion
 */
BI.DimensionEmptyRegion = BI.inherit(BI.DimensionRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.DimensionEmptyRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-empty-region",
            dimensionCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.DimensionEmptyRegion.superclass._init.apply(this, arguments);
    },

});
$.shortcut("bi.dimension_empty_region", BI.DimensionEmptyRegion);
/**
 * Created by GUY on 2016/3/16.
 * @class BI.DimensionEmptyRegion
 * @extends BI.AbstractRegion
 */
BI.DimensionEmptyRegion = BI.inherit(BI.AbstractRegion, {
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

    _getDimensionClass: function () {
        return "dimension-container";
    },

    _getDimensionContainerClass: function () {
        return "dimensions-container";
    },

    _getSortableHelperClass: function () {
        return "dimension-sortable-helper";
    }
});
BI.DimensionEmptyRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_empty_region", BI.DimensionEmptyRegion);
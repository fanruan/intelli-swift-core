/**
 * Created by GUY on 2016/3/16.
 * @class BI.DimensionRegion
 * @extends BI.AbstractRegion
 */
BI.DimensionRegion = BI.inherit(BI.AbstractRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.DimensionRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-region",
            dimensionCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _getDimensionClass: function () {
        return "dimension-container";
    },

    _getDimensionContainerClass: function () {
        return "dimensions-container";
    },

    _getSortableHelperClass: function () {
        return "dimension-sortable-helper";
    },

    _dropDataFilter: function (data) {
        var self = this, o = this.options;
        data = BI.filter(data, function (i, dimension) {
            return BI.Utils.isDimensionType(dimension.type);
        });
        return data;
    }
});
$.shortcut("bi.dimension_region", BI.DimensionRegion);
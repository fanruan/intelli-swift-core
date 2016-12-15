/**
 * Created by GUY on 2016/3/16.
 * @class BI.TargetRegion
 * @extends BI.AbstractRegion
 */
BI.TargetRegion = BI.inherit(BI.AbstractRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-region",
            dimensionCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _getDimensionClass: function () {
        return "target-container";
    },

    _getDimensionContainerClass: function () {
        return "targets-container";
    },

    _getSortableHelperClass: function () {
        return "target-sortable-helper";
    },

    _dropDataFilter: function (data) {
        var self = this, o = this.options;
        data = BI.filter(data, function (i, dimension) {
            return BI.Utils.isTargetType(dimension.type);
        });
        return data;
    },

    _fieldDragStart: function () {
        var onlyCounter = !BI.some(this.dimensions, function (i, dim) {
            return BI.Utils.isTargetType();
        });
        if (onlyCounter) {
            this._showForbiddenMask();
        }
    },
});
$.shortcut("bi.target_region", BI.TargetRegion);
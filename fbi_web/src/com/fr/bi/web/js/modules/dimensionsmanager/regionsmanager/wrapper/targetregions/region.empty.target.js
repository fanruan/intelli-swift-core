/**
 * Created by GUY on 2016/3/16.
 * @class BI.TargetEmptyRegion
 * @extends BI.AbstractRegion
 */
BI.TargetEmptyRegion = BI.inherit(BI.AbstractRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetEmptyRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-empty-region",
            dimensionCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.TargetEmptyRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
    },

    _getDimensionClass: function () {
        return "target-container";
    },

    _getDimensionContainerClass: function () {
        return "targets-container";
    },

    _getSortableHelperClass: function () {
        return "target-sortable-helper";
    }
});
BI.TargetEmptyRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_empty_region", BI.TargetEmptyRegion);
/**
 * Created by GUY on 2016/3/16.
 * @class BI.TargetEmptyRegion
 * @extends BI.TargetRegion
 */
BI.TargetEmptyRegion = BI.inherit(BI.TargetRegion, {
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
    }
});
$.shortcut("bi.target_empty_region", BI.TargetEmptyRegion);
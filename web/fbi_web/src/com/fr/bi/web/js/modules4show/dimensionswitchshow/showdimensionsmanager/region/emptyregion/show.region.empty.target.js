/**
 * Created by zcf on 2016/12/28.
 */
BI.ShowTargetEmptyRegion = BI.inherit(BI.ShowTargetRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowTargetEmptyRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-empty-region",
            dimensionCreator: BI.emptyFn,
            containment: false,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.ShowTargetEmptyRegion.superclass._init.apply(this, arguments);
    },

    setVisible: function (enable) {
        if (enable) {
            this.element.css({"height": "35px"});
        } else {
            this.element.css({"height": "0%"});
        }
        this.center.element.sortable("refreshPositions");
    }
});
$.shortcut("bi.show_target_empty_region", BI.ShowTargetEmptyRegion);
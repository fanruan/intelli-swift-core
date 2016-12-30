/**
 * Created by zcf on 2016/12/26.
 */
BI.ShowTargetRegion = BI.inherit(BI.TargetRegion, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowTargetRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-target-region",
            dimensionCreator: BI.emptyFn,
            containment: false,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        })
    },

    _createEmptyTip: function () {
        return BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Drag_Field"),
            cls: "region-empty-tip",
            height: 25
        });
    },

    setVisible: function (enable) {
        if (enable) {
            this.element.css({"height": "100%"});
        } else {
            this.element.css({"height": "0%"});
        }
        this.center.element.sortable("refreshPositions");
    },

    getViewType: function () {
        return this.options.viewType;
    }
});
$.shortcut("bi.show_target_region", BI.ShowTargetRegion);
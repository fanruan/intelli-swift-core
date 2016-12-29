/**
 * Created by zcf on 2016/12/26.
 */
BI.ShowTargetRegionSettingsWrapper = BI.inherit(BI.ShowTargetRegionWrapper, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowTargetRegionSettingsWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-target-region-wrapper",
            scopeCreator: BI.emptyFn,
            wId: "",
            containment: false
        });
    },

    _createRegion: function (regionType, dIds) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.show_target_setting_region",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, regionType, op)
            },
            scopeCreator: o.scopeCreator,
            containment: o.containment,
            helperContainer: this.center,
            wId: o.wId,
            viewType: o.viewType,
            regionType: regionType
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionWrapper.EVENT_CHANGE, arguments);
        });
        region.on(BI.AbstractRegion.EVENT_START, function () {
            self.fireEvent(BI.RegionWrapper.EVENT_START, arguments);
        });
        region.on(BI.AbstractRegion.EVENT_STOP, function () {
            self.fireEvent(BI.RegionWrapper.EVENT_STOP, arguments);
        });
        return region;
    }
});
$.shortcut("bi.show_target_region_setting_wrapper", BI.ShowTargetRegionSettingsWrapper);
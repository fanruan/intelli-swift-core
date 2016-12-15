/**
 * Created by GUY on 2016/3/16.
 * @class BI.TargetRegionSettingsWrapper
 * @extends BI.TargetRegionWrapper
 */
BI.TargetRegionSettingsWrapper = BI.inherit(BI.TargetRegionWrapper, {

    _defaultConfig: function () {
        return BI.extend(BI.TargetRegionSettingsWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-region-settings-wrapper",
            scopeCreator: BI.emptyFn,
            wId: "",
        });
    },

    _createRegion: function (regionType, dIds) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.target_settings_region",
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

        return region;
    }
});
$.shortcut("bi.target_settings_region_wrapper", BI.TargetRegionSettingsWrapper);
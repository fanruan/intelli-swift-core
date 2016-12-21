/**
 * Created by GUY on 2016/3/16.
 * @class BI.TargetSettingsRegion
 * @extends BI.AbstractRegion
 */
BI.TargetSettingsRegion = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetSettingsRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-settings-region",
            dimensionCreator: BI.emptyFn,
            scopeCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.TARGET1
        });
    },

    _init: function () {
        BI.TargetSettingsRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.region = BI.createWidget({
            type: "bi.target_region",
            containment: o.containment,
            dimensionCreator: o.dimensionCreator,
            helperContainer: o.helperContainer,
            wId: o.wId,
            viewType: o.viewType,
            regionType: o.regionType
        });
        this.region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.AbstractRegion.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.default",
            element: this.element,
            rgap: 24,
            items: [this.region]
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this._createRegionSettings(),
                right: 0,
                top: 0,
                bottom: 0,
                width: 24
            }]
        });
    },

    _createRegionSettings: function () {
        var self = this, o = this.options;
        return BI.createWidget({
            type: "bi.absolute",
            cls: "target-region-settings-container",
            items: [{
                el: o.scopeCreator(o.regionType),
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        })
    },

    getValue: function () {
        return this.region.getValue();
    },

    populate: function () {
        this.region.populate.apply(this.region, arguments);
    }
});
$.shortcut("bi.target_settings_region", BI.TargetSettingsRegion);
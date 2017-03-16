/**
 * Created by zcf on 2016/12/29.
 */
BI.ShowTargetSettingsRegion=BI.inherit(BI.Widget,{
    _defaultConfig: function () {
        return BI.extend(BI.ShowTargetSettingsRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-target-settings-region",
            dimensionCreator: BI.emptyFn,
            scopeCreator: BI.emptyFn,
            wId: "",
            viewType: BICst.REGION.TARGET1,
            containment: false
        });
    },
    _init: function () {
        BI.ShowTargetSettingsRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.region = BI.createWidget({
            type: "bi.show_target_region",
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
        this.region.on(BI.AbstractRegion.EVENT_START, function () {
            self.fireEvent(BI.AbstractRegion.EVENT_START, arguments);
        });
        this.region.on(BI.AbstractRegion.EVENT_STOP, function () {
            self.fireEvent(BI.AbstractRegion.EVENT_STOP, arguments);
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

    setVisible:function (enable) {
        this.region.setVisible(enable);
        if (enable) {
            this.element.css({"height": "100%"});
        } else {
            this.element.css({"height": "0px"});
        }
    },

    getViewType:function () {
        return this.region.getViewType();
    },

    getValue: function () {
        return this.region.getValue();
    },

    populate: function () {
        this.region.populate.apply(this.region, arguments);
    }

});
$.shortcut("bi.show_target_setting_region",BI.ShowTargetSettingsRegion);
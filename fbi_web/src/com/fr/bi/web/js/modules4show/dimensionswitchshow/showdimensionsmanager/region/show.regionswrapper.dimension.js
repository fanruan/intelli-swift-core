/**
 * Created by zcf on 2016/12/26.
 */
BI.ShowDimensionRegionWrapper = BI.inherit(BI.DimensionRegionWrapper, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowDimensionRegionWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-dimension-region-wrapper",
            containment: false,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _createRegion: function (regionType, dIds) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.show_dimension_region",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, regionType, op)
            },
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
    },

    _createEmptyRegion: function () {
        var self = this, o = this.options;
        var regionType = BI.parseInt(o.viewType);
        while (this.views.contains(regionType + "")) {
            regionType++;
        }
        var region = BI.createWidget({
            type: "bi.show_dimension_empty_region",
            cls: "dimension-region-tag",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, regionType + "", op)
            },
            containment: o.containment,
            helperContainer: this.center,
            wId: o.wId,
            viewType: o.viewType
        });
        region.populate();
        return region;
    },

    setVisible: function (enable) {
        var buttons = this.center.getAllButtons();
        buttons[buttons.length - 1].setVisible(enable);
        BI.each(this.regions, function (id, region) {
            region.setVisible(enable);
        });
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
$.shortcut("bi.show_dimension_region_wrapper", BI.ShowDimensionRegionWrapper);
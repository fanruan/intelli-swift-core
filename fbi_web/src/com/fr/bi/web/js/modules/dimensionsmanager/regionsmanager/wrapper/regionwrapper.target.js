/**
 * Created by GUY on 2016/3/16.
 * @class BI.TargetRegionWrapper
 * @extends BI.RegionWrapper
 */
BI.TargetRegionWrapper = BI.inherit(BI.RegionWrapper, {

    _defaultConfig: function () {
        return BI.extend(BI.TargetRegionWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-region-wrapper",
            containment: null,
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _getRegionClass: function () {
        return "target-region";
    },

    _getEmptyRegionClass: function () {
        return "bi-target-empty-region";
    },

    _createRegion: function (regionType, dIds) {
        var self = this, o = this.options;
        if (!this.wrapper[regionType]) {
            var wrapper = this.wrapper[regionType] = BI.createWidget({
                type: "bi.layout",
                cls: "target-region target-region-tag",
                data: {
                    regionType: regionType
                }
            });

            var region = this.regions[regionType] = BI.createWidget({
                type: "bi.target_region",
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

            BI.createWidget({
                type: "bi.default",
                element: wrapper,
                items: [region],
                lgap: 11
            });
            BI.createWidget({
                type: "bi.absolute",
                element: wrapper,
                items: [{
                    el: this._createDragTool(),
                    left: 0,
                    top: 0,
                    bottom: 0
                }]
            });
        }
        this.regions[regionType].populate();
        return this.wrapper[regionType];
    },

    _createEmptyRegion: function () {
        var self = this, o = this.options;
        var regionType = BI.parseInt(o.viewType);
        while (this.views.contains(regionType + "")) {
            regionType++;
        }
        var region = BI.createWidget({
            type: "bi.target_empty_region",
            cls: "target-region-tag",
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

    getValue: function () {
        var self = this, o = this.options;
        var regions = $(".target-region-tag", this.element);
        var view = {};
        var regionType = BI.parseInt(o.viewType);
        BI.each(regions, function (i, region) {
            var dIds = [];
            var dimensions = $(".dimension-tag", region);
            BI.each(dimensions, function (j, dimension) {
                dIds.push($(dimension).data("dId"));
            });
            if (dIds.length > 0) {
                view[regionType++] = dIds;
            }
        });
        return view;
    }
});
$.shortcut("bi.target_region_wrapper", BI.TargetRegionWrapper);
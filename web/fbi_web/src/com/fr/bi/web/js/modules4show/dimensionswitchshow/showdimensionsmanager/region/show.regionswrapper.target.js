/**
 * Created by zcf on 2016/12/26.
 */
BI.ShowTargetRegionWrapper = BI.inherit(BI.ShowRegionWrapper, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowTargetRegionWrapper.superclass._defaultConfig(this, arguments), {
            baseCls: "bi-show-target-region-wrapper",
            containment: false,
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

    _createRegionWrapper: function (regionType, dIds) {
        var self = this, o = this.options;
        if (!this.wrapper[regionType]) {
            var wrapper = this.wrapper[regionType] = BI.createWidget({
                type: "bi.layout",
                cls: "target-region target-region-tag",
                data: {
                    regionType: regionType
                }
            });

            var region = this.regions[regionType] = this._createRegion(regionType, dIds);

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

    _createRegion: function (regionType, dIds) {
        var self = this, o = this.options;

        var region = BI.createWidget({
            type: "bi.show_target_region",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, regionType, op)
            },
            containment: o.containment,
            helperContainer: this.center,
            wId: o.wId,
            viewType: o.viewType,
            regionType: regionType
        });
        region.on(BI.ShowAbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowRegionWrapper.EVENT_CHANGE, arguments);
        });
        region.on(BI.ShowAbstractRegion.EVENT_START, function () {
            self.fireEvent(BI.ShowRegionWrapper.EVENT_START, arguments);
        });
        region.on(BI.ShowAbstractRegion.EVENT_STOP, function () {
            self.fireEvent(BI.ShowRegionWrapper.EVENT_STOP, arguments);
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
            type: "bi.show_target_empty_region",
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
$.shortcut("bi.show_target_region_wrapper", BI.ShowTargetRegionWrapper);
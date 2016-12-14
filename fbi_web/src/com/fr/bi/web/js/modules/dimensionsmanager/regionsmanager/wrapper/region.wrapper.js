/**
 * Created by GUY on 2016/3/16.
 * @class BI.RegionWrapper
 * @extends BI.Widget
 */
BI.RegionWrapper = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.RegionWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-region-wrapper",
        });
    },

    _init: function () {
        BI.RegionWrapper.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regions = {};
        this.wrapper = {};
        this.views = [];
        this.center = BI.createWidget({
            type: "bi.button_group",
            layouts: [{
                type: "bi.vertical",
                cls: "regions-container",
                scrolly: true,
                width: "100%",
                height: "100%",
                hgap: this.constants.REGION_DIMENSION_GAP,
                vgap: this.constants.REGION_DIMENSION_GAP
            }]
        });

        this.center.element.sortable({
            containment: o.containment.element,
            tolerance: "move",
            handle: ".drag-tool",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height()
                    });
                    holder.element.css({"margin": self.constants.REGION_DIMENSION_GAP + "px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            items: "." + this._getRegionClass(),
            cancel: "." + this._getEmptyRegionClass(),
            update: function (event, ui) {
                self.fireEvent(BI.RegionWrapper.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });

        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [this.center]
        })
    },

    _getRegionClass: function () {
    },

    _getEmptyRegionClass: function () {
    },

    _createDragTool: function () {
        var dragTool = BI.createWidget({
            type: "bi.layout",
            cls: "region-drag-background drag-tool",
            width: 10
        });
        return dragTool;
    },

    _createRegionWrapper: function (regionType, dIds) {
    },

    _createRegion: function (regionType, dIds) {
    },

    _createEmptyRegion: function () {
    },

    populate: function () {
        var self = this, o = this.options;
        var classification = BI.Utils.getWidgetViewClassificationByID(o.wId);
        var view = BI.Utils.getWidgetViewByID(o.wId);
        this.views = classification[o.viewType] || [];
        var items = [];
        BI.each(this.views, function (i, region) {
            items.push(self._createRegionWrapper(region, view[region]))
        });
        var emptyRegion = this._createEmptyRegion();
        if (BI.isNotNull(emptyRegion)) {
            items.push(emptyRegion);
        }
        BI.DOM.hang(this.wrapper);
        this.center.populate(items);
    }
});
BI.RegionWrapper.EVENT_CHANGE = "RegionWrapper.EVENT_CHANGE";
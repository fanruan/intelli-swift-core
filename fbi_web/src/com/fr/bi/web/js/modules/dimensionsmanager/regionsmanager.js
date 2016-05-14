/**
 * 区域管理器
 *
 * Created by GUY on 2016/3/17.
 * @class BI.RegionsManager
 * @extends BI.Widget
 */
BI.RegionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.RegionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-regions-manager",
            dimensionCreator: BI.emptyFn,
            regionType: BICst.Widget.TABLE,
            wId: ""
        });
    },

    _init: function () {
        BI.RegionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regions = {};
        switch (o.regionType) {
            case BICst.Widget.TABLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.Widget.TREE:
                this.regions[BICst.REGION.DIMENSION1] = this._createTreeDimensionRegion();
                break;
            case BICst.Widget.DETAIL:
                this.regions[BICst.REGION.DIMENSION1] = this._createDetailDimensionRegion();
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Column_Header"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.Widget.DATE:
            case BICst.Widget.YMD:
            case BICst.Widget.NUMBER:
            case BICst.Widget.YEAR:
            case BICst.Widget.MONTH:
            case BICst.Widget.QUARTER:
            case BICst.Widget.STRING:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Data"), BICst.REGION.DIMENSION1);
                break;
            case BICst.Widget.AXIS:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Left_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Right_Value_Axis"), BICst.REGION.TARGET2);
                break;
            case BICst.Widget.BAR:
            case BICst.Widget.ACCUMULATE_BAR:
            case BICst.Widget.DOUGHNUT:
            case BICst.Widget.RADAR:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.Widget.PIE:
            case BICst.Widget.DASHBOARD:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.Widget.MAP:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Region_Name"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.Widget.BUBBLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Y_Value"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-X_Value"), BICst.REGION.TARGET2);
                this.regions[BICst.REGION.TARGET3] = this._createTargetRegion(BI.i18nText("BI-Bubble_Size"), BICst.REGION.TARGET3);
                break;
            case BICst.Widget.SCATTER:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Y_Value"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-X_Value"), BICst.REGION.TARGET2);
                break;
        }

        BI.createWidget({
            type: "bi.float_center",
            element: this.element,
            hgap: 10,
            vgap: 10,
            items: BI.toArray(this.regions)
        })
    },

    _createTreeDimensionRegion: function () {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.tree_region",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });

        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
        });

        region.getSortableCenter().element.sortable({
            containment: this.element,
            connectWith: ".dimensions-container",
            tolerance: "pointer",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            items: ".dimension-container",
            update: function (event, ui) {
                self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        return region;
    },

    _createDetailDimensionRegion: function () {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.detail_region",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });

        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
        });

        region.getSortableCenter().element.sortable({
            containment: this.element,
            connectWith: ".dimensions-container",
            tolerance: "pointer",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            items: ".dimension-container",
            update: function (event, ui) {
                self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        return region;
    },

    _createDimensionRegion: function (titleName, regionType) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.dimension_region",
            dimensionCreator: o.dimensionCreator,
            titleName: titleName,
            wId: o.wId,
            regionType: regionType
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
        });
        region.getSortableCenter().element.sortable({
            containment: this.element,
            connectWith: ".dimensions-container",
            tolerance: "pointer",
            //helper: "clone",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            items: ".dimension-container",
            update: function (event, ui) {
                self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        return region;
    },

    _createTargetRegion: function (titleName, regionType) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.target_region",
            dimensionCreator: o.dimensionCreator,
            titleName: titleName,
            wId: o.wId,
            regionType: regionType
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
        });
        region.getSortableCenter().element.sortable({
            containment: this.element,
            connectWith: ".targets-container",
            tolerance: "pointer",
            //helper: "clone",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            items: ".target-container",
            update: function (event, ui) {
                self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        return region;
    },

    getValue: function () {
        var views = {};
        BI.each(this.regions, function (type, region) {
            views[type] = region.getValue();
        });
        return views;
    },

    populate: function (views) {
        var self = this;
        BI.each(views, function (type, dimensions) {
            self.regions[type].populate(dimensions);
        });
    }
});
BI.RegionsManager.EVENT_CHANGE = "RegionsManager.EVENT_CHANGE";
$.shortcut('bi.regions_manager', BI.RegionsManager);
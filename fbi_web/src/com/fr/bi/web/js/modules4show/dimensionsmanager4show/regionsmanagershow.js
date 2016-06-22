/**
 * 区域管理器
 * Created by wuk on 16/4/14.
 * Created by GUY on 2016/3/17.
 * @class BI.RegionsManagerShow
 * @extends BI.Widget
 */
BI.RegionsManagerShow = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.RegionsManagerShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-regions-manager",
            dimensionCreator: BI.emptyFn,
            regionType: BICst.WIDGET.TABLE,
            wId: ""
        });
    },

    _init: function () {
        BI.RegionsManagerShow.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regions = {};
        if(o.regionType >= BICst.MAP_TYPE.WORLD){
            this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Region_Name"), BICst.REGION.DIMENSION1);
            this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
            this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Region_Suspension_Target"), BICst.REGION.TARGET2)
        }
        switch (o.regionType) {
            case BICst.WIDGET.TABLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.TREE:
                this.regions[BICst.REGION.DIMENSION1] = this._createTreeDimensionRegion();
                break;
            case BICst.WIDGET.DETAIL:
                this.regions[BICst.REGION.DIMENSION1] = this._createDetailDimensionRegion();
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Column_Header"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YMD:
            case BICst.WIDGET.NUMBER:
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.STRING:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Data"), BICst.REGION.DIMENSION1);
                break;
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Left_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Right_Value_Axis"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_BAR:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Value_Axis"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Positive_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Negative_Value_Axis"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.FALL_AXIS:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Value_Axis"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.RANGE_AREA:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Value_Axis_One"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Value_Axis_Two"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Left_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Right_Value_Axis_One"), BICst.REGION.TARGET2);
                this.regions[BICst.REGION.TARGET3] = this._createTargetRegion(BI.i18nText("BI-Right_Value_Axis_Two"), BICst.REGION.TARGET3);
                break;
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.FUNNEL:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.MAP:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Region_Name"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.GIS_MAP:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Address"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Name_Title"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Region_Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.BUBBLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Y_Value"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-X_Value"), BICst.REGION.TARGET2);
                this.regions[BICst.REGION.TARGET3] = this._createTargetRegion(BI.i18nText("BI-Bubble_Size"), BICst.REGION.TARGET3);
                break;
            case BICst.WIDGET.SCATTER:
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
            self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
                self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
            type: "bi.detail_region_show",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });

        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
                self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
            type: "bi.dimension_region_show",
            dimensionCreator: o.dimensionCreator,
            titleName: titleName,
            wId: o.wId,
            regionType: regionType
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
                self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
            type: "bi.target_region_show",
            dimensionCreator: o.dimensionCreator,
            titleName: titleName,
            wId: o.wId,
            regionType: regionType
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
                self.fireEvent(BI.RegionsManagerShow.EVENT_CHANGE);
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
BI.RegionsManagerShow.EVENT_CHANGE = "RegionsManagerShow.EVENT_CHANGE";
$.shortcut('bi.regions_manager_show', BI.RegionsManagerShow);

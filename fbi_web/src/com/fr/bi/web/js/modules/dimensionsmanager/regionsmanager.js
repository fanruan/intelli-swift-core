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
            regionType: BICst.WIDGET.TABLE,
            wId: ""
        });
    },

    _init: function () {
        BI.RegionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regions = {};
        if ((o.regionType + "").indexOf(BICst.DI_TU) !== -1) {
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
            case BICst.WIDGET.TREE_LABEL:
                this.regions[BICst.REGION.DIMENSION1] = this._createTreeDimensionRegion(BI.i18nText("BI-Data"));
                break;
            case BICst.WIDGET.LIST_LABEL:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Data"), BICst.REGION.DIMENSION1);
                break;
            case BICst.WIDGET.DETAIL:
                this.regions[BICst.REGION.DIMENSION1] = this._createDetailDimensionRegion(BI.i18nText("BI-Data"));
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Column_Header"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                //动态创建region 先创建分类和列表头的wrapper
                this.regions[BI.RegionsManager.COMPLEX_REGION_CATEGORY] = this._createComplexRegionWrapper(BI.i18nText("BI-Row_Header"), BI.RegionsManager.COMPLEX_REGION_CATEGORY);
                this.regions[BI.RegionsManager.COMPLEX_REGION_COLUMN] = this._createComplexRegionWrapper(BI.i18nText("BI-Column_Header"), BI.RegionsManager.COMPLEX_REGION_COLUMN);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YMD:
            case BICst.WIDGET.NUMBER:
            case BICst.WIDGET.SINGLESLIDER:
            case BICst.WIDGET.INTERVALSLIDER:
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
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Positive_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Negative_Value_Axis"), BICst.REGION.TARGET2);
                break;
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
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Value_Axis_One"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Value_Axis_Two"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.RANGE_AREA:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Low_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-High_Value_Axis"), BICst.REGION.TARGET2);
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
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-Region_Suspension_Target"), BICst.REGION.TARGET2)
                break;
            case BICst.WIDGET.GIS_MAP:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Address"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.DIMENSION2] = this._createDimensionRegion(BI.i18nText("BI-Name_Title"), BICst.REGION.DIMENSION2);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Region_Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.BUBBLE:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Y_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-X_Value_Axis"), BICst.REGION.TARGET2);
                this.regions[BICst.REGION.TARGET3] = this._createTargetRegion(BI.i18nText("BI-Bubble_Size"), BICst.REGION.TARGET3);
                break;
            case BICst.WIDGET.SCATTER:
                this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.regions[BICst.REGION.TARGET1] = this._createTargetRegion(BI.i18nText("BI-Y_Value_Axis"), BICst.REGION.TARGET1);
                this.regions[BICst.REGION.TARGET2] = this._createTargetRegion(BI.i18nText("BI-X_Value_Axis"), BICst.REGION.TARGET2);
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

    _createTreeDimensionRegion: function (titleName) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.tree_region",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            titleName: titleName
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

    _createDetailDimensionRegion: function (titleName) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.detail_region",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            titleName: titleName
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

    _createComplexRegionWrapper: function (titleName, wrapperType) {
        var self = this, o = this.options;
        var regionWrapper = BI.createWidget({
            type: "bi.complex_region_wrapper",
            titleName: titleName,
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            wrapperType: wrapperType
        });
        var sortArea = regionWrapper.getCenterArea();
        sortArea.element.sortable({
            containment: sortArea.element,
            tolerance: "move",
            handle: ".drag-tool",
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
            items: ".bi-complex-dimension-region",
            cancel: ".bi-complex-empty-region",
            update: function (event, ui) {
                var sortedRegion = sortArea.element.sortable("toArray");
                regionWrapper.sortRegion();
                self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });

        return regionWrapper;
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

    _isComplexCategoryByType: function (type) {
        return BI.parseInt(type) < BI.parseInt(BICst.REGION.DIMENSION2);
    },

    _isComplexColumnByType: function (type) {
        return BI.parseInt(BICst.REGION.DIMENSION2) <= BI.parseInt(type) &&
            BI.parseInt(type) < BI.parseInt(BICst.REGION.TARGET1);
    },

    _bindComplexRegionEvent: function () {
        var self = this, o = this.options;
        BI.each(this.regions, function (type, region) {
            if (o.regionType === BICst.WIDGET.COMPLEX_TABLE && type !== BICst.REGION.TARGET1) {
                var subRegions = region.getRegions();
                BI.each(subRegions, function (stype, sregion) {
                    sregion.on(BI.ComplexDimensionRegion.EVENT_CHANGE, function () {
                        // self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
                    });
                    sregion.element.sortable({
                        containment: self.element,
                        connectWith: ".bi-complex-dimension-region",
                        tolerance: "pointer",
                        //helper: "clone",
                        scroll: false,
                        placeholder: {
                            element: function ($currentItem) {
                                var holder = BI.createWidget({
                                    type: "bi.label",
                                    cls: "ui-sortable-place-holder",
                                    height: $currentItem.height() - 2
                                });
                                holder.element.css({"margin": "5px 5px 5px 15px"});
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
                    })
                });
            }
        });
    },

    getValue: function () {
        var views = {}, o = this.options;
        BI.each(this.regions, function (type, region) {
            if (o.regionType === BICst.WIDGET.COMPLEX_TABLE && type !== BICst.REGION.TARGET1) {
                var subRegions = region.getRegions();
                BI.each(subRegions, function (stype, sregion) {
                    views[stype] = sregion.getValue();
                });
            } else {
                views[type] = region.getValue();
            }
        });
        return views;
    },

    populate: function (views) {
        var self = this, o = this.options;
        BI.each(views, function (type, dimensions) {
            if (o.regionType === BICst.WIDGET.COMPLEX_TABLE && type !== BICst.REGION.TARGET1) {
                //复杂表的region添加到wrapper中
                if (self._isComplexCategoryByType(type)) {
                    self.regions[BI.RegionsManager.COMPLEX_REGION_CATEGORY].refreshRegion(type, dimensions);
                } else if (self._isComplexColumnByType((type))) {
                    self.regions[BI.RegionsManager.COMPLEX_REGION_COLUMN].refreshRegion(type, dimensions);
                }
            } else {
                self.regions[type].populate(dimensions);
            }
        });
        if (o.regionType === BICst.WIDGET.COMPLEX_TABLE) {
            this._bindComplexRegionEvent();
        }
    }
});
BI.extend(BI.RegionsManager, {
    COMPLEX_REGION_CATEGORY: 100,
    COMPLEX_REGION_COLUMN: 101
});
BI.RegionsManager.EVENT_CHANGE = "RegionsManager.EVENT_CHANGE";
$.shortcut('bi.regions_manager', BI.RegionsManager);
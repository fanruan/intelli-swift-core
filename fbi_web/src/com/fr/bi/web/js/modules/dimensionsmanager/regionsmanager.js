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
        this.wrappers = {};
        if ((o.regionType + "").indexOf(BICst.DI_TU) !== -1) {
            this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Region_Name"), BICst.REGION.DIMENSION1);
            this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
            this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-Region_Suspension_Target"), BICst.REGION.TARGET2)
        }
        switch (o.regionType) {
            case BICst.WIDGET.TABLE:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.TREE:
            case BICst.WIDGET.TREE_LABEL:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createTreeDimensionRegion(BI.i18nText("BI-Data"));
                break;
            case BICst.WIDGET.LIST_LABEL:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Data"), BICst.REGION.DIMENSION1);
                break;
            case BICst.WIDGET.DETAIL:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDetailDimensionRegion(BI.i18nText("BI-Data"));
                break;
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.RECT_TREE:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.DIMENSION2] = this._createDimensionRegionWrapper(BI.i18nText("BI-Column_Header"), BICst.REGION.DIMENSION2);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                //动态创建region 先创建分类和列表头的wrapper
                this.wrappers[BICst.REGION.DIMENSION1] = this._createComplexRegionWrapper(BI.i18nText("BI-Row_Header"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.DIMENSION2] = this._createComplexRegionWrapper(BI.i18nText("BI-Column_Header"), BICst.REGION.DIMENSION2);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YMD:
            case BICst.WIDGET.NUMBER:
            case BICst.WIDGET.SINGLE_SLIDER:
            case BICst.WIDGET.INTERVAL_SLIDER:
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.STRING:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Data"), BICst.REGION.DIMENSION1);
                break;
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.DIMENSION2] = this._createDimensionRegionWrapper(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Left_Value_Axis"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-Right_Value_Axis"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_BAR:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.DIMENSION2] = this._createDimensionRegionWrapper(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Value_Axis"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Positive_Value_Axis"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-Negative_Value_Axis"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.COMPARE_AREA:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Positive_Value_Axis"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-Negative_Value_Axis"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.FALL_AXIS:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Value_Axis"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.COMPARE_BAR:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Value_Axis_One"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-Value_Axis_Two"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.RANGE_AREA:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Low_Value_Axis"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-High_Value_Axis"), BICst.REGION.TARGET2);
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Left_Value_Axis"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-Right_Value_Axis_One"), BICst.REGION.TARGET2);
                this.wrappers[BICst.REGION.TARGET3] = this._createTargetRegionWrapper(BI.i18nText("BI-Right_Value_Axis_Two"), BICst.REGION.TARGET3);
                break;
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.DIMENSION2] = this._createDimensionRegionWrapper(BI.i18nText("BI-Series"), BICst.REGION.DIMENSION2);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.MULTI_PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.FUNNEL:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.MAP:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Region_Name"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Target"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-Region_Suspension_Target"), BICst.REGION.TARGET2)
                break;
            case BICst.WIDGET.GIS_MAP:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Address"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.DIMENSION2] = this._createDimensionRegionWrapper(BI.i18nText("BI-Name_Title"), BICst.REGION.DIMENSION2);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Region_Target"), BICst.REGION.TARGET1);
                break;
            case BICst.WIDGET.BUBBLE:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Y_Value_Axis"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-X_Value_Axis"), BICst.REGION.TARGET2);
                this.wrappers[BICst.REGION.TARGET3] = this._createTargetRegionWrapper(BI.i18nText("BI-Bubble_Size"), BICst.REGION.TARGET3);
                break;
            case BICst.WIDGET.SCATTER:
                this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper(BI.i18nText("BI-Category"), BICst.REGION.DIMENSION1);
                this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper(BI.i18nText("BI-Y_Value_Axis"), BICst.REGION.TARGET1);
                this.wrappers[BICst.REGION.TARGET2] = this._createTargetRegionWrapper(BI.i18nText("BI-X_Value_Axis"), BICst.REGION.TARGET2);
                break;
        }

        BI.createWidget({
            type: "bi.float_center",
            element: this.element,
            hgap: 10,
            vgap: 10,
            items: BI.toArray(this.wrappers)
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

    _createDimensionRegionWrapper: function (titleName, wrapperType) {
        var self = this, o = this.options;
        var wrapper = BI.createWidget({
            type: "bi.dimension_region_wrapper",
            dimensionCreator: o.dimensionCreator,
            titleName: titleName,
            wId: o.wId,
            wrapperType: wrapperType
        });
        var sortArea = wrapper.getCenterArea();
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
            items: ".bi-dimension-region",
            cancel: ".bi-empty-region",
            update: function (event, ui) {
                wrapper.sortRegion();
                self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        return wrapper;
    },

    _createTargetRegionWrapper: function (titleName, wrapperType) {
        var self = this, o = this.options;
        var wrapper = BI.createWidget({
            type: "bi.target_region_wrapper",
            dimensionCreator: o.dimensionCreator,
            titleName: titleName,
            wId: o.wId,
            wrapperType: wrapperType
        });
        var sortArea = wrapper.getCenterArea();
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
            items: ".bi-dimension-region",
            cancel: ".bi-empty-region",
            update: function (event, ui) {
                wrapper.sortRegion();
                self.fireEvent(BI.RegionsManager.EVENT_CHANGE);
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        return wrapper;
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
            items: ".bi-dimension-region",
            cancel: ".bi-complex-empty-region",
            update: function (event, ui) {
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

    _bindRegionEvent: function () {
        var self = this, o = this.options;
        BI.each(this.wrappers, function (type, wrapper) {
            var element = wrapper.element;
            var wrapperType = wrapper.getWrapperType();
            var regions = wrapper.getRegions();
            BI.each(regions, function (stype, region) {
                region.getSortableCenter().element.sortable({
                    containment: self.element,
                    connectWith: wrapperType < BICst.REGION.TARGET1 ? ".dimensions-container" : ".targets-container",
                    tolerance: "pointer",
                    helper: function (event, ui) {
                        var drag = BI.createWidget();
                        drag.element.append(ui.html());
                        BI.createWidget({
                            type: "bi.default",
                            element: element,
                            items: [drag]
                        })
                        return drag.element;
                    },
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
                    items: wrapperType < BICst.REGION.TARGET1 ? ".dimension-container" : ".target-container",
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
        });
    },

    getValue: function () {
        var views = {}, o = this.options;
        BI.each(this.wrappers, function (type, wrapper) {
            var ids = [];
            if(o.regionType === BICst.WIDGET.TREE || o.regionType === BICst.WIDGET.TREE_LABEL || o.regionType === BICst.WIDGET.DETAIL) {
                views[type] = wrapper.getValue();
            } else {
                BI.each(wrapper.getRegions(), function (idx, region) {
                    ids.push(idx);
                    views[idx] = region.getValue();
                });
            }
            var emptyIds =  wrapper.getEmptyRegionValue();
            var next = (BI.parseInt(BI.max(ids)) + 1)  || BI.parseInt(wrapper.getWrapperType());
            if(BI.isNotEmptyArray(emptyIds)) {
                views[next] = emptyIds;
            }
        });
        return views;

    },

    populate: function (views) {
        var self = this, o = this.options;
        if(o.regionType === BICst.WIDGET.TREE || o.regionType === BICst.WIDGET.TREE_LABEL || o.regionType === BICst.WIDGET.DETAIL) {
            BI.each(views, function (type, dimensions) {
                self.wrappers[type].populate(dimensions);
            });
        } else {
            BI.each(views, function (type, dimensions) {
                self.wrappers[getType(type)].refreshRegion(type, dimensions);
            });
            this._bindRegionEvent();
        }
        function getType(type) {
            var first = type.toString().slice(0,1);
            var length = type.toString().length - 1;
            return BI.parseFloat(first + "e" + length);
        }
    }
});

BI.RegionsManager.EVENT_CHANGE = "RegionsManager.EVENT_CHANGE";
$.shortcut('bi.regions_manager', BI.RegionsManager);
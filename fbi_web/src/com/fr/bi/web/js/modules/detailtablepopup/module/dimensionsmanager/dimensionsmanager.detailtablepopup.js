/**
 * 指标弹出明细表维度管理
 *
 * Created by GUY on 2016/5/13.
 * @class BI.DetailTablePopupDimensionsManager
 * @extends BI.Widget
 */
BI.DetailTablePopupDimensionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupDimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup-dimensions-manager"

        });
    },

    _init: function () {
        BI.DetailTablePopupDimensionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regions = {};
        this.regions[BICst.REGION.DIMENSION1] = this._createDimensionRegion();
        BI.createWidget({
            type: "bi.float_center",
            element: this.element,
            hgap: 10,
            vgap: 10,
            items: BI.toArray(this.regions)
        })
    },

    _createDimensionRegion: function () {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.dimension_region",
            dimensionCreator: o.dimensionCreator,
            titleName: BI.i18nText("BI-Data"),
            regionType: BICst.REGION.DIMENSION1
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.DetailTablePopupDimensionsManager.EVENT_CHANGE);
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
                self.fireEvent(BI.DetailTablePopupDimensionsManager.EVENT_CHANGE);
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
        return {
            type: BICst.WIDGET.DETAIL,
            view: views
        }
    },

    populate: function () {
        var widget = Data.SharingPool.get("detailtablepopupwidget");
        var view = widget.view;
        this.regions[BICst.REGION.DIMENSION1].populate(view[BICst.REGION.DIMENSION1]);
    }
});
BI.DetailTablePopupDimensionsManager.EVENT_CHANGE = "DetailTablePopupDimensionsManager.EVENT_CHANGE";
$.shortcut('bi.detail_table_popup_dimensions_manager', BI.DetailTablePopupDimensionsManager);
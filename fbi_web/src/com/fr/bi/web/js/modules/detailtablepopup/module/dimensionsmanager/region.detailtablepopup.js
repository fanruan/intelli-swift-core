/**
 * 一个区域
 *
 * Created by GUY on 2016/5/26.
 * @class BI.DetailTablePopupRegion
 * @extends BI.Widget
 */
BI.DetailTablePopupRegion = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        var conf = BI.DetailTablePopupRegion.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: 'bi-region',
            extraCls: 'bi-detail-table-popup-dimension-region bi-dimension-region',
            regionType: BICst.REGION.DIMENSION1,
            titleName: "",
            wId: ""
        })
    },

    _init: function () {
        BI.DetailTablePopupRegion.superclass._init.apply(this, arguments);
        this._createRegion();
        this.store = {};
    },

    _createRegion: function () {
        var self = this, o = this.options;
        this.titleName = BI.createWidget({
            type: "bi.label",
            cls: "region-north-title",
            text: o.titleName,
            height: this.constants.REGION_HEIGHT_NORMAL
        });

        var north = BI.createWidget({
            type: "bi.border",
            items: {
                center: {el: this.titleName}
            },
            cls: "region-north"
        });

        this.center = BI.createWidget({
            type: "bi.vertical",
            cls: this._getRegionConnect(),
            scrolly: true,
            width: "100%",
            height: "100%",
            hgap: this.constants.REGION_DIMENSION_GAP,
            vgap: this.constants.REGION_DIMENSION_GAP
        });

        this.center.element.droppable({
            accept: ".detail-table-popup-select-data-level0-item-button, .detail-table-popup-select-date-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                var helper = ui.helper;
                var data = helper.data("data");
                if (self.options.regionType < BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return dimension.type === BICst.TARGET_TYPE.STRING || dimension.type === BICst.TARGET_TYPE.DATE || dimension.type === BICst.TARGET_TYPE.NUMBER;
                    });
                }
                BI.each(data, function (i, dimension) {
                    self.addDimension(dimension.dId || BI.UUID(), dimension)
                });
                if (data.length > 0) {
                    self.fireEvent(BI.DetailTablePopupRegion.EVENT_CHANGE);
                }
            },
            over: function (event, ui) {

            },
            out: function (event, ui) {

            }
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: north,
                height: this.constants.REGION_HEIGHT_NORMAL
            }, {
                type: "bi.default",
                items: [this.center]
            }]
        })
    },

    getSortableCenter: function () {
        return this.center;
    },

    _getRegionConnect: function () {
        return "dimensions-container";
    },

    addDimension: function (dId, options) {
        this.store[dId] = this._createDimension(dId, options);
        this.center.addItem(this.store[dId]);
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this.options.regionType, options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "dimension-container",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: dim,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        return container;
    },

    getValue: function () {
        var result = [];
        var dimensions = $(".dimension-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    },

    populate: function (dimensions) {
        var self = this, o = this.options;
        BI.DOM.hang(self.store);
        this.store = {};
        BI.each(dimensions, function (i, did) {
            self.store[did] = self._createDimension(did);
            self.center.addItem(self.store[did]);
        })
    }
});
$.shortcut("bi.detail_table_popup_dimension_region", BI.DetailTablePopupRegion);
BI.DetailTablePopupRegion.EVENT_CHANGE = "DetailTablePopupRegion.EVENT_CHANGE";
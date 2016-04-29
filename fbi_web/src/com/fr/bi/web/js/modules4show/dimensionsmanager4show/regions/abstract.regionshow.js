/**
 * 一个区域
 *
 * Created by Kary on 2016/4/11.
 * @class BI.AbstractRegionShow
 * @extends BI.Widget
 */
BI.AbstractRegionShow = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        var conf = BI.AbstractRegionShow.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: 'bi-region',
            titleName: "",
            wid: ""
        })
    },

    _init: function () {
        BI.AbstractRegionShow.superclass._init.apply(this, arguments);
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
            accept: ".select-data-level0-item-button, .select-date-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                var helper = ui.helper;
                var data = helper.data("data");
                var regionType = self._getRegionType();
                if (regionType >= BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return dimension.type === BICst.TARGET_TYPE.NUMBER || dimension.type === BICst.TARGET_TYPE.COUNTER
                    });
                }
                BI.each(data, function (i, dimension) {
                    self.addDimension(BI.UUID(), dimension)
                });
                if (data.length > 0) {
                    self.fireEvent(BI.AbstractRegionShow.EVENT_CHANGE);
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

    _getRegionType: function () {

    },

    _createDimension: function (did) {

    },

    addDimension: function (dId, options) {
        this.store[dId] = this._createDimension(dId, options);
        this.center.addItem(this.store[dId]);
    },

    getValue: function () {

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
BI.AbstractRegionShow.EVENT_CHANGE = "AbstractRegionShow.EVENT_CHANGE";

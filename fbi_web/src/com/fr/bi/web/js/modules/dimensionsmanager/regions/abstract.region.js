/**
 * 一个区域
 *
 * Created by GUY on 2016/3/17.
 * @class BI.AbstractRegion
 * @extends BI.Widget
 */
BI.AbstractRegion = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        var conf = BI.AbstractRegion.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: 'bi-region',
            titleName: "",
            wId: ""
        })
    },

    _init: function () {
        BI.AbstractRegion.superclass._init.apply(this, arguments);
        var self = this;
        this._createRegion();
        this.store = {};
        this._toggleTip();
        BI.Broadcasts.on(BICst.BROADCAST.FIELD_DRAG_START, function (fields) {
            self._fieldDragStart(fields);
        });
        BI.Broadcasts.on(BICst.BROADCAST.FIELD_DRAG_STOP, function () {
            self._fieldDragStop();
        });
    },

    _getFieldClass: function (type) {
        switch (type) {
            case BICst.REGION.DIMENSION1:
                return "classify-font";
            case BICst.REGION.DIMENSION2:
                return "classify-font";
            case BICst.REGION.TARGET1:
            case BICst.REGION.TARGET2:
            case BICst.REGION.TARGET3:
                return "series-font";
            default:
                return "classify-font";
        }
    },

    _createRegion: function () {
        var self = this, o = this.options;
        var titleName = BI.createWidget({
            type: "bi.icon_text_item",
            logic: {
                dynamic: true
            },
            cls: "region-north-title " + this._getFieldClass(o.regionType),
            text: o.titleName,
            height: this.constants.REGION_HEIGHT_NORMAL
        });

        var north = BI.createWidget({
            type: "bi.border",
            items: {
                west: {
                    el: titleName,
                    height: this.constants.REGION_HEIGHT_NORMAL,
                    left: this.constants.REGION_DIMENSION_GAP
                }
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
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                BI.size(self.store) === 0 && BI.isNotNull(self.tip) && self.tip.setVisible(true);

                var helper = ui.helper;
                var data = helper.data("data");
                if (self.options.regionType >= BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return dimension.type === BICst.TARGET_TYPE.NUMBER || dimension.type === BICst.TARGET_TYPE.COUNTER || dimension.type === BICst.TARGET_TYPE.FORMULA ||
                            dimension.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE ||
                            dimension.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE ||
                            dimension.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE ||
                            dimension.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ABOVE ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ALL ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP ||
                            dimension.type === BICst.TARGET_TYPE.RANK ||
                            dimension.type === BICst.TARGET_TYPE.RANK_IN_GROUP;
                    });
                }
                if (self.options.regionType < BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return dimension.type === BICst.TARGET_TYPE.STRING || dimension.type === BICst.TARGET_TYPE.DATE || dimension.type === BICst.TARGET_TYPE.NUMBER;
                    });
                }
                BI.each(data, function (i, dimension) {
                    self.addDimension(dimension.dId || BI.UUID(), dimension)
                });
                if (data.length > 0) {
                    self.fireEvent(BI.AbstractRegion.EVENT_CHANGE);
                }
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DROP_PREFIX);
            },
            over: function (event, ui) {
                if (BI.isNull(self.forbiddenMask) || !self.forbiddenMask.isVisible()) {
                    self.dropArea = BI.createWidget({
                        type: "bi.layout",
                        height: 25,
                        cls: "virtual-drop-area"
                    });
                    self.center.addItem(self.dropArea);
                    BI.size(self.store) === 0 && BI.isNotNull(self.tip) && self.tip.setVisible(false);
                }
                var helperWidget = ui.helper.data().helperWidget;
                var helper = self._getFieldDropOverHelper();
                if (BI.isNotNull(helper)) {
                    helperWidget.modifyContent(helper);
                }
            },
            out: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                BI.size(self.store) === 0 && BI.isNotNull(self.tip) && self.tip.setVisible(true);
                var helperWidget = ui.helper.data().helperWidget;
                helperWidget.populate();
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

    _getDragTipContent: function () {

    },

    _fieldDragStart: function (fields) {

    },

    _fieldDragStop: function () {

    },

    _allowDrop: function () {

    },

    _getFieldDropOverHelper: function () {

    },

    _showForbiddenMask: function () {
        if (BI.isNotNull(this.forbiddenMask)) {
            this.forbiddenMask.setVisible(true);
        } else {
            this.forbiddenMask = BI.createWidget({
                type: "bi.layout",
                cls: "forbidden-mask"
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this.forbiddenMask,
                    top: 25,
                    left: 0,
                    bottom: 0,
                    right: 0
                }]
            });
        }
    },

    _hideForbiddenMask: function () {
        BI.isNotNull(this.forbiddenMask) && this.forbiddenMask.setVisible(false);
    },

    addDimension: function (dId, options) {
        this.store[dId] = this._createDimension(dId, options);
        this.center.addItem(this.store[dId]);
    },

    _toggleTip: function (dimensions) {
        if (BI.isNull(dimensions) || dimensions.length === 0) {
            if (BI.isNotNull(this.tip)) {
                this.tip.setVisible(true);
            } else {
                this.tip = BI.createWidget({
                    type: "bi.label",
                    text: this._getDragTipContent(),
                    height: 25,
                    cls: "region-empty-tip"
                });
                this.center.addItem(this.tip);
            }
        } else {
            BI.isNotNull(this.tip) && this.tip.setVisible(false);
        }
    },

    getValue: function () {

    },

    populate: function (dimensions) {
        var self = this, o = this.options;
        BI.DOM.hang(this.store);
        var store = this.store;
        this.store = {};
        BI.each(dimensions, function (i, did) {
            self.store[did] = self._createDimension(did);
            self.center.addItem(self.store[did]);
        });
        this._toggleTip(dimensions);
    }
});
BI.AbstractRegion.EVENT_CHANGE = "AbstractRegion.EVENT_CHANGE";
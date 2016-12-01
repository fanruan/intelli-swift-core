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
        REGION_DIMENSION_GAP: 5,
        REGION_DIMENSION_LEFT_GAP: 15
    },

    _defaultConfig: function () {
        var conf = BI.AbstractRegion.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: 'bi-dimension-region',
            titleName: "",
            wId: ""
        })
    },

    _init: function () {
        BI.AbstractRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this._createRegion();
        this.store = {};
        this._toggleTip();
        this.element.attr("id", o.regionType);
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

        this.center = BI.createWidget({
            type: "bi.vertical",
            cls: this._getRegionConnect(),
            scrolly: true,
            width: "100%",
            height: "100%",
            lgap: this.constants.REGION_DIMENSION_LEFT_GAP,
            hgap: this.constants.REGION_DIMENSION_GAP,
            vgap: this.constants.REGION_DIMENSION_GAP
        });

        this.center.element.droppable({
            accept: ".select-data-level0-item-button, .select-data-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();

                var helper = ui.helper;
                var data = helper.data("data");
                if (self.options.regionType >= BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return BI.Utils.isTargetType(dimension.type);
                    });
                }
                if (self.options.regionType < BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return BI.Utils.isDimensionType(dimension.type);
                    });
                }
                BI.each(data, function (i, dimension) {
                    dimension.name = createDimName(dimension.name);
                    if(!BI.has(dimension, "used")){
                        dimension.used = true;
                    }
                    self.addDimension(dimension.dId || BI.UUID(), dimension);
                });
                if (data.length > 0) {
                    self.fireEvent(BI.AbstractRegion.EVENT_CHANGE);
                }
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DROP_PREFIX);

                function createDimName (fieldName) {
                    return BI.Func.createDistinctName(BI.Utils.getWidgetDimensionsByID(o.wId), fieldName);
                }
            },
            over: function (event, ui) {
                if (BI.isNull(self.forbiddenMask) || !self.forbiddenMask.isVisible()) {
                    self.dropArea = BI.createWidget({
                        type: "bi.layout",
                        height: 25,
                        cls: "virtual-drop-area"
                    });
                    self.center.addItem(self.dropArea);
                }
                var helperWidget = ui.helper.data().helperWidget;
                var helper = self._getFieldDropOverHelper();
                if (BI.isNotNull(helper)) {
                    helperWidget.modifyContent(helper);
                }
            },
            out: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                var helperWidget = ui.helper.data().helperWidget;
                helperWidget.populate();
            }
        });

        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [this.center]
        });
    },

    _toggleTip: function () {

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
                    top: 0,
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

    getRegionType: function () {
        return this.options.regionType;
    },

    getValue: function () {

    },

    populate: function (dimensions) {
        var self = this, o = this.options;
        BI.DOM.hang(this.store);
        this.store = {};
        BI.each(dimensions, function (i, did) {
            self.store[did] = self._createDimension(did);
            self.center.addItem(self.store[did]);
        });
        this._toggleTip(dimensions);
    }
});
BI.AbstractRegion.EVENT_CHANGE = "AbstractRegion.EVENT_CHANGE";
/**
 * Created by Young's on 2016/9/12.
 */
BI.ComplexDimensionRegion = BI.inherit(BI.Widget, {
    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5,
        REGION_DIMENSION_LEFT_GAP: 15
    },

    _defaultConfig: function () {
        return BI.extend(BI.ComplexDimensionRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-region",
            wId: ""
        });
    },

    _init: function () {
        BI.ComplexDimensionRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.store = {};
        this.containers = {};

        this.sinlgeRegion = BI.createWidget({
            type: "bi.vertical",
            cls: "dimensions-container",
            scrolly: true,
            width: "100%",
            height: "100%",
            lgap: this.constants.REGION_DIMENSION_LEFT_GAP,
            rgap: this.constants.REGION_DIMENSION_GAP,
            vgap: this.constants.REGION_DIMENSION_GAP
        });
        this.sinlgeRegion.element.droppable({
            accept: ".select-data-level0-item-button, .select-data-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();

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
                    self.addDimension(dimension.dId || BI.UUID(), dimension);
                });
                if (data.length > 0) {
                    self.fireEvent(BI.ComplexDimensionRegion.EVENT_CHANGE);
                }
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DROP_PREFIX);
            },
            over: function(event, ui) {
                if (BI.isNull(self.forbiddenMask) || !self.forbiddenMask.isVisible()) {
                    self.dropArea = BI.createWidget({
                        type: "bi.layout",
                        height: 25,
                        cls: "virtual-drop-area"
                    });
                    self.sinlgeRegion.addItem(self.dropArea);
                }
                var helperWidget = ui.helper.data().helperWidget;
                var helper = self._getFieldDropOverHelper();
                if (BI.isNotNull(helper)) {
                    helperWidget.modifyContent(helper);
                }
            },
            out: function(event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                var helperWidget = ui.helper.data().helperWidget;
                helperWidget.populate();
            }
        });

        // var dragTool = BI.createWidget({
        //     type: "bi.layout",
        //     cls: "complex-region-left-drag-background drag-tool",
        //     width: 10
        // });
        // BI.createWidget({
        //     type: "bi.absolute",
        //     element: this.element,
        //     items: [{
        //         el: dragTool,
        //         left: 0,
        //         top: 0,
        //         bottom: 0
        //     }]
        // });

        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [this.sinlgeRegion]
        });

        //element上加Id
        this.element.attr("id", o.regionType);

        BI.Broadcasts.on(BICst.BROADCAST.FIELD_DRAG_START, function (fields) {
            self._fieldDragStart(fields);
        });
        BI.Broadcasts.on(BICst.BROADCAST.FIELD_DRAG_STOP, function () {
            self._fieldDragStop();
        });
    },

    _fieldDragStart: function (fields) {
        this.fields = fields;
        var onlyCounter = !BI.some(fields, function (i, fieldId) {
            return BI.Utils.getFieldTypeByID(fieldId) !== BICst.COLUMN.COUNTER;
        });
        if (onlyCounter) {
            this._showForbiddenMask();
        }
    },

    _fieldDragStop: function () {
        this.fields = null;
        this._hideForbiddenMask();
    },

    _getFieldDropOverHelper: function () {
        //可以放置的字段 + 不可放置的字段
        var total = this.fields.length;
        var counters = 0;
        BI.each(this.fields, function (i, fieldId) {
            if (BI.Utils.getFieldTypeByID(fieldId) === BICst.COLUMN.COUNTER) {
                counters++;
            }
        });
        if (counters > 0 && counters !== total) {
            return BI.createWidget({
                type: "bi.left",
                cls: "helper-warning",
                items: [{
                    type: "bi.left",
                    cls: "drag-helper-active-font",
                    items: [{
                        type: "bi.icon",
                        width: 20,
                        height: 20
                    }, {
                        type: "bi.label",
                        text: total - counters
                    }],
                    lgap: 5
                }, {
                    type: "bi.left",
                    cls: "drag-helper-forbidden-font",
                    items: [{
                        type: "bi.icon",
                        width: 20,
                        height: 20
                    }, {
                        type: "bi.label",
                        text: counters
                    }],
                    lgap: 5
                }],
                rgap: 5
            });
        } else if (counters === total) {
            return BI.createWidget({
                type: "bi.left",
                cls: "helper-warning drag-helper-forbidden-font",
                items: [{
                    type: "bi.icon",
                    width: 20,
                    height: 20
                }],
                hgap: 5
            });
        }
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
        this.sinlgeRegion.addItem(this.store[dId]);
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this.options.regionType, options);
        if (this.containers[dId]) {
            BI.createWidget({
                type: "bi.absolute",
                element: this.containers[dId],
                items: [{
                    el: dim,
                    left: 0,
                    top: 0,
                    right: 0,
                    bottom: 0
                }]
            });
        } else {
            this.containers[dId] = BI.createWidget({
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
        }
        return this.containers[dId];
    },

    getRegionType: function () {
        return this.options.regionType;
    },

    getSortableCenter: function () {
        return this.sinlgeRegion;
    },

    getValue: function () {
        var self = this, o = this.options || {};
        var result = [];
        var dimensions = $(".dimension-container", this.sinlgeRegion.element);
        BI.each(dimensions, function (i, dom) {
            var dId = $(dom).data("dId");
            if (BI.isNull(self.containers[dId])) {
                var dim = o.dimensionCreator(dId, o.regionType, o);
                self.containers[dId] = BI.createWidget({
                    type: "bi.absolute",
                    element: dom,
                    items: [{
                        el: dim,
                        left: 0,
                        top: 0,
                        right: 0,
                        bottom: 0
                    }]
                });
            }
            result.push(dId);
        });
        return result;
    },

    populate: function (dimensions) {
        var self = this, o = this.options;
        BI.DOM.hang(this.store);
        this.store = {};
        BI.each(dimensions, function (i, did) {
            self.store[did] = self._createDimension(did);
            self.sinlgeRegion.addItem(self.store[did]);
        })
    }
});
BI.ComplexDimensionRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.complex_dimension_region", BI.ComplexDimensionRegion);

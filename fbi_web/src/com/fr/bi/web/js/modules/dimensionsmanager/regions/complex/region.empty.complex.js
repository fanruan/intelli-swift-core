/**
 * Created by Young's on 2016/9/12.
 */
BI.ComplexEmptyRegion = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ComplexEmptyRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-complex-empty-region"
        });
    },

    _init: function () {
        BI.ComplexEmptyRegion.superclass._init.apply(this, arguments);
        var self = this;
        var commentTip = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Drag_Left_Field"),
            cls: "drag-comment",
            height: 25
        });
        var emptyRegion = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            height: 40,
            items: [commentTip],
            vgap: 5,
            hgap: 5
        });
        emptyRegion.element.droppable({
            accept: ".select-data-level0-item-button, .select-date-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                BI.size(self.store) === 0 && BI.isNotNull(commentTip) && commentTip.setVisible(true);

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
                if (data.length > 0) {
                    self.fireEvent(BI.ComplexEmptyRegion.EVENT_CHANGE, data);
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
                    emptyRegion.addItem(self.dropArea);
                    BI.size(self.store) === 0 && BI.isNotNull(commentTip) && commentTip.setVisible(false);
                }
                var helperWidget = ui.helper.data().helperWidget;
                var helper = self._getFieldDropOverHelper();
                if (BI.isNotNull(helper)) {
                    helperWidget.modifyContent(helper);
                }
            },
            out: function(event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                BI.size(self.store) === 0 && BI.isNotNull(commentTip) && commentTip.setVisible(true);
                var helperWidget = ui.helper.data().helperWidget;
                helperWidget.populate();
            }
        });

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
    }
});
BI.extend(BI.ComplexEmptyRegion, {
    ID: "__complex_empty_region__"
});
BI.ComplexEmptyRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.complex_empty_region", BI.ComplexEmptyRegion);
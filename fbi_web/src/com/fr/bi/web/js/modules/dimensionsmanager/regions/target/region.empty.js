/**
 * Created by fay on 2016/11/16.
 */
BI.TargetEmptyRegion = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetEmptyRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-empty-region"
        });
    },

    _init: function () {
        BI.TargetEmptyRegion.superclass._init.apply(this, arguments);
        var self = this;
        var commentTip = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Drag_Left_Field"),
            cls: "drag-comment",
            height: 25
        });
        var TargetEmptyRegion = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            height: 40,
            items: [commentTip],
            vgap: 5,
            hgap: 5
        });
        TargetEmptyRegion.element.droppable({
            accept: ".select-data-level0-item-button, .select-data-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                BI.isNotNull(self.dropArea) && self.dropArea.destroy();
                BI.size(self.store) === 0 && BI.isNotNull(commentTip) && commentTip.setVisible(true);

                var helper = ui.helper;
                var data = helper.data("data");
                if (self.options.wrapperType >= BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return BI.Utils.isTargetType(dimension.type);
                    });
                }
                if (self.options.wrapperType < BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return BI.Utils.isDimensionType(dimension.type);
                    });
                }

                if (data.length > 0) {
                    self.fireEvent(BI.TargetEmptyRegion.EVENT_CHANGE, data);
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
                    TargetEmptyRegion.addItem(self.dropArea);
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
        var hasNum = BI.some(fields, function (i, fieldId) {
            var fieldType = BI.Utils.getFieldTypeByID(fieldId);
            return fieldType === BICst.COLUMN.NUMBER || fieldType === BICst.COLUMN.COUNTER;
        });
        if (!hasNum) {
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
            if (BI.Utils.getFieldTypeByID(fieldId) !== BICst.COLUMN.COUNTER &&
                BI.Utils.getFieldTypeByID(fieldId) !== BICst.COLUMN.NUMBER) {
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

    getValue: function () {
        return  BI.TargetEmptyRegion.ID;
    }
});
BI.extend(BI.TargetEmptyRegion, {
    ID: "__target_empty_region__"
});
BI.TargetEmptyRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_empty_region", BI.TargetEmptyRegion);
/**
 * ExcelView
 *
 * Created by GUY on 2016/3/30.
 * @class BI.ExcelView
 * @extends BI.Widget
 */
BI.ExcelView = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view",
            height: 25,
            tableId: "",
            mergeInfos: []
        });
    },

    _init: function () {
        BI.ExcelView.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.open = BI.createWidget({
            type: "bi.button",
            level: "success",
            height: o.height,
            text: BI.i18nText("BI-Open_Excel_View")
        });
        this.close = BI.createWidget({
            type: "bi.button",
            invisible: true,
            height: o.height,
            text: BI.i18nText("BI-Close_Excel_View")
        });
        this.table = BI.createWidget({
            type: "bi.excel_view_display_manager"
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            isDefaultInit: false,
            isNeedAdjustWidth: false,
            element: this.element,
            el: {
                type: "bi.horizontal_adapt",
                items: [this.open, this.close]
            },
            adjustLength: 10,
            popup: {
                type: "bi.popup_view",
                cls: "excel-table-popup-view",
                logic: {
                    dynamic: false
                },
                el: {
                    type: "bi.vtape",
                    items: [{
                        el: {
                            type: "bi.right",
                            items: [{
                                type: "bi.button",
                                text: BI.i18nText("BI-Close"),
                                height: 28,
                                handler: function () {
                                    self.combo.hideView();
                                }
                            }],
                            vgap: 6,
                            rgap: 10
                        },
                        height: 40
                    }, {
                        el: this.table,
                        height: "fill"
                    }]
                }
            },
            direction: "left,custom"
        });
        this.combo.on(BI.Combo.EVENT_AFTER_POPUPVIEW, function () {
            self.populate();
            self._showClose();
        });
        this.combo.on(BI.Combo.EVENT_AFTER_HIDEVIEW, function () {
            self._showOpen();
        })

    },

    _showOpen: function () {
        this.open.setVisible(true);
        this.close.setVisible(false);
    },

    _showClose: function () {
        this.open.setVisible(false);
        this.close.setVisible(true);
    },

    _setValue: function (positions) {
        var self = this;
        var fieldIdMap = {};
        var store = [];
        var dim = [];
        var draggable = {
            cursor: BICst.cursorUrl,
            cursorAt: {left: 5, top: 5},
            drag: function (e, ui) {
            },
            start: function () {
                self.combo.hideView();
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DRAG_START, dim);
            },
            stop: function () {
                self.combo.showView();
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DRAG_STOP);
            },
            helper: function (event) {
                var text;
                var id = event.target.id;
                var cellId = id.split("-")[0];
                store = [fieldIdMap[cellId]];
                if (store.length > 1) {
                    text = BI.i18nText("BI-All_Field_Count", store.length);
                } else {
                    text = BI.Utils.getFieldNameByID(store[0]);
                }
                var data = BI.map(store, function (idx, fId) {
                    var fieldType = BI.Utils.getFieldTypeByID(fId);
                    var targetType = BICst.TARGET_TYPE.STRING;
                    switch (fieldType) {
                        case BICst.COLUMN.STRING:
                            targetType = BICst.TARGET_TYPE.STRING;
                            break;
                        case BICst.COLUMN.NUMBER:
                            targetType = BICst.TARGET_TYPE.NUMBER;
                            break;
                        case BICst.COLUMN.DATE:
                            targetType = BICst.TARGET_TYPE.DATE;
                            break;
                    }
                    var data = {
                        id: fId,
                        name: BI.Utils.getFieldNameByID(fId),
                        _src: {
                            id: fId,
                            field_id: fId
                        },
                        type: targetType
                    };
                    if (targetType === BICst.TARGET_TYPE.DATE) {
                        data.group = {type: BICst.GROUP.M};
                        data.name = BI.i18nText("BI-Month_Fen") + "(" + BI.Utils.getFieldNameByID(fId) + ")";
                    }
                    return data;
                });
                dim = data;
                var help = BI.createWidget({
                    type: "bi.helper",
                    data: {data: data},
                    text: text
                });
                BI.createWidget({
                    type: "bi.absolute",
                    element: "body",
                    items: [{
                        el: help
                    }]
                });
                return help.element;
            }
        };
        BI.each(positions, function (fieldId, mark) {
            var col = mark.col, row = mark.row;
            var cellId = BI.int2Abc(BI.parseInt(col) + 1) + (BI.parseInt(row) + 1);
            fieldIdMap[cellId] = fieldId;
            self.table.setTdSelectById(true, cellId);
            self.table.setTdDraggable(cellId, draggable);
        });
    },

    isSelected: function () {

    },

    setSelected: function (b) {

    },

    doRedMark: function () {

    },

    unRedMark: function () {

    },

    doHighLight: function () {

    },

    unHighLight: function () {

    },

    populate: function () {
        var self = this, o = this.options;
        var tableId = o.tableId;
        var excelView = BI.Utils.getExcelViewByTableId(tableId);
        if (BI.isNotNull(excelView) && BI.isNotEmptyObject(excelView.positions)) {
            var excelFullName = excelView.excelFullName;
            var positions = excelView.positions;
            self.table.setExcel(excelFullName, function () {
                self._setValue(positions);
            });
        }
    }
});
$.shortcut('bi.excel_view', BI.ExcelView);
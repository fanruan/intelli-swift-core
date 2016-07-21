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
            tableId: ""
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
            type: "bi.excel_table"
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

    _createItems: function (items) {
        var self = this;
        var store = [];
        var draggable = {
            cursor: BICst.cursorUrl,
            cursorAt: {left: 5, top: 5},
            drag: function (e, ui) {
            },
            start: function () {
                self.combo.hideView();
            },
            stop: function () {
                self.combo.showView();
            },
            helper: function () {
                var text;
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
        var result = [];
        BI.each(items, function (i, row) {
            var r = [];
            BI.each(row, function (j, item) {
                BI.isEmptyString(item.text) && (item.text = " ");
                r.push(BI.extend(item.value ? {
                    type: "bi.excel_view_cell",
                    drag: draggable,
                    title: BI.Utils.getFieldNameByID(item.value),
                    handler: function () {
                        if (this.isSelected()) {
                            store.push(this.getValue());
                        } else {
                            BI.remove(store, this.getValue());
                        }
                    }
                } : {}, item))
            });
            result.push(r);
        });
        return result;
    },

    _showOpen: function () {
        this.open.setVisible(true);
        this.close.setVisible(false);
    },

    _showClose: function () {
        this.open.setVisible(false);
        this.close.setVisible(true);
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
        var o = this.options;
        var tableId = o.tableId;
        var excelView = BI.Utils.getExcelViewByTableId(tableId);
        if (BI.isNotNull(excelView) && BI.isNotEmptyObject(excelView.positions)) {
            var excel = excelView.excel;
            var positions = excelView.positions;
            var items = [];
            BI.each(excel, function (i, row) {
                var item = [];
                BI.each(row, function (j, cell) {
                    item.push({text: cell})
                });
                items.push(item);
            });
            BI.each(positions, function (id, position) {
                items[position.row][position.col].value = id;
            });
            items = this._createItems(items);
            this.table.attr("columnSize", BI.makeArray(items[0].length, ""));
            this.table.populate(items);
        }
    }
});
$.shortcut('bi.excel_view', BI.ExcelView);
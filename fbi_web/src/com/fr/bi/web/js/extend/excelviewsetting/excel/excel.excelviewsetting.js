/**
 * ExcelViewSettingExcel
 *
 * Created by GUY on 2016/4/8.
 * @class BI.ExcelViewSettingExcel
 * @extends BI.Widget
 */
BI.ExcelViewSettingExcel = BI.inherit(BI.Widget, {

    _constants: {
        SHOW_TIP: 1,
        SHOW_EXCEL: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingExcel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-setting-excel",
            tables: []
        });
    },

    _init: function () {
        BI.ExcelViewSettingExcel.superclass._init.apply(this, arguments);
        var self = this;

        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: function(v){
                switch (v) {
                    case self._constants.SHOW_TIP:
                        return BI.createWidget({
                            type: "bi.label",
                            text: BI.i18nText("BI-Please_Upload_Excel"),
                            height: 40,
                            cls: "upload-tip"
                        });
                    case self._constants.SHOW_EXCEL:
                        self.table = BI.createWidget({
                            type: "bi.excel_table",
                            isNeedMerge: false
                        });
                        return self.table;
                }
            }
        });
        this.tab.setSelect(this._constants.SHOW_TIP);
    },

    _formatItems: function (items) {
        var map = this.map = {};
        var store = this.store = {};//储存选中的cell
        var self = this;
        return BI.map(items, function (i, row) {
            map[i] = {};
            store[i] = {};
            return BI.map(row, function (j, cell) {
                map[i][j] = BI.createWidget({
                    type: "bi.excel_view_setting_cell",
                    text: cell,
                    height: 18,
                    handler: function () {
                        //if (!this.isSelected()) {
                        self.fireEvent(BI.ExcelViewSettingExcel.EVENT_CHANGE, i, j);
                        //}
                    }
                });
                return map[i][j];
            });
        });
    },

    setValue: function(positions) {
        var self = this;
        BI.each(this.store, function (i, cols) {
            BI.each(cols, function (j, col) {
                col.setSelected(false);
                col.setTitle("");
            });
            self.store[i] = {};
        });
        BI.each(positions, function(fieldId, mark) {
            var col = mark.col, row = mark.row;
            var el = self.map[row][col];
            el.setSelected(true);
            el.setTitle(self._getFieldNameByFieldId(fieldId));
            self.store[row][col] = el;
        })
    },

    _getFieldNameByFieldId: function(fieldId) {
        var allFields = this.options.all_fields;
        return allFields[fieldId].field_name;
    },

    getValue: function () {

    },

    populate: function (items) {
        if(BI.isEmptyArray(items)) {
            this.tab.setSelect(this._constants.SHOW_TIP);
            return;
        }
        this.tab.setSelect(this._constants.SHOW_EXCEL);
        this.table.attr("columnSize", BI.makeArray(items[0].length, ""));
        this.table.populate(this._formatItems(items));
    }
});
BI.ExcelViewSettingExcel.EVENT_CHANGE = "ExcelViewSettingExcel.EVENT_CHANGE";
$.shortcut('bi.excel_view_setting_excel', BI.ExcelViewSettingExcel);
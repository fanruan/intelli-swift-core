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
            tables: [],
            mergeInfos: []
        });
    },

    _init: function () {
        BI.ExcelViewSettingExcel.superclass._init.apply(this, arguments);
        var self = this;

        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: function (v) {
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
                            type: "bi.excel_view_display_manager"
                        });
                        self.table.on(BI.ExcelViewDisplayManager.CLICK, function () {
                            var currentCellId = self.table.getCurrentCellId();
                            var index = currentCellId.search(/[0-9]+/);
                            var col = currentCellId.slice(0, index);
                            var row = currentCellId.slice(index);
                            self.fireEvent(BI.ExcelViewSettingExcel.EVENT_CHANGE, BI.parseInt(row) - 1, BI.abc2Int(col) - 1);
                        });
                        return self.table;
                }
            }
        });
        this.tab.setSelect(this._constants.SHOW_TIP);
    },

    setValue: function (positions) {
        var self = this;
        BI.each(this.store, function (i, cellId) {
            self.table.setTdSelectById(false, cellId);
        });
        this.store = [];
        BI.each(positions, function (fieldId, mark) {
            var col = mark.col, row = mark.row;
            var cellId = BI.int2Abc(BI.parseInt(col) + 1) + (BI.parseInt(row) + 1);
            self.table.setTdSelectById(true, cellId);
            self.store.push(cellId);
        });
    },

    _getFieldNameByFieldId: function (fieldId) {
        var allFields = this.options.all_fields;
        return allFields[fieldId].field_name;
    },

    getValue: function () {

    },

    setExcel: function (excelId, callback) {
        if (BI.isEmptyString(excelId)) {
            this.tab.setSelect(this._constants.SHOW_TIP);
            return;
        }
        this.tab.setSelect(this._constants.SHOW_EXCEL);
        this.table.setExcel(excelId, callback);
    },

    populate: function () {

    }
});
BI.ExcelViewSettingExcel.EVENT_CHANGE = "ExcelViewSettingExcel.EVENT_CHANGE";
$.shortcut('bi.excel_view_setting_excel', BI.ExcelViewSettingExcel);
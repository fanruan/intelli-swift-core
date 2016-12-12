/**
 * Created by zcf on 2016/11/24.
 */
BI.ExcelTableDataDisplayManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelTableDataDisplayManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "",
            excelId: ""
        })
    },

    _init: function () {
        BI.ExcelTableDataDisplayManager.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.model = new BI.ExcelTableDataDisplayModel({
            excelId: o.excelId
        });

        this.excel = BI.createWidget({
            type: "bi.preview_table",
            element: this.element,
            rowSize: 25
        })
    },

    _populateExcel: function () {
        var items = this.model.getItems();
        var header = this.model.getHeader();
        this.excel.populate(items, header);
    },

    getFileName: function () {
        return this.model.getFileName();
    },

    getExcelData: function () {
        return this.model.getItems();
    },

    getExcelHeader: function () {
        return this.model.getHeader();
    },

    populate: function (excelId) {
        var self = this;
        this.model.populate(excelId, function () {
            self._populateExcel();
        })
    }
});
$.shortcut("bi.excel_table_data_display_manager", BI.ExcelTableDataDisplayManager);
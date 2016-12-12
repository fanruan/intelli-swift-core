/**
 * Created by zcf on 2016/11/28.
 */
ExcelTableDataDisplayManagerView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(ExcelTableDataDisplayManagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        ExcelTableDataDisplayManagerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;

        var uploadButton = BI.createWidget({
            type: "bi.upload_excel_button",
            text: BI.i18nText("BI-Upload_Data"),
            progressEL: this.excel,
            width: 120,
            height: 28
        });

        uploadButton.on(BI.UploadExcelButton.EVENT_AFTER_UPLOAD, function (files) {
            var file = files[files.length - 1];
            self.excelId = file.attach_id;
        });

        var excel = BI.createWidget({
            type: "bi.excel_table_data_display_manager"
        });

        var button = BI.createWidget({
            type: "bi.button",
            text: "populate",
            height: 30,
            width: 30
        });
        button.on(BI.Button.EVENT_CHANGE, function () {
            excel.populate(self.excelId);
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [uploadButton, button, excel]
        })
    }

});

ExcelTableDataDisplayManagerModel = BI.inherit(BI.Model, {});
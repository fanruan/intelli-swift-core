/**
 * Created by zcf on 2016/11/25.
 */
ExcelVDisplayManagerView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(ExcelVDisplayManagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        ExcelVDisplayManagerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        debugger;
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
            self.excelName = file.filename;
            BI.requestAsync("fr_bi_configure", "save_upload_excel", {
                fileId: self.excelId
            }, function () {
                console.log("success");
            })
        });

        var excel = BI.createWidget({
            type: "bi.excel_view_display_manager"
        });
        excel.on(BI.ExcelViewDisplayManager.CLICK, function () {
            var id = excel.getCurrentCellId();
            excel.setTdSelectById(true, id);
            // excel.setTdDraggable(id);
        });

        var button = BI.createWidget({
            type: "bi.button",
            title: "123",
            text: "populate",
            height: 30,
            width: 30
        });
        button.on(BI.Button.EVENT_CHANGE, function () {
            excel.setExcel("62fc121e-628b-4fc2-babf-078f1e3ac769统计组件(1).xlsx", function () {

            });
            // excel.populate(self.excelId+self.excelName);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [uploadButton, button, excel]
        })
    }

});

ExcelVDisplayManagerModel = BI.inherit(BI.Model, {});
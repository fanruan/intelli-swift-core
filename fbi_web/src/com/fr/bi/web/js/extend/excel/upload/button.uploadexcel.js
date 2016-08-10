/**
 * Created by Young's on 2016/3/14.
 */
BI.UploadExcelButton = BI.inherit(BI.Button, {
    _defaultConfig: function () {
        return BI.extend(BI.UploadExcelButton.superclass._defaultConfig.apply(this, arguments), {
            progressEL: BICst.BODY_ELEMENT,
            level: "common"
        })
    },

    _init: function () {
        BI.UploadExcelButton.superclass._init.apply(this, arguments);
        var self = this;
        this.file = BI.createWidget({
            type: "bi.upload_file_with_progress",
            progressEL: this.options.progressEL,
            accept: "*.csv;*.xls;*.xlsx",
            maxSize: 500 * 1024 * 1024
        });
        this.file.on(BI.UploadFileWithProgress.EVENT_CHANGE, function(){
            this.upload();
        });
        this.file.on(BI.UploadFileWithProgress.EVENT_UPLOADED, function () {
            var files = this.getValue();
            self.fireEvent(BI.UploadExcelButton.EVENT_AFTER_UPLOAD, files);
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.file,
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            }]
        });
    },
    
    upload: function(){
        this.file.upload();
    }
});
BI.UploadExcelButton.EVENT_AFTER_UPLOAD = "EVENT_AFTER_UPLOAD";
$.shortcut("bi.upload_excel_button", BI.UploadExcelButton);
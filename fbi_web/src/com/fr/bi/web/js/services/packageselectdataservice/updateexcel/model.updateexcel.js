/**
 * Created by zcf on 2017/1/9.
 */
BI.UpdateExcelModel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.UpdateExcelModel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "",
            tableId: ""
        })
    },

    _init: function () {
        BI.UpdateExcelModel.superclass._init.apply(this, arguments);
        this.populate();
    },

    setFile: function (file, preProcess, callback, complete) {
        var self = this;
        this.file = file;
        this.excelFileName = file.filename;
        this.fullFileName = file.attach_id + this.fileName;
        preProcess();
        Data.Req.reqSaveFileGetExcelData({fileId: file.attach_id}, function (date) {

            // callback(res);
        }, complete)

    },

    getFields: function () {
        return this.fields || [];
    },

    getUpdateDate: function () {
        return this.excelUpdateDate;
    },

    getExcelName: function () {
        return this.excelFileName;
    },

    populate: function () {
        var self = this, o = this.options;
        this.tableId = o.tableId;
        this.fields = BI.Utils.getFieldsByTableId(this.tableId);
        this.excelFileName = "";
        this.excelUpdateDate = "";
    }
});

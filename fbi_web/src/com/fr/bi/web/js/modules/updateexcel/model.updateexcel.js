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

    _isEqualFields: function (newFieldsName, oldFieldsName) {
        if (newFieldsName.length !== oldFieldsName.length) {
            return false;
        }
        return BI.every(oldFieldsName, function (i, field) {
            return newFieldsName[i] === oldFieldsName[i];
        })
    },

    _getDate: function () {
        var date = new Date();
        return date.toLocaleString();
    },

    setFile: function (file, preProcess, callback, complete) {
        var self = this;
        var excelFileName = file.filename;
        var fullFileName = file.attach_id + excelFileName;
        preProcess();
        Data.Req.reqDeziSaveFileGetExcelData({fileId: file.attach_id}, function (data) {
            self.validation = false;
            //对比前一次fields
            var newFields = data.fields[0];
            var newFieldsName = BI.pluck(newFields, "field_name");
            var oldFieldsName = BI.pluck(self.fields, "field_name");

            if (self._isEqualFields(newFieldsName, oldFieldsName)) {
                self.validation = true;
                self.excelFileName = excelFileName;
                self.excelUpdateDate = self._getDate();
                self.fullFileName = fullFileName;
            }
            callback();
        }, complete)
    },

    getFullFileName: function () {
        return this.fullFileName;
    },

    getValidation: function () {
        return this.validation;
    },

    getFields: function () {
        return this.fields;
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
        this.validation = false;
        this.excelFileName = "";
        this.excelUpdateDate = "";
        this.fullFileName = "";
    }
});

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

    _getFieldNamesArray: function () {
        var fieldNamesArray = [];
        BI.each(this.fields, function (i, fs) {
            BI.each(fs, function (j, field) {
                fieldNamesArray.push(field.field_name);
            })
        });
        return fieldNamesArray;
    },

<<<<<<< HEAD
=======
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

>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
    setFile: function (file, preProcess, callback, complete) {
        var self = this;
        var excelFileName = file.filename;
        var fullFileName = file.attach_id + excelFileName;
        preProcess();
<<<<<<< HEAD
        Data.Req.reqSaveFileGetExcelData({fileId: file.attach_id}, function (data) {
=======
        Data.Req.reqDeziSaveFileGetExcelData({fileId: file.attach_id}, function (data) {
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
            self.validation = false;
            //对比前一次fields
            var newFields = data.fields[0];
            var newFieldsName = BI.pluck(newFields, "field_name");
            var oldFieldsName = BI.pluck(self.fields, "field_name");
<<<<<<< HEAD
            var intersectionFields = BI.intersection(newFieldsName, oldFieldsName);
            if (intersectionFields.length === oldFieldsName.length && intersectionFields.length === newFieldsName.length) {
                self.validation = true;
                self.excelFileName = excelFileName;
                self.excelUpdateDate = new Date();
=======

            if (self._isEqualFields(newFieldsName, oldFieldsName)) {
                self.validation = true;
                self.excelFileName = excelFileName;
                self.excelUpdateDate = self._getDate();
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
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

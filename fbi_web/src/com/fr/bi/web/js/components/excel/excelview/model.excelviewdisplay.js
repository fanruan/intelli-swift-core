/**
 * Created by zcf on 2016/11/25.
 */
BI.ExcelViewDisplayModel = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewDisplayModel.superclass._defaultConfig.apply(this, arguments), {
            excelId: ""
        });
    },

    _init: function () {
        BI.ExcelViewDisplayModel.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        this.excel = [];
        this.mergeInfos = [];
        this.excelId = o.excelId;
        this.fileName = "";
    },

    setExcelId: function (excelId) {
        this.excelId = excelId;
    },

    getItems: function () {
        return this.excel;
    },

    getFileName: function () {
        return this.fileName;
    },

    getMergeInfos: function () {
        return this.mergeInfos;
    },

    getExcelId: function () {
        return this.excelId;
    },

    populate: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.saveFileGetExcelViewData(this.excelId, function (data) {
            self.fileName = data.fileName;
            self.excel = [];
            self.mergeInfos = data.mergeInfos;

            var row = [];
            BI.each(data.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    row.push(field.field_name);
                });
            });
            self.excel.push(row);
            BI.each(data.data, function (i, d) {
                self.excel.push(d);
            });
            callback();
        }, function () {
            mask.destroy();
        })
    }
});
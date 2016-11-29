/**
 * Created by zcf on 2016/11/28.
 */
BI.ExcelTableDataDisplayModel = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelTableDataDisplayModel.superclass._defaultConfig.apply(this, arguments), {
            excelId: ""
        })
    },

    _init: function () {
        BI.ExcelTableDataDisplayModel.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        this.excelId = o.excelId;
        this.header = [];
        this.items = [];
        this.fileName = "";
    },

    setExcelId: function (excelId) {
        this.excelId = excelId;
    },

    getExcelId: function () {
        return this.excelId;
    },

    getFileName: function () {
        return this.fileName;
    },

    getItems: function () {
        return this.items;
    },

    getHeader: function () {
        return this.header;
    },

    populate: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.saveFileGetExcelData(this.excelId, function (data) {
            var header = [];
            var items = [];

            BI.each(data.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    header.push({
                        text: field.field_name
                    })
                });
            });

            BI.each(data.data, function (row, rowData) {
                var rowItems = [];
                BI.each(rowData, function (i, v) {
                    rowItems.push({
                        text: v
                    })
                });
                items.push(rowItems);
            });

            self.header = [header];
            self.items = items;
            self.fileName = data.fileName;

            callback();
        }, function () {
            mask.destroy();
        })
    }
});
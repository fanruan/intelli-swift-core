/**
 * ExcelViewSettingModel
 *
 * Created by GUY on 2016/4/7.
 * @class BI.ExcelViewSettingModel
 * @extends BI.Widget
 */
BI.ExcelViewSettingModel = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingModel.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.ExcelViewSettingModel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.excel = [];
        this.mergeInfos = [];
        this.positions = {};
        this.excelName = "";
        this.excelFullName = "";
        var table = o.table;
        var tableFields = table.fields;
        var tableId = table.id;
        var translations = table.translations;

        var view = o.view;
        if (BI.isNotNull(view) && BI.isNotNull(view.name)) {
            this.excelName = view.name;
            this.excelFullName = view.excelFullName;
            this.positions = view.positions;
        }

        this.tables = [];
        var fields = [], allPrimaryFields = [];
        BI.each(tableFields, function (i, fs) {
            BI.each(fs, function (j, field) {
                var primaryFields = [];
                fields.push({
                    field: translations[field.id] || field.field_name,
                    value: field.id
                });
                BI.Utils.getPrimaryFieldsByFieldId4Conf(field.id, primaryFields);
                allPrimaryFields = allPrimaryFields.concat(primaryFields);
            });
        });
        this.tables.push({
            open: true,
            value: tableId,
            tableName: translations[tableId],
            fields: fields
        });

        this._initPrimaryTableInfo(allPrimaryFields, tableId);
    },

    _initPrimaryTableInfo: function (primaryFields, baseTableId) {
        var self = this;
        BI.each(primaryFields, function (i, pId) {
            var tableId = BI.Utils.getTableIdByFieldId4Conf(pId);
            var fieldIds = BI.Utils.getFieldIdsByTableId4Conf(tableId);
            var fields = [];
            BI.each(fieldIds, function (i, fId) {
                fields.push({
                    field: BI.Utils.getTransNameById4Conf(fId) || BI.Utils.getFieldNameById4Conf(fId),
                    value: fId
                });
            });
            tableId !== baseTableId && self.tables.push({
                value: tableId,
                tableName: BI.Utils.getTransNameById4Conf(tableId),
                fields: fields
            })
        });
    },

    _searchFieldByRowCol: function (row, col) {
        var fieldId = null;
        BI.some(this.positions, function (id, position) {
            if (position.row === row && position.col === col) {
                fieldId = id;
                return true;
            }
        });
        return fieldId;
    },

    _getAllFields: function () {
        var result = [];
        BI.each(this.tables, function (i, table) {
            BI.each(table.fields, function (i, field) {
                result.push(field.value);
            });
        });
        return result;
    },

    setFile: function (file, callback) {
        this.file = file;
        this.excelName = file.filename;
        this.excelFullName = file.attach_id + this.excelName;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        this.clearRowCol();
        BI.requestAsync("fr_bi_configure", "save_upload_excel", {
            fileId: file.attach_id
        }, function () {
            callback();
            mask.destroy();
        }, BI.emptyFn);
    },

    setRowColOnField: function (field, row, col) {
        var preFieldId = this._searchFieldByRowCol(row, col);
        if (BI.isNotNull(preFieldId)) {
            delete this.positions[preFieldId];
        }
        this.positions[field] = {row: row, col: col};
    },

    getPositions: function () {
        return this.positions;
    },

    getAllFields: function () {
        return this.allFields;
    },

    getTables: function () {
        var self = this;
        var tables = BI.deepClone(this.tables);
        BI.each(tables, function (i, table) {
            BI.each(table.fields, function (i, field) {
                if (BI.isNotNull(self.positions[field.value])) {
                    BI.extend(field, self.positions[field.value]);
                }
            })
        });
        return tables;
    },

    getNextField: function (field) {
        var fields = this._getAllFields();
        if (BI.isNull(field)) {
            return fields[0];
        }
        var index = fields.indexOf(field);
        if (index > -1) {
            return fields[index + 1];
        }
    },

    getExcelFullName: function () {
        return this.excelFullName || "";
    },

    setExcelFullName: function (name) {
        this.excelFullName = name;
    },

    getExcelName: function () {
        return this.excelName;
    },

    setExcelName: function (name) {
        this.excelName = name;
    },

    clearRowCol: function () {
        this.excel = [];
        this.positions = {};
    },

    clearOneCell: function (fieldId) {
        delete  this.positions[fieldId];
    }
});
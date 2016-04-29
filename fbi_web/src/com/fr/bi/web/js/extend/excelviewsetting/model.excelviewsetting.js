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
        this.positions = {};
        this.excelName = "";
        var table = o.table;
        var relations = table.relations;
        var translations = table.translations;
        var tableFields = table.fields;
        var tableId = table.id;
        this.allFields = table.all_fields;

        var view = o.view;
        if(BI.isNotNull(view)) {
            this.excelName = view.name;
            this.excel = view.excel;
            this.positions = view.positions;
        }

        this.tables = [];
        var fields = [];
        BI.each(tableFields, function(i, fs) {
            BI.each(fs, function(j, field) {
                fields.push({
                    field: field.field_name,
                    value: field.id
                });
            });
        });
        this.tables.push({
            open: true,
            value: tableId,
            tableName: translations[tableId],
            fields: fields
        });

        //这里找到所有主表
        var foreignKeyMap = relations.foreignKeyMap;
        BI.each(foreignKeyMap, function(fieldId, maps) {
            if(BI.isNotNull(self.allFields[fieldId]) && tableId === self.allFields[fieldId].table_id) {
                BI.each(maps, function(i, map) {
                    var pFieldId = map.primaryKey.field_id;
                    var pTableId = self.allFields[pFieldId].table_id;
                    var pFields = [];
                    BI.each(self.allFields, function(fId, field) {
                        if(field.table_id === pTableId && field.field_type !== BICst.COLUMN.COUNTER) {
                            pFields.push({
                                field: field.field_name,
                                value: field.id
                            });
                        }
                    });
                    self.tables.push({
                        value: pTableId,
                        tableName: translations[pTableId],
                        fields: pFields
                    })
                })
            }
        });
    },

    _searchFieldByRowCol: function (row, col) {
        var fieldId = null;
        BI.some(this.positions, function (id, position) {
            if(position.row === row && position.col === col) {
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

    setFile: function(file, callback){
        var self = this;
        this.file = file;
        this.excelName = file.filename;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        this.clearRowCol();
        BI.Utils.saveFileGetExcelData(file.attach_id, function(data){
            self.excel = [];
            var row = [];
            BI.each(data.fields, function(i, fs){
                BI.each(fs, function(j, field){
                    row.push(field.field_name);
                });
            });
            self.excel.push(row);
            BI.each(data.data, function(i, d){
                self.excel.push(d);
            });
            callback();
            mask.destroy();
        })
    },

    setRowColOnField: function (field, row, col) {
        var preFieldId = this._searchFieldByRowCol(row, col);
        if (BI.isNotNull(preFieldId)) {
            delete this.positions[preFieldId];
        }
        this.positions[field] = {row: row, col: col};
    },

    getPositions: function(){
        return this.positions;
    },

    getExcelData: function(){
        return this.excel;
    },

    getAllFields: function() {
        return this.allFields;
    },

    getTables: function () {
        var self = this;
        var tables = BI.deepClone(this.tables);
        BI.each(tables, function(i, table) {
            BI.each(table.fields, function(i, field) {
                if(BI.isNotNull(self.positions[field.value])) {
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

    getExcelName: function(){
        return this.excelName;
    },

    clearRowCol: function () {
        this.positions = {};
    }
});
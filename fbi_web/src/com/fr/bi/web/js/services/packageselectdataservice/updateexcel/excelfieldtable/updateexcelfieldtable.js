/**
 * Created by zcf on 2017/1/11.
 */
BI.UpdateExcelFieldsTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.UpdateExcelFieldsTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-excel-fields-table",
            fields: []
        })
    },
    _init: function () {
        BI.UpdateExcelFieldsTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var fields = o.fields;
        var table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            isNeedMerge: false,
            columnSize: [250, 250],
            header: [[{
                type: "bi.label",
                text: BI.i18nText("BI-Field_Name"),
                cls: "table-header",
                textAlign: "left",
                lgap: 10,
                height: 25
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Field_Type"),
                cls: "table-header",
                textAlign: "left",
                lgap: 10,
                height: 25
            }]],
            items: this._createTableItems(fields)
        })
    },
    _createTableItems: function (fields) {
        var self = this;
        var items = [];
        BI.each(fields, function (i, field) {
            var item = [];
            item.push({
                type: "bi.label",
                cls: "fields-name",
                text: field.field_name,
                textAlign: "left"
            });
            item.push({
                type: "bi.left",
                cls: self._getFieldCls(field.field_type)+" fields-type",
                items: [{
                    type: "bi.icon",
                    height: 20,
                    width: 20
                }]
            });
            items.push(item);
        });
        return items;
    },

    _getFieldCls: function (fieldType) {
        switch (fieldType) {
            case BICst.COLUMN.STRING:
                return "excel-field-type-string-font";
            case BICst.COLUMN.NUMBER:
                return "excel-field-type-number-font";
            case BICst.COLUMN.DATE:
                return "excel-field-type-date-font";
            default:
                return "chart-string-font";
        }
    }
});
$.shortcut("bi.update_excel_field_table", BI.UpdateExcelFieldsTable);
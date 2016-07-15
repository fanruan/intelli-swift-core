/**
 * Created by Young's on 2016/3/15.
 */
BI.ExcelFieldSetTable = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.ExcelFieldSetTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-field-set-table"
        })
    },

    _init: function(){
        BI.ExcelFieldSetTable.superclass._init.apply(this, arguments);
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

    _createTableItems: function(fields){
        var self = this, items = [], isNewArray = this.options.isNewArray;
        var index = 0;
        BI.each(fields, function(i, fs){
            BI.each(fs, function(j, field){
                var item = [];
                item.push({
                    type: "bi.label",
                    text: field.field_name,
                    textAlign: "left",
                    cls: isNewArray[index] ? "new-field" : "normal-field",
                    lgap: 10
                });
                var fieldTypeCombo = BI.createWidget({
                    type: "bi.excel_field_set_combo",
                    fieldType: field.field_type
                });
                fieldTypeCombo.on(BI.ExcelFieldSetCombo.EVENT_CHANGE, function(){
                    self.fireEvent(BI.ExcelFieldSetTable.EVENT_CHANGE, {
                        fieldName: field.field_name,
                        fieldType: this.getValue()[0]
                    });
                });
                item.push(fieldTypeCombo);
                items.push(item);
                index++;
            });
        });
        return items;
    }

});
BI.ExcelFieldSetTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.excel_field_set_table", BI.ExcelFieldSetTable);
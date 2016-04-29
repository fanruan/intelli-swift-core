/**
 * Created by Young's on 2016/3/16.
 */
BI.ExcelFieldTypeItem = BI.inherit(BI.BasicButton, {

    _defaultConfig: function(){
        var conf = BI.ExcelFieldTypeItem.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-excel-field-type-item"
        })
    },

    _init: function(){
        BI.ExcelFieldTypeItem.superclass._init.apply(this, arguments);
        var o = this.options;
        var iconCls = "chart-string-font", fieldType = o.value;
        switch (fieldType){
            case BICst.COLUMN.STRING:
                iconCls = "excel-field-type-string-font";
                break;
            case BICst.COLUMN.NUMBER:
                iconCls = "excel-field-type-number-font";
                break;
            case BICst.COLUMN.DATE:
                iconCls = "excel-field-type-date-font";
                break;
        }
        this.checkMark = BI.createWidget({
            type: "bi.icon_button",
            cls: "item-check-font",
            width: 30,
            height: 30
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.checkMark, {
                type: "bi.icon_button",
                cls: iconCls,
                width: 30,
                height: 30
            }]
        });
    },

    doClick: function(){
        BI.ExcelFieldTypeItem.superclass.doClick.apply(this, arguments);
    },

    setSelected: function(v){
        BI.ExcelFieldTypeItem.superclass.setSelected.apply(this, arguments);
        this.checkMark.setSelected(v);
    },

    getValue: function(){
        return this.options.value;
    }
});
$.shortcut("bi.excel_field_type_item", BI.ExcelFieldTypeItem);
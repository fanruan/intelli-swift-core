/**
 * Created by Young's on 2016/3/15.
 */
BI.ExcelFieldSet = BI.inherit(BI.Widget, {

    constants: {
        COMMENT_LABEL_HEIGHT: 30,
        FIELD_SET_COMMENT_HEIGHT: 40
    },

    _defaultConfig: function(){
        return BI.extend(BI.ExcelFieldSet.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-field-set"
        })
    },

    _init: function(){
        BI.ExcelFieldSet.superclass._init.apply(this, arguments);
        var self = this;
        var excelFields = BI.createWidget({
            type: "bi.excel_field_set_table",
            fields: this.options.fields,
            isNewArray: this.options.isNewArray
        });
        excelFields.on(BI.ExcelFieldSetTable.EVENT_CHANGE, function(data){
            self.fireEvent(BI.ExcelFieldSet.EVENT_CHANGE, data);
        });
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.label",
                    cls: "field-set-comment",
                    text: BI.i18nText("BI-Table_Field_Info_Comment1"),
                    height: this.constants.COMMENT_LABEL_HEIGHT,
                    textAlign: "left"
                },
                height: this.constants.COMMENT_LABEL_HEIGHT
            }, {
                el: {
                    type: "bi.label",
                    cls: "field-set-comment",
                    text: BI.i18nText("BI-Table_Field_Info_Comment2"),
                    height: this.constants.COMMENT_LABEL_HEIGHT,
                    textAlign: "left"
                },
                height: this.constants.COMMENT_LABEL_HEIGHT
            }, {
                el: {
                    type: "bi.label",
                    cls: "field-set-comment",
                    text: BI.i18nText("BI-Field_Type_Setting"),
                    height: this.constants.FIELD_SET_COMMENT_HEIGHT,
                    textAlign: "left"
                },
                height: this.constants.FIELD_SET_COMMENT_HEIGHT
            }, {
                el: excelFields,
                height: "fill"
            }]
        })
    }
});
BI.ExcelFieldSet.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.excel_field_set",BI.ExcelFieldSet);
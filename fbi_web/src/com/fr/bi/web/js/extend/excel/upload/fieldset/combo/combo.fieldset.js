/**
 * Created by Young's on 2016/3/15.
 */
BI.ExcelFieldSetCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.ExcelFieldSetCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-field-set-combo"
        })
    },

    _init: function(){
        BI.ExcelFieldSetCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var trigger = BI.createWidget({
            type: "bi.field_set_trigger"
        });
        var popup = BI.createWidget({
            type: "bi.field_set_popup",
            items: [{
                value: BICst.COLUMN.STRING
            }, {
                value: BICst.COLUMN.NUMBER
            }, {
                value: BICst.COLUMN.DATE
            }]
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            el: trigger,
            popup: {
                el: popup
            }
        });
        popup.on(BI.FieldSetPopup.EVENT_CHANGE, function(){
            self.combo.hideView();
        });
        this.combo.setValue([o.fieldType]);
        this.combo.on(BI.Combo.EVENT_CHANGE, function(){
            self.fireEvent(BI.ExcelFieldSetCombo.EVENT_CHANGE);
        });
    },

    getValue: function(){
        return this.combo.getValue();
    },

    setValue: function(v){
        this.combo.setValue(v);
    }
});
BI.ExcelFieldSetCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.excel_field_set_combo", BI.ExcelFieldSetCombo);
/**
 * @class BI.CustomMultiDateCombo
 * @extend BI.Widget
 * 日期控件——适配后台数据
 */
BI.CustomMultiDateCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.CustomMultiDateCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-multi-date-combo"
        })
    },

    _init: function(){
        BI.CustomMultiDateCombo.superclass._init.apply(this, arguments);
        var self = this;
        this.multiDate = BI.createWidget({
            type: "bi.multidate_combo",
            element: this.element
        });
        this.multiDate.on(BI.MultiDateCombo.EVENT_CHANGE,function(){
            self.fireEvent(BI.CustomMultiDateCombo.EVENT_CHANGE);
        });
    },

    getValue: function(){
        return this.multiDate.getValue();
    },

    setValue: function(v){
        if(BI.isEmpty(v)) {
            this.multiDate.setValue();
            return;
        }
        this.multiDate.setValue(v);
    }
});
BI.CustomMultiDateCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_multi_date_combo", BI.CustomMultiDateCombo);
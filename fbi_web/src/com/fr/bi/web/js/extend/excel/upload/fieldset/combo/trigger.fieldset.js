/**
 * Created by Young's on 2016/3/15.
 */
BI.FieldSetTrigger = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.FieldSetTrigger.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-field-set-trigger"
        })
    },

    _init: function(){
        BI.FieldSetTrigger.superclass._init.apply(this, arguments);
        this.value = this.options.value;
        this.iconChangeButton = BI.createWidget({
            type: "bi.icon_change_button"
        });
        this.iconChangeButton.setIcon(this.getIconByType(this.value));
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: this.element,
            items: {
                left: [this.iconChangeButton],
                right: [{
                    type: "bi.icon_button",
                    cls: "excel-field-type-pull-down-font"
                }]
            },
            lhgap: 10,
            rhgap: 10,
            height: 25
        })
    },

    getIconByType: function(v){
        switch (v){
            case BICst.COLUMN.STRING:
                return "chart-string-font";
            case BICst.COLUMN.NUMBER:
                return "chart-number-font";
            case BICst.COLUMN.DATE:
                return "chart-date-font";
        }
    },

    setValue: function(v){
        this.value = v[0];
        this.iconChangeButton.setIcon(this.getIconByType(v[0]));
    },

    getValue: function(){
        return this.value;
    }
});
$.shortcut("bi.field_set_trigger", BI.FieldSetTrigger);
BI.ConfNumberIntervalCustomGroupOther = BI.inherit(BI.Widget, {

    constants:{
        editorWidth:88,
        editorHeight:28,
        lgap:5
    },

    _defaultConfig: function () {
        return BI.extend(BI.ConfNumberIntervalCustomGroupOther.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-number-custom-group-other"
        })
    },
    _init: function () {
        var self = this, o = this.options;
        BI.ConfNumberIntervalCustomGroupOther.superclass._init.apply(this, arguments);
        this.checkbox = BI.createWidget({
            type: "bi.checkbox"
        });
        this.label = BI.createWidget({
            type: "bi.label",
            cls:"group-other-label",
            value: BI.i18nText("BI-The_Left_Values")
        })
        this.editor = BI.createWidget({
            type: "bi.editor",
            cls:"custom-group-editor",
            lgap:this.constants.lgap,
            value: BI.i18nText("BI-Others"),
            width: this.constants.editorWidth,
            height: this.constants.editorHeight
        })

        this.checkbox.on(BI.Checkbox.EVENT_CHANGE,function(){
            if(self.checkbox.isSelected()){
                self.editor.setEnable(true);
            }else{
                self.editor.setEnable(false);
            }
        });

        this.checkbox.setSelected(true);

        BI.createWidget({
            element: this.element,
            type: "bi.horizontal",
            lgap: this.constants.lgap,
            items: [
                self.checkbox,
                self.label,
                self.editor
            ]
        })
    },

    isValid:function(){
        return this.checkbox.isSelected();
    },

    getValue:function(){
        return this.editor.getValue();
    },

    setValue:function(v){
        this.checkbox.setSelected(BI.isNotNull(v));
        this.editor.setValue(BI.isNotNull(v) ? v : BI.i18nText("BI-Others"));
        this.editor.setEnable(BI.isNotNull(v));
    }



});

$.shortcut("bi.conf_number_custom_group_other", BI.ConfNumberIntervalCustomGroupOther);
/**
 * Created by roy on 15/11/2.
 */
BI.CustomGroupFieldButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        var conf = BI.CustomGroupFieldButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            tagName: "a",
            baseCls: (conf.baseCls || "") + " bi-text-button display-block" + "bi-custom-group-field-button",
            textAlign: "center",
            whiteSpace: "nowrap",
            textWidth: null,
            textHeight: null,
            hgap: 0,
            valueRight: "",
            py: ""
        })
    },

    _init: function () {
        BI.CustomGroupFieldButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.textLeft = BI.createWidget({
            type: "bi.label",
            textAlign: o.textAlign,
            whiteSpace: o.whiteSpace,
            textHeight: o.textHeight,
            height: o.height,
            lgap: o.hgap,
            text: o.textLeft,
            value: o.valueLeft,
            py: o.py
        });

        this.textRight = BI.createWidget({
            type: "bi.label",
            textAlign: o.textAlign,
            whiteSpace: o.whiteSpace,
            textHeight: o.textHeight,
            height: o.height,
            rgap: o.hgap,
            text: o.textRight,
            value: o.valueRight
        });

        BI.createWidget({
            type: "bi.left",
            element:this.element,
            items: [
                self.textLeft,
                self.textRight
            ]
        })
    },

    doClick: function(){
        BI.CustomGroupFieldButton.superclass.doClick.apply(this, arguments);
        if(this.isValid()) {
            this.fireEvent(BI.CustomGroupFieldButton.EVENT_CHANGE, this.getValue(), this);
        }
    },

    doRedMark: function(){
        this.textLeft.doRedMark.apply(this.textLeft, arguments);
    },

    unRedMark: function(){
        this.textLeft.unRedMark.apply(this.textLeft, arguments);
    },

    doHighLight: function () {
        this.textLeft.doHighLight.apply(this.textLeft, arguments);
    },

    unHighLight: function () {
        this.textLeft.unHighLight.apply(this.textLeft, arguments);
    },


    setValueLeft: function(text){
        BI.CustomGroupFieldButton.superclass.setValue.apply(this, arguments);
        if(!this.isReadOnly()) {
            text = BI.isArray(text) ? text.join(",") : text;
            this.textLeft.setValue(text);
        }
    },

    setValueRight: function(text){
        BI.CustomGroupFieldButton.superclass.setValue.apply(this, arguments);
        if(!this.isReadOnly()) {
            text = BI.isArray(text) ? text.join(",") : text;
            this.textRight.setValue(text);
        }
    }
});
BI.CustomGroupFieldButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_group_custom_group_field_button",BI.CustomGroupFieldButton);
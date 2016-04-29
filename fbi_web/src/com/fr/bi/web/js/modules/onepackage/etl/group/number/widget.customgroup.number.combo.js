/**
 * @class BI.ConfNumberIntervalCustomGroupCombo
 * @extend BI.Widget
 */
BI.ConfNumberIntervalCustomGroupCombo = BI.inherit(BI.Widget,{

    constants:{
        triggerHeight:30,
        comboWidth:150,
        itemHeight:30,
        textWidth:85
    },

    _defaultConfig:function(){
        return BI.extend(BI.ConfNumberIntervalCustomGroupCombo.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-number-custom-group-combo"
        });
    },

    _init:function(){
        BI.ConfNumberIntervalCustomGroupCombo.superclass._init.apply(this,arguments);

        var self = this;

        this.trigger = BI.createWidget({
            type:"bi.select_text_trigger",
            height:this.constants.triggerHeight,
            items:BICst.NUMBER_INTERVAL_CUSTOM_GROUP
        });

        this.combo = BI.createWidget({
            type:"bi.combo",
            width:this.constants.comboWidth,
            adjustLength:1,
            el:this.trigger,
            popup:{
                el:{
                    type:"bi.button_group",
                    items:BI.createItems(BICst.NUMBER_INTERVAL_CUSTOM_GROUP,{
                        type: "bi.single_select_item",
                        height: this.constants.itemHeight
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });

        this.combo.on(BI.Controller.EVENT_CHANGE,function(type){
            self.setValue(this.getValue());
            self.fireEvent(BI.Controller.EVENT_CHANGE,type,self.getValue()[0],self);
        });

        this.combo.on(BI.Combo.EVENT_CHANGE,function(){
            self.combo.hideView();
            self.fireEvent(BI.ConfNumberIntervalCustomGroupCombo.EVENT_CHANGE);
        });

        this.combo.setValue(BI.ConfNumberIntervalCustomGroupCombo.Type_Auto);

        BI.createWidget({
            type:"bi.htape",
            element:this.element,
            height:this.constants.itemHeight,
            items:[{
                el:{
                    type: "bi.label",
                    cls:"group-combo-label",
                    textAlign: "left",
                    height: this.constants.itemHeight,
                    text: BI.i18nText("BI-Grouping_Style")
                },
                width:this.constants.textWidth
            },this.combo]
        });
    },

    getValue:function(){
        return this.combo.getValue();
    },

    setValue:function(v){
        this.combo.setValue(v);
    }
});

BI.extend(BI.ConfNumberIntervalCustomGroupCombo,{
    Type_Auto: BICst.NUMBER_INTERVAL_CUSTOM_GROUP_AUTO,
    Type_Custom: BICst.NUMBER_INTERVAL_CUSTOM_GROUP_CUSTOM
});

BI.ConfNumberIntervalCustomGroupCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.conf_number_custom_group_combo",BI.ConfNumberIntervalCustomGroupCombo);
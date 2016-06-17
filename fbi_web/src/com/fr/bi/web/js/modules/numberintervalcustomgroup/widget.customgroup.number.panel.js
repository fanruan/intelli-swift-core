/**
 * @class BI.NumberIntervalCustomGroupPanel
 * @extend BI.Widget
 */

BI.NumberIntervalCustomGroupPanel = BI.inherit(BI.Widget,{

    constants:{
        bgap:10,
        addButtonHeight:30,
        iconTextButtonWidth:73
    },

    _defaultConfig:function(){
        return BI.extend(BI.NumberIntervalCustomGroupPanel.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-number-custom-group-panel"
        });
    },

    _init:function(){
        BI.NumberIntervalCustomGroupPanel.superclass._init.apply(this,arguments);

        var self = this;
        this.button = BI.createWidget({
            type:"bi.icon_text_item",
            cls:"group-add-font group-tab-button-text",
            height:this.constants.addButtonHeight,
            width:this.constants.iconTextButtonWidth,
            text:BI.i18nText("BI-Add_Grouping")
        });

        this.button_group = BI.createWidget({
            type: "bi.number_custom_item_group",
            items: []
        });

        this.button.on(BI.IconTextItem.EVENT_CHANGE,function(){
            if(self.button_group.isValid()){
                self.button_group.addItem();
                self.button_group.scrollToBottom();
            }
        });

        this.button_group.on(BI.NumberIntervalCustomItemGroup.EVENT_ERROR,function(){
            self.fireEvent(BI.NumberIntervalCustomGroupPanel.EVENT_ERROR);
        });

        this.button_group.on(BI.NumberIntervalCustomItemGroup.EVENT_EMPTY_GROUP,function(){
            self.fireEvent(BI.NumberIntervalCustomGroupPanel.EVENT_EMPTY_GROUP);
        });

        this.button_group.on(BI.NumberIntervalCustomItemGroup.EVENT_VALID,function(){
            self.fireEvent(BI.NumberIntervalCustomGroupPanel.EVENT_VALID);
        });

        return BI.createWidget({
            type: "bi.panel",
            element:this.element,
            title: BI.i18nText("BI-Custom_Grouping"),
            titleButtons: [this.button],
            el: this.button_group,
            logic:{
                dynamic: true
            }
        });
    },

    isValid: function(){
        return this.button_group.isValid();
    },

    isEmptyPanel: function(){
        return this.button_group.isEmptyGroup();
    },

    populate:function(items){
        this.button_group.populate(items);
    },

    setValue:function(obj){
        this.button_group.setValue(obj);
    },

    getValue:function(){
        return this.button_group.getValue();
    }
});

BI.NumberIntervalCustomGroupPanel.EVENT_ERROR = "EVENT_ERROR";
BI.NumberIntervalCustomGroupPanel.EVENT_EMPTY_GROUP = "EVENT_EMPTY_GROUP";
BI.NumberIntervalCustomGroupPanel.EVENT_VALID = "EVENT_VALID";

$.shortcut("bi.number_custom_group_panel",BI.NumberIntervalCustomGroupPanel);
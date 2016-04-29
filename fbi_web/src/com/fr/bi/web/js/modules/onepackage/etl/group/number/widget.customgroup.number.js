/**
 * @class BI.ConfNumberIntervalCustomGroup
 * @extend BI.Widget
 */

BI.ConfNumberIntervalCustomGroup = BI.inherit(BI.Widget,{

    constants:{
        tabHeight:328,
        bgap:10
    },

    _defaultConfig:function(){
        return BI.extend(BI.ConfNumberIntervalCustomGroup.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-number-custom-group"
        });
    },

    _init:function(){
        BI.ConfNumberIntervalCustomGroup.superclass._init.apply(this,arguments);

        var self = this;

        this.styleCombo = BI.createWidget({
            type:"bi.conf_number_custom_group_combo"
        });


        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el:this.styleCombo,
                bgap:this.constants.bgap
            },this._createTab()]
        });

    },

    _createTab:function(){
        var self = this,o = this.options;
        this.tab = BI.createWidget({
            type:"bi.conf_number_custom_group_tab",
            fieldName: o.fieldName,
            tab:this.styleCombo,
            height:this.constants.tabHeight
        });

        this.tab.on(BI.ConfNumberIntervalCustomGroupTab.EVENT_ERROR,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroup.EVENT_ERROR);
        });

        this.tab.on(BI.ConfNumberIntervalCustomGroupTab.EVENT_EMPTY_GROUP,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroup.EVENT_EMPTY_GROUP);
        });

        this.tab.on(BI.ConfNumberIntervalCustomGroupTab.EVENT_VALID,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroup.EVENT_VALID);
        });

        return this.tab;
    },

    populate:function(config){
        this.tab.populate(config);
    },

    setValue:function(obj){
        this.tab.setValue(obj);
    },

    getValue:function(){
        return this.tab.getValue();
    }
});

BI.ConfNumberIntervalCustomGroup.EVENT_ERROR = "EVENT_ERROR";
BI.ConfNumberIntervalCustomGroup.EVENT_EMPTY_GROUP = "EVENT_EMPTY_GROUP";
BI.ConfNumberIntervalCustomGroup.EVENT_VALID = "EVENT_VALID";

$.shortcut("bi.conf_number_custom_group",BI.ConfNumberIntervalCustomGroup);
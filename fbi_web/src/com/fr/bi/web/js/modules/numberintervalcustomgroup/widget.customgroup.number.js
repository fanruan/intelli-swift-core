/**
 * @class BI.NumberIntervalCustomGroup
 * @extend BI.Widget
 */

BI.NumberIntervalCustomGroup = BI.inherit(BI.Widget,{

    constants:{
        tabHeight:328,
        bgap:10
    },

    _defaultConfig:function(){
        return BI.extend(BI.NumberIntervalCustomGroup.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-number-custom-group"
        });
    },

    _init:function(){
        BI.NumberIntervalCustomGroup.superclass._init.apply(this,arguments);

        var self = this;

        this.styleCombo = BI.createWidget({
            type:"bi.number_custom_group_combo"
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
            type:"bi.number_custom_group_tab",
            dId: o.dId,
            tab:this.styleCombo,
            height:this.constants.tabHeight
        });

        this.tab.on(BI.NumberIntervalCustomGroupTab.EVENT_ERROR,function(){
            self.fireEvent(BI.NumberIntervalCustomGroup.EVENT_ERROR);
        });

        this.tab.on(BI.NumberIntervalCustomGroupTab.EVENT_EMPTY_GROUP,function(){
            self.fireEvent(BI.NumberIntervalCustomGroup.EVENT_EMPTY_GROUP);
        });

        this.tab.on(BI.NumberIntervalCustomGroupTab.EVENT_VALID,function(){
            self.fireEvent(BI.NumberIntervalCustomGroup.EVENT_VALID);
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

BI.NumberIntervalCustomGroup.EVENT_ERROR = "EVENT_ERROR";
BI.NumberIntervalCustomGroup.EVENT_EMPTY_GROUP = "EVENT_EMPTY_GROUP";
BI.NumberIntervalCustomGroup.EVENT_VALID = "EVENT_VALID";

$.shortcut("bi.number_custom_group",BI.NumberIntervalCustomGroup);
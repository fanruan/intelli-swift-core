/**
 * @class BI.CircleTab
 * @extend BI.Widget
 */

BI.CircleTab = BI.inherit(BI.Widget,{

    constants:{
        CIRCLE_GAP_TEN: 10,
        conditionHeight: 30
    },

    _defaultConfig:function(){
        return BI.extend(BI.CircleTab.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-circle-tab"
        });
    },

    _init:function(){
        BI.CircleTab.superclass._init.apply(this,arguments);

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            logic: {
                dynamic: true
            },
            element: this.element,
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.setSelect(BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT);
    },

    _createTabs : function(v){
        var self = this, o = this.options;
        switch (v){
            case BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT:

                this.button = BI.createWidget({
                    type: "bi.circle_select_field_button",
                    height: this.constants.conditionHeight,
                    value: BI.i18nText("BI-Please_Select_Field")
                });

                this.onePopup = BI.createWidget({
                    type: "bi.circle_one_region_popup",
                    cls: "field-popup"
                });
                this.onePopup.on(BI.CircleOneRegionPopup.EVENT_CHANGE, function(){
                    self.button.setValue(self.onePopup.getValue()[0]);
                    self.fireEvent(BI.CircleTab.EVENT_CHANGE);
                });
                this.button.on(BI.CircleSelectFieldButton.EVENT_CHANGE, function(){
                    self.onePopup.setValue(self.button.getValue());
                });
                this.onePopup.populate(o.textItems|| []);

                return BI.createWidget({
                    type: "bi.vertical",
                    vgap: 10,
                    items: [{
                        type: "bi.horizontal",
                        cls: "level-condition",
                        height: this.constants.conditionHeight,
                        rgap: 5,
                        scrollx: false,
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Id_Column_Layer_Base")
                        }, this.button]
                    }, this.onePopup]
                });

            case BI.CircleOperatorPane.CONDITION_TYPE_HAS_PARENT:
                this.twoConditionSwitch = BI.createWidget({
                    type: "bi.circle_two_condition_switch"
                });
                this.twoConditionSwitch.on(BI.CircleTwoConditionSwitch.EVENT_CHANGE, function(){
                    self.fireEvent(BI.CircleTab.EVENT_TWO_VALUE_CHANGE);
                });
                this.twoConditionSwitch.populate({
                    textItems: o.textItems,
                    numberItems: o.numberItems
                });
                return this.twoConditionSwitch;
        }
    },

    _defaultState: function(){
        this.button && this.button.setValue();
        this.onePopup && this.onePopup.setValue();
        this.twoConditionSwitch && this.twoConditionSwitch.setValue();
    },

    setSelect: function(v){
        this.tab.setSelect(v);
        this._defaultState();
    },

    setValue: function(v){
        if(BI.isNull(v)){
            this.setSelect(BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT);
        }
    },

    getValue: function(){
        switch(this.tab.getSelect()){
            case BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT:
                return {
                    id_field_name: this.button.getValue(),
                    parentid_field_name: ""
                };
            case BI.CircleOperatorPane.CONDITION_TYPE_HAS_PARENT:
                var value = this.twoConditionSwitch.getValue();
                return {
                    id_field_name: BI.isEqual(value.conditionValue, BI.i18nText("BI-Please_Select_Field")) ? "" : value.conditionValue,
                    parentid_field_name: BI.isEqual(value.parentValue, BI.i18nText("BI-Please_Select_Field")) ? "" : value.parentValue
                };
        }
    },

    populate:function(items){
        var o = this.options;
        o.textItems = items.textItems || [];
        o.numberItems = items.numberItems || [];

        this.setSelect(BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT);
        this.onePopup.populate(o.textItems);
    }
});
BI.CircleTab.EVENT_TWO_VALUE_CHANGE = "EVENT_TWO_VALUE_CHANGE";
BI.CircleTab.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.circle_tab",BI.CircleTab);
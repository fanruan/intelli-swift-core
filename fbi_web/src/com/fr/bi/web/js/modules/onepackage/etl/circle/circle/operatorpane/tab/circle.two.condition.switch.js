/**
 * 具有两个region的popup
 * @class BI.CircleTwoConditionSwitch
 * @extends BI.Widget
 */

BI.CircleTwoConditionSwitch = BI.inherit(BI.Widget, {

    constants: {
        conditionHeight: 30
    },

    _defaultConfig: function() {
        return BI.extend(BI.CircleTwoConditionSwitch.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-circle-two-condition-switch"
        })
    },

    _init : function() {
        BI.CircleTwoConditionSwitch.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        return BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            vgap: 10,
            items: [{
                type: "bi.left",
                items: [this._createIdConditionSwitcher(), {
                    el:this._createPIdConditionSwitcher(),
                    lgap: 20
                }]
            },this.currentPopup, this.parentPopup]
        });

    },

    _createIdConditionSwitcher: function(){
        var self = this;

        this.conditionButton = BI.createWidget({
            type: "bi.circle_select_field_button",
            height: this.constants.conditionHeight,
            value: BI.i18nText("BI-Please_Select_Field")
        });


        this.currentPopup = BI.createWidget({
            type: "bi.circle_two_region_popup",
            cls: "field-popup"
        });

        this.conditionSwitch = BI.createWidget({
            type: "bi.switcher",
            direction: BI.Direction.Custom,
            toggle: false,
            isDefaultInit: true,
            el: this.conditionButton,
            popup: this.currentPopup
        });

        this.conditionSwitch.on(BI.Switcher.EVENT_CHANGE, function(){
            this.setValue(this.getValue());
            self.fireEvent(BI.CircleTwoConditionSwitch.EVENT_CHANGE);
        });

        this.conditionSwitch.on(BI.Switcher.EVENT_BEFORE_POPUPVIEW, function(){
            self.conditionButton.setSelected(true);
            self.parentSwitch.hideView();
        });

        this.conditionSwitch.on(BI.Switcher.EVENT_HIDEVIEW, function(){
            self.conditionButton.setSelected(false);
        });

        return BI.createWidget({
            type: "bi.horizontal",
            cls: "level-condition",
            height: this.constants.conditionHeight,
            rgap: 5,
            scrollx: false,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Id_Column_Layer_Base")
            }, this.conditionSwitch]
        });
    },

    _createPIdConditionSwitcher: function(){
        var self = this;

        this.parentButton = BI.createWidget({
            type: "bi.circle_select_field_button",
            height: this.constants.conditionHeight,
            value: BI.i18nText("BI-Please_Select_Field")
        });

        this.parentPopup = BI.createWidget({
            type: "bi.circle_two_region_popup",
            cls: "field-popup"
        });

        this.parentSwitch = BI.createWidget({
            type: "bi.switcher",
            direction: BI.Direction.Custom,
            toggle: false,
            isDefaultInit: false,
            el: this.parentButton,
            popup: this.parentPopup
        });

        this.parentSwitch.on(BI.Switcher.EVENT_CHANGE, function () {
            this.setValue(this.getValue());
            self.fireEvent(BI.CircleTwoConditionSwitch.EVENT_CHANGE);
        });

        this.parentSwitch.on(BI.Switcher.EVENT_BEFORE_POPUPVIEW, function() {
            self.parentButton.setSelected(true);
            self.conditionSwitch.hideView();
        });

        this.parentSwitch.on(BI.Switcher.EVENT_HIDEVIEW, function(){
            self.parentButton.setSelected(false);
        });

        this.parentPopup.setVisible(false);

        return BI.createWidget({
            type: "bi.horizontal",
            cls: "level-condition",
            height: this.constants.conditionHeight,
            rgap: 5,
            scrollx: false,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-ParentID_Column_Text")
            }, this.parentSwitch]
        });
    },

    populate: function(items){
        this.currentPopup.populate({
            textItems: items.textItems || [],
            numberItems: items.numberItems || []
        });
        this.parentPopup.populate({
            textItems: items.textItems || [],
            numberItems: items.numberItems || []
        });
    },

    setValue: function(v){
        if(BI.isNull(v)){
            this.conditionSwitch.showView();
            this.parentSwitch.hideView();
            this.conditionSwitch && this.conditionSwitch.setValue();
            this.parentSwitch && this.parentSwitch.setValue();
            return;
        }
        this.conditionSwitch && this.conditionSwitch.setValue(v.conditionValue);
        this.parentSwitch && this.parentSwitch.setValue(v.parentValue);
    },

    getValue: function(){
        return {
            conditionValue: this.conditionSwitch.getValue(),
            parentValue: this.parentSwitch.getValue()
        }
    }
});

BI.CircleTwoConditionSwitch.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.circle_two_condition_switch", BI.CircleTwoConditionSwitch);
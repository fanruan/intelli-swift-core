/**
 * 自循环列主要操作面板
 * @class BI.CircleOperatorPane
 * @extends BI.LoadingPane
 */

BI.CircleOperatorPane = BI.inherit(BI.LoadingPane, {

    constants: {
        CIRCLE_COMBO_LABEL_WIDTH: 140,
        CIRCLE_LABEL_WIDTH: 70,
        itemComboHeight: 30,
        buttonWidth: 90,
        CIRCLE_GAP_TEN: 10,
        CIRCLE_GAP_TWENTY: 20,
        editorDefaultValue: 2,
        CIRCLE_TYPE_NUMBER: 1,
        CIRCLE_TYPE_STRING: 0
    },

    _defaultConfig: function(){
        return BI.extend(BI.CircleOperatorPane.superclass._defaultConfig.apply(this, arguments),  {
            baseCls: "bi-circle-operator-pane"
        });
    },

    _init: function(){
        BI.CircleOperatorPane.superclass._init.apply(this, arguments);
        var self = this;
        this.fetch_union_length = 0;
        this.circleManageCombo = BI.createWidget({
            type: "bi.text_value_combo",
            height: this.constants.itemComboHeight,
            items: [{
                text: BI.i18nText("BI-Layer_Base_One"),
                value: BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT
            },{
                text: BI.i18nText("BI-Layer_Base_Two"),
                value: BI.CircleOperatorPane.CONDITION_TYPE_HAS_PARENT
            }]
        });

        this.circleManageCombo.setValue(BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT);

        this.circleManageCombo.on(BI.StaticCombo.EVENT_CHANGE, function(){
            self._checkValid();
            if(this.getValue()[0] === BI.CircleOperatorPane.CONDITION_TYPE_HAS_PARENT){
                self._setLevelEditorVisible(false);
                self.tab.setSelect(BI.CircleOperatorPane.CONDITION_TYPE_HAS_PARENT);
                return;
            }
            self.tab.setSelect(BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT);
        });

        this.circleShowTextFieldCombo = BI.createWidget({
            type: "bi.circle_show_text_combo"
        });

        this.circleShowTextFieldCombo.on(BI.CircleShowTextCombo.EVENT_CONFIRM, function(){
            self._checkValid();
        });

        this.tipLabel = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Layer_Length"),
            textAlign: "left",
            cls: "operator-label-text",
            width: this.constants.CIRCLE_LABEL_WIDTH
        });

        this.levelLengthEditor = BI.createWidget({
            type: "bi.sign_editor",
            value: this.constants.editorDefaultValue,
            validationChecker: function(v){
                return BI.isPositiveInteger(v);
            },
            cls: "level-length-editor",
            allowBlank: false,
            width: 300
        });

        this.constructRelationButton = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Structure_Relations"),
            width: this.constants.buttonWidth,
            tipType: "warning",
            height: 30
        });

        this.constructRelationButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fetch_union_length = "";
            self.fireEvent(BI.CircleOperatorPane.EVENT_CONSTRUCTRELATIONBUTTON_CLICK)
        });

        this.tab = BI.createWidget({
            type: "bi.circle_tab"
        });

        this.tab.on(BI.CircleTab.EVENT_CHANGE, function(){
            self.loading();
            BI.Utils.getCircleLayerLevelInfo(self.options.table, BI.extend(self.getValue(), {
                divide_length: ""
            }), function(res){
                self._setLevelEditorVisible(res.size);
                self.levelLengthEditor.isVisible() && self.levelLengthEditor.setValue(res.size);
                self.loaded();
                self._checkValid();
            });
        });

        this.tab.on(BI.CircleTab.EVENT_TWO_VALUE_CHANGE, function(){
            self._checkValid();
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            hgap: this.constants.CIRCLE_GAP_TEN,
            items: [{
                type: "bi.vertical",
                items: [{
                    type: "bi.htape",
                    items: [{
                        el: {
                            type: "bi.label",
                            cls: "operator-label-text",
                            textAlign: "left",
                            text: BI.i18nText("BI-Self_Cycle_Column_Super"),
                            height: this.constants.itemComboHeight
                        },
                        width: this.constants.CIRCLE_COMBO_LABEL_WIDTH
                    }, {
                        el: this.circleManageCombo,
                        height: this.constants.itemComboHeight
                    }],
                    height: this.constants.itemComboHeight
                }, {
                    type: "bi.htape",
                    items: [{
                        el: {
                            type: "bi.label",
                            cls: "operator-label-text",
                            textAlign: "left",
                            text: BI.i18nText("BI-Displayed_Value"),
                            height: this.constants.itemComboHeight
                        },
                        width: this.constants.CIRCLE_COMBO_LABEL_WIDTH
                    }, {
                        el: this.circleShowTextFieldCombo,
                        height: this.constants.itemComboHeight
                    }],
                    height: this.constants.itemComboHeight

                }, this.tab],
                vgap: this.constants.CIRCLE_GAP_TEN
            },{
                type: "bi.left_right_vertical_adapt",
                height: this.constants.itemComboHeight,
                items: {
                    left: [this.tipLabel, this.levelLengthEditor],
                    right: [this.constructRelationButton]
                }
            }],
            vgap: 10
        });

        this._setLevelEditorVisible(false);
    },

    _checkValid: function(){
        var showTextField = this.circleShowTextFieldCombo.getValue();
        var conditionValue = this.tab.getValue(), o = this.options;
        var manageValue = this.circleManageCombo.getValue()[0];
        if(BI.isNull(showTextField.type) || (showTextField.type === BI.Selection.Multi && BI.isEmpty(showTextField.value))){
            this.constructRelationButton.setEnable(false);
            this.constructRelationButton.setTitle(BI.i18nText("BI-Please_Select_Show_Text_Fields"));
            return;
        }
        if(BI.isEmpty(conditionValue.id_field_name)){
            this.constructRelationButton.setEnable(false);
            this.constructRelationButton.setTitle(BI.i18nText("BI-Please_Select_Layer_Condition_ID_Column"));
            return;
        }
        if(manageValue === BI.CircleOperatorPane.CONDITION_TYPE_HAS_PARENT && BI.isEmpty(conditionValue.parentid_field_name)){
            this.constructRelationButton.setEnable(false);
            this.constructRelationButton.setTitle(BI.i18nText("BI-Please_Select_Layer_Condition_Parent_Column"));
            return;
        }
        var textValues = BI.pluck(o.textItems, "value");
        var numberValues = BI.pluck(o.numberItems, "value");
        if((BI.contains(textValues, conditionValue.id_field_name) && BI.contains(numberValues, conditionValue.parentid_field_name)) ||
            (BI.contains(textValues, conditionValue.parentid_field_name) && BI.contains(numberValues, conditionValue.id_field_name))){
            this.constructRelationButton.setEnable(false);
            this.constructRelationButton.setTitle(BI.i18nText("BI-Fields_Type_Different_Not_Construct"));
            return;
        }
        if(manageValue === BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT && this.levelLengthEditor.isVisible() === false){
            this.constructRelationButton.setEnable(false);
            this.constructRelationButton.setTitle(BI.i18nText("BI-Cannot_Create_Layer"));
            return;
        }
        this.constructRelationButton.setEnable(true);
        this.constructRelationButton.setTitle("");
    },

    _defaultState: function(){
        this.circleManageCombo.setValue(BI.CircleOperatorPane.CONDITION_TYPE_NOT_HAS_PARENT);
        this.circleShowTextFieldCombo.setValue();
        this.fetch_union_length = 0;
        this.tab.setValue();
    },

    populate: function(items){
        var o = this.options;
        var fields = items.fields;
        o.table = items.table;
        o.textItems = [];
        BI.each(fields, function(idx, item){
            if(item.field_type === BICst.COLUMN.STRING){
                o.textItems.push({
                    value: item.field_name,
                    title: item.field_name
                });
            }
        });
        o.numberItems = [];
        BI.each(fields, function(idx, item){
            if(item.field_type === BICst.COLUMN.NUMBER){
                o.numberItems.push({
                    value: item.field_name,
                    title: item.field_name
                });
            }
        });
        this.circleShowTextFieldCombo.populate(this.options.textItems);
        this.circleShowTextFieldCombo.setValue();
        this.tab.populate({
            textItems : this.options.textItems,
            numberItems: this.options.numberItems
        });
        this._setLevelEditorVisible(false);
        this._checkValid();
    },

    _assertMultiSelectComboValue: function(){
        var value = this.circleShowTextFieldCombo.getValue();
        if(value.type === BI.Selection.Multi){
            return value.value;
        }
        return value.assist;
    },

    getValue: function(){
        return BI.extend(this.tab.getValue(), {
            fetch_union_length: this.fetch_union_length,
            divide_length: this.levelLengthEditor.getValue(),
            showfields: this._assertMultiSelectComboValue()
        });
    },

    setValue: function(v){
        if(BI.isNull(v)){
            this._defaultState();
            return;
        }
        this.tab.setValue(v);
    },

    _setLevelEditorVisible: function(b){
        this.tipLabel.setVisible(!!b);
        this.levelLengthEditor.setVisible(!!b);
    }
});

BI.extend(BI.CircleOperatorPane, {
    CONDITION_TYPE_NOT_HAS_PARENT: 1,
    CONDITION_TYPE_HAS_PARENT: 2
});

BI.CircleOperatorPane.EVENT_CONSTRUCTRELATIONBUTTON_CLICK = "EVENT_CONSTRUCTRELATIONBUTTON_CLICK";
$.shortcut("bi.circle_operator_pane", BI.CircleOperatorPane);
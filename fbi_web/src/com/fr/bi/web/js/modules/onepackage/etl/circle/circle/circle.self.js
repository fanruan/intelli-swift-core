/**
 * 对自循环列三个面板的承接
 * @class BI.CircleSelf
 * @extends BI.Widget
 */

BI.CircleSelf = BI.inherit(BI.Widget, {

    constants: {
        itemComboHeight: 50,
        buttonWidth: 90,
        buttonHeight: 30,
        gap: 10
    },

    _defaultConfig: function(){
        return BI.extend(BI.CircleSelf.superclass._defaultConfig.apply(this, arguments), {
           baseCls: "bi-circle-self"
        });
    },

    _init: function(){
        BI.CircleSelf.superclass._init.apply(this, arguments);

        var self = this;
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.tab.on(BI.Tab.EVENT_CHANGE, function(){
            switch(self.tab.getSelect()){
                case BI.CircleSelf.OPERATOR_PANE:
                    self.fireEvent(BI.CircleSelf.EVENT_SHOW_OPERATOR_PANE);
                    break;
                case BI.CircleSelf.RESULT_PANE:
                    self.fireEvent(BI.CircleSelf.EVENT_SHOW_RESULT_PANE);
                    break;
            }
        });

        this.tab.setSelect(BI.CircleSelf.OPERATOR_PANE);
        this.fireEvent(BI.CircleSelf.EVENT_SHOW_OPERATOR_PANE);
    },

    _createTabs: function(v){
        var self = this, o = this.options;
        switch (v){
            case BI.CircleSelf.OPERATOR_PANE:
                this.operatorPane = BI.createWidget({
                    type: "bi.circle_operator_pane"
                });

                this.operatorPane.on(BI.CircleOperatorPane.EVENT_CONSTRUCTRELATIONBUTTON_CLICK, function(){
                    self.tab.setSelect(BI.CircleSelf.LEVEL_PANE);
                    self.levelLayerPane.populate({
                        table: o.table,
                        layerInfo: self.operatorPane.getValue()
                    });
                });
                return this.operatorPane;
            case BI.CircleSelf.LEVEL_PANE:
                this.levelLayerPane = BI.createWidget({
                    type: "bi.circle_level_pane"
                });
                this.levelLayerPane.on(BI.CircleLevelPane.EVENT_SAVE_CLICK, function(){
                    self.tab.setSelect(BI.CircleSelf.RESULT_PANE);
                    self.fireEvent(BI.CircleSelf.EVENT_SHOW_RESULT_PANE);
                    self.resultPane.populate({
                        floors: self.levelLayerPane.getValue(),
                        divide_length: self.operatorPane.getValue().divide_length,
                        id_field_name: self.operatorPane.getValue().id_field_name,
                        parentid_field_name: self.operatorPane.getValue().parentid_field_name,
                        field_length: self.field_length,
                        showfields: self.operatorPane.getValue().showfields
                    });

                });
                return this.levelLayerPane;
            case BI.CircleSelf.RESULT_PANE:
                this.resultPane = BI.createWidget({
                    type: "bi.circle_result_pane"
                });
                this.resultPane.on(BI.CircleResultPane.BUTTON_CONFIG_CLICK, function(){
                    if(!BI.isNull(o.table.etl_value)){
                        o.table = o.table.tables[0];
                    }
                    self.tab.setSelect(BI.CircleSelf.OPERATOR_PANE);
                    self.fireEvent(BI.CircleSelf.EVENT_SHOW_OPERATOR_PANE);
                    self.operatorPane.populate({fields: self.options.fields, table: self.options.table});
                    self.operatorPane.setValue();
                });
                return this.resultPane;
        }
    },

    populate: function(items){
        var o = this.options;
        o.table = items.table;
        o.fields = items.fields;
        if(this.tab.getSelect() === BI.CircleSelf.RESULT_PANE){
            this.resultPane.populate(o.table.etl_value);
            return;
        }
        this.tab.setSelect(BI.CircleSelf.OPERATOR_PANE);
        this.operatorPane.populate({fields: o.fields, table: o.table});
        this.operatorPane.setValue();
    },

    showOperatorPane: function(){
        this.tab.setSelect(BI.CircleSelf.OPERATOR_PANE);
    },

    showResultPane: function(){
        this.tab.setSelect(BI.CircleSelf.RESULT_PANE);
    },

    getValue: function(){
        return this.resultPane.getValue();
    }
});

BI.extend(BI.CircleSelf, {
    OPERATOR_PANE: 1,
    LEVEL_PANE: 2,
    RESULT_PANE: 3
});

BI.CircleSelf.EVENT_SHOW_RESULT_PANE= "EVENT_SHOW_RESULT_PANE";
BI.CircleSelf.EVENT_SHOW_OPERATOR_PANE = "EVENT_SHOW_OPERATOR_PANE";

$.shortcut("bi.circle_self", BI.CircleSelf);
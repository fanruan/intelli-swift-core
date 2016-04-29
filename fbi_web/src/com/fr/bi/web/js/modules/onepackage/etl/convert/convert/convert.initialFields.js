/**
 * 选择栏次面板
 * @class BI.ConvertInitialFieldsPane
 * @extends BI.Widget
 */
BI.ConvertInitialFieldsPane = BI.inherit(BI.Widget, {

    constants: {
        COMBO_HEIGHT: 25,
        itemHeight: 25
    },

    _defaultConfig: function(){
        return BI.extend(BI.ConvertInitialFieldsPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-convert-initial-fields-pane"
        });
    },

    _init: function(){
        BI.ConvertInitialFieldsPane.superclass._init.apply(this, arguments);

        var self = this;
        this.button_tree = BI.createWidget({
            type:"bi.button_tree",
            chooseType: BI.Selection.Multi,
            layouts: [{
                type: "bi.vertical",
                vgap: 5,
                hgap: 5,
                scrolly: true
            }]
        });

        this.button_tree.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ConvertInitialFieldsPane.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.panel",
            element: this.element,
            title: BI.i18nText("BI-Original_Indicators"),
            el: this.button_tree
        });
    },

    _rebuildItems: function(items){
        items = BI.map(items, function(idx,item){
            return {
                value: item["field_name"]
            }
        });
        return BI.createItems(items, {
            type: "bi.convert_multi_select_item",
            height: this.constants.itemHeight
        });
    },

    getValue: function(){
        var results = [];
        BI.each(this.button_tree.getValue(), function(i, val){
            if(val.selected === true){
                results.push(val.value);
            }
        });
        return results;
    },

    setValue: function(v){
        var values = BI.map(v, function (idx, value) {
            return {
                value: value,
                selected: true
            }
        });
        this.button_tree.setValue(values);
    },

    populate: function(items){
        this.button_tree.populate(this._rebuildItems(items));
    }
});

BI.ConvertInitialFieldsPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.convert_initial_fields_pane", BI.ConvertInitialFieldsPane);
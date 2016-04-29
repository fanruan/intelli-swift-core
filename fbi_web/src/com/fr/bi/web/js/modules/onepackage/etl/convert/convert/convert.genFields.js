/**
 * 生成指标字段的面板
 * @class BI.ConvertGenFieldsPane
 * @extends BI.Widget
 */
BI.ConvertGenFieldsPane = BI.inherit(BI.Widget, {

    constants: {
        COMBO_HEIGHT: 25,
        itemHeight: 25,
        columnsItemsPos: 0,
        lanciPos:1
    },

    _defaultConfig: function(){
        return BI.extend(BI.ConvertGenFieldsPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-convert-gen-fields-pane"
        });
    },

    _init: function(){
        BI.ConvertGenFieldsPane.superclass._init.apply(this, arguments);

        this.button_group = BI.createWidget({
            type:"bi.button_tree",
            chooseType: BI.Selection.Multi,
            layouts: [{
                type: "bi.vertical",
                vgap: 5,
                hgap: 5
            }]
        });

        BI.createWidget({
            type: "bi.panel",
            cls: "gene-fields-panel",
            element: this.element,
            title: BI.i18nText("BI-Generated_Indicators"),
            el: this.button_group
        });
    },

    _rebuildItems: function(items){
        var self = this, result = [];
        if(BI.isEmpty(items[this.constants.columnsItemsPos]) || BI.isEmpty(items[this.constants.lanciPos])){
            return [];
        }
        BI.each(items[self.constants.lanciPos], function(idx, item){
            BI.each(items[self.constants.columnsItemsPos], function(id, it){
                var label = BI.createWidget({
                    type: "bi.convert_display_label",
                    height: self.constants.itemHeight
                });
                label.setValue({
                    lanciValue: item,
                    columnValue: it
                });
                result.push(label);
            });
        });
        return result;
    },

    populate: function(items){
        this.button_group.populate(this._rebuildItems(items));
    }
});

BI.ConvertGenFieldsPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.convert_gen_fields_pane", BI.ConvertGenFieldsPane);
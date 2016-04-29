/**
 * @class BI.SelectSingleTableField
 * @extend BI.Widget
 * 选择表中单个字段(不提供搜索)
 */
BI.SelectSingleTableField = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectSingleTableField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-single-table-field",
            fields: []
        })
    },

    _init: function () {
        BI.SelectSingleTableField.superclass._init.apply(this, arguments);
        var self = this,o = this.options;
        var title = {
            type: "bi.label",
            text: BI.i18nText("BI-Table_Fields"),
            cls: "select-fields-label",
            lgap: 5,
            textAlign: "left",
            textHeight: 30
        };
        this.items_group = BI.createWidget({
            type:"bi.group_select_fields_item_group",
            fields: o.fields
        })

        this.items_group.on(BI.SelectSingleTableField.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectSingleTableField.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items:[{
                el : title,
                height : 30
            }, {
                el : this.items_group
            }]
        });
    },

    populate:function(items){
        this.options.fields = items;
        this.items_group.populate(items);
    },

    getValue:function(){
        return this.items_group.getValue();
    }

});
BI.SelectSingleTableField.EVENT_CHANGE = "EVENT_CHANGE";
BI.SelectSingleTableField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.select_single_table_field", BI.SelectSingleTableField);
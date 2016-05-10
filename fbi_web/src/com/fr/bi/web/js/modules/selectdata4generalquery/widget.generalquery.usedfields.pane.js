/**
 * Created by Young's on 2016/5/9.
 */
BI.GeneralQueryUsedFieldsPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.GeneralQueryUsedFieldsPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-general-query-used-fields-pane"
        })
    },

    _init: function(){
        BI.GeneralQueryUsedFieldsPane.superclass._init.apply(this, arguments);
        var self = this;

        var editor = BI.createWidget({
            type: "bi.search_editor",
            height: 25
        });
        var searcher = BI.createWidget({
            type: "bi.searcher",
            el: editor,
            isAutoSearch: true,
            isAutoSync: true
        });
        BI.createWidget({
            type: "bi.absolute",
            element: searcher,
            items: [{
                el: editor,
                top: 10,
                left: 10,
                right: 10
            }]
        });

        this.usedFields = BI.createWidget({
            type: "bi.button_group",
            items: [],
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.usedFields.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.GeneralQueryUsedFieldsPane.EVENT_CHANGE, arguments);
        });
        searcher.setAdapter(this.usedFields);
        searcher.on(BI.Searcher.EVENT_CHANGE, function(){
            self.fireEvent(BI.GeneralQueryUsedFieldsPane.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: searcher,
                height: 45
            }, {
                el: this.usedFields,
                height: "fill"
            }]
        });
    },

    populate: function(){
        var allFields = BI.Utils.getAllUsedFieldIds();
        var items = [];
        BI.each(allFields, function(i, fId){
            if(BI.Utils.getFieldTypeByID(fId) !== BICst.COLUMN.COUNTER) {
                items.push({
                    type: "bi.general_query_select_data_item",
                    value: fId,
                    fieldType: BI.Utils.getFieldTypeByID(fId),
                    text: BI.Utils.getFieldNameByID(fId),
                    title: BI.Utils.getFieldNameByID(fId)
                })
            }
        });
        this.usedFields.populate(items);
    }
});
BI.GeneralQueryUsedFieldsPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.general_query_used_fields_pane", BI.GeneralQueryUsedFieldsPane);
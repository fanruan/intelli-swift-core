/**
 * @class BI.RelationSetPane
 * @extend BI.BarPopoverSection
 * 设置关联
 */
BI.RelationSetPane = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.RelationSetPane.superclass._defaultConfig.apply(this, arguments, {

        })
    },

    _init: function(){
        BI.RelationSetPane.superclass._init.apply(this, arguments);
    },

    end: function(){
        this.fireEvent(BI.RelationSetPane.EVENT_CHANGE, this.relationPane.getValue());
    },

    rebuildNorth: function(north){
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: this.getFieldNameByFieldId() + BI.i18nText("BI-Link_Infor"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function(center){
        var o = this.options;
        this.relationPane = BI.createWidget({
            type: "bi.relation_pane",
            element: center,
            field_id: o.field_id,
            relations: o.relations,
            translations: o.translations,
            all_fields: o.all_fields
        });
    },

    getFieldNameByFieldId: function(){
        var o = this.options;
        var allFields = o.all_fields, fieldId = o.field_id, translations = o.translations;
        return translations[fieldId] || allFields[fieldId].field_name;
    }
});
BI.RelationSetPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.relation_set_pane", BI.RelationSetPane);
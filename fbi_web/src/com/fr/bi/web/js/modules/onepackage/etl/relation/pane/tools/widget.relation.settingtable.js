/**
 * @class BI.RelationSettingTable
 * @extend BI.Widget
 * 带有设置关联关系（1:N,N:1,1:1）、关联表和删除按钮
 */
BI.RelationSettingTable = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.RelationSettingTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-setting-table"
        })
    },

    _init: function(){
        BI.RelationSettingTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var model = o.model;
        this.setGroup = BI.createWidget({
            type: "bi.relation_set_group",
            relationType: o.relationType
        });
        this.setGroup.on(BI.Controller.EVENT_CHANGE, function(){
            arguments[1] = BI.RelationSettingTable.CLICK_GROUP;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.tableButton = BI.createWidget({
            type: "bi.relation_table_field_button",
            table_name: model.getTableNameByFieldId(o.fieldId),
            field_name: model.getFieldNameByFieldId(o.fieldId),
            field_id: o.fieldId
        });
        this.tableButton.on(BI.Controller.EVENT_CHANGE, function(){
            arguments[1] = BI.RelationSettingTable.CLICK_TABLE;
            arguments[2] = o.fieldId;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        var removeButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font",
            height: 20,
            width: 20
        });
        removeButton.on(BI.Controller.EVENT_CHANGE, function(){
            self.destroy();
            arguments[1] = BI.RelationSettingTable.CLICK_REMOVE;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [this.setGroup, this.tableButton, removeButton],
            hgap: 20,
            vgap: 5
        })
    },

    getValue: function(){
        return {
            relationType: this.setGroup.getValue()[0],
            fieldId: this.options.fieldId
        }
    }
});
BI.extend(BI.RelationSettingTable, {
    CLICK_GROUP: "_relation_setting_table_click_group_",
    CLICK_TABLE: "_relation_setting_table_click_table_",
    CLICK_REMOVE: "_relation_setting_table_click_remove_"
});
$.shortcut("bi.relation_setting_table", BI.RelationSettingTable);
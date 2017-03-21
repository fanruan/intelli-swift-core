/**
 * @class BI.RelationSettingTable
 * @extend BI.Widget
 * 带有设置关联关系（1:N,N:1,1:1）、关联表和删除按钮
 */
BI.RelationSettingTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.RelationSettingTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-setting-table"
        })
    },

    _init: function () {
        BI.RelationSettingTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var edit = o.field.available;
        this.setGroup = BI.createWidget({
            type: "bi.relation_set_group",
            relationType: o.relationType,
            edit: edit
        });
        this.setGroup.on(BI.Controller.EVENT_CHANGE, function () {
            arguments[1] = BI.RelationSettingTable.CLICK_GROUP;
            self.tableButton.setPrimaryKeyIconVisible(!(this.getValue()[0] === BICst.RELATION_TYPE.ONE_TO_N));
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.tableButton = BI.createWidget({
            type: "bi.relation_table_field_button",
            table_name: o.field[BICst.JSON_KEYS.TABLE_TRAN_NAME],
            field_name: self._getFieldNameByField(o.field)
        });
        o.relationType && self.tableButton.setPrimaryKeyIconVisible(!(o.relationType === BICst.RELATION_TYPE.ONE_TO_N));
        edit && this.tableButton.on(BI.Controller.EVENT_CHANGE, function () {
            arguments[1] = BI.RelationSettingTable.CLICK_TABLE;
            arguments[2] = o.fieldId;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        var container = BI.createWidget({
            type: "bi.vertical_adapt",
            element: this.element,
            hgap: 20,
            vgap: 5
        });
        container.addItems([this.setGroup, this.tableButton]);

        if (edit) {
            var removeButton = BI.createWidget({
                type: "bi.icon_button",
                cls: "close-h-font",
                invisible: true,
                height: 20,
                width: 20
            });
            removeButton.on(BI.Controller.EVENT_CHANGE, function () {
                self.destroy();
                arguments[1] = BI.RelationSettingTable.CLICK_REMOVE;
                self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            });
            container.addItem(removeButton);

            this.element.hover(function () {
                removeButton.setVisible(true);
            }, function () {
                removeButton.setVisible(false);
            });
        }

    },

    _getFieldNameByField: function (field) {
        var tranName = field[BICst.JSON_KEYS.FIELD_TRAN_NAME];
        var fieldName = field.field_name;
        return BI.isNotNull(tranName) ? (tranName + "(" + fieldName + ")") : fieldName;
    },

    getValue: function () {
        return {
            relationType: this.setGroup.getValue()[0],
            field: this.options.field
        }
    }
});
BI.extend(BI.RelationSettingTable, {
    CLICK_GROUP: "_relation_setting_table_click_group_",
    CLICK_TABLE: "_relation_setting_table_click_table_",
    CLICK_REMOVE: "_relation_setting_table_click_remove_"
});
$.shortcut("bi.relation_setting_table", BI.RelationSettingTable);
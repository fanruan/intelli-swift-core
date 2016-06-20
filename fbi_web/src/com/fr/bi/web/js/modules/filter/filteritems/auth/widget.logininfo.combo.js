/**
 * Created by Young's on 2016/5/19.
 */
BI.LoginInfoCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.LoginInfoCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-login-info-combo"
        })
    },

    _init: function(){
        BI.LoginInfoCombo.superclass._init.apply(this, arguments);
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.single_tree_combo",
            element: this.element
        });
        this._beforeLoginComboPopup();
        this.combo.on(BI.SingleTreeCombo.EVENT_BEFORE_POPUPVIEW, function(){
            self._beforeLoginComboPopup();
        });
        this.combo.on(BI.SingleTreeCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.LoginInfoCombo.EVENT_CHANGE);
        });
    },

    _beforeLoginComboPopup: function(){
        var self = this, o = this.options;
        var loginField = BI.Utils.getAuthorityLoginField();
        var fieldType = o.field_type;
        var items = [];
        if(BI.isNotNull(loginField)) {
            var primaryFields = this._getPrimaryFieldsByFieldId(loginField);
            primaryFields.splice(0, 0, loginField);
            var comboValue = this.combo.getValue();
            BI.each(primaryFields, function(i, fieldId){
                var tableName = BI.Utils.getTableNameByFieldId4Conf(fieldId);
                var tableId = BI.Utils.getTableIdByFieldId4Conf(fieldId);
                var fields = self._getAllFieldsByTableId(tableId);

                var parentOpen = false;
                BI.each(fields, function(j, field){
                    if(field.field_type === fieldType) {
                        items.push({
                            id: field.id,
                            pId: tableId,
                            text: field.field_name,
                            value: field.id,
                            selected: comboValue.contains(field.id)
                        });
                        comboValue.contains(field.id) && (parentOpen = true);
                    }
                });
                items.push({
                    id: tableId,
                    isParent: true,
                    text: tableName,
                    open: parentOpen
                });
            });
            this.combo.populate(items);
            return;
        }

        switch (fieldType) {
            case BICst.COLUMN.STRING:
                items.push({
                    text: BI.i18nText("BI-System_Username"),
                    id: -1,
                    value: BI.LoginInfoCombo.SYSTEM_USER
                });
                break;
            case BICst.COLUMN.NUMBER:
                items.push({
                    text: BI.i18nText("BI-Login_Info_Not_Set"),
                    id: -1,
                    disabled: true
                });
                break;
        }
        this.combo.populate(items);
    },

    _getAllFieldsByTableId: function(tableId) {
        var fields = [], allFields = Data.SharingPool.get("fields");
        BI.each(allFields, function(i, field){
            if(field.table_id === tableId) {
                fields.push(field);
            }
        });
        return fields;
    },

    _getPrimaryFieldsByFieldId: function(fieldId){
        var self = this;
        var relations = Data.SharingPool.get("relations");
        var translations = Data.SharingPool.get("translations");
        var primaryFields = [];
        var connectionSet = relations.connectionSet;
        BI.each(connectionSet, function(i, cs){
            if(cs.foreignKey.field_id === fieldId) {
                primaryFields.push(cs.primaryKey.field_id);
                primaryFields = primaryFields.concat(self._getPrimaryFieldsByFieldId(cs.primaryKey.field_id));
            }
        });
        return primaryFields;
    },

    setValue: function(v){
        this.combo.setValue(v);
    },
    
    getValue: function(){
        return this.combo.getValue()[0];
    }
});
BI.extend(BI.LoginInfoCombo, {
    SYSTEM_USER: "__system_user__"
});
BI.LoginInfoCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.login_info_combo", BI.LoginInfoCombo);
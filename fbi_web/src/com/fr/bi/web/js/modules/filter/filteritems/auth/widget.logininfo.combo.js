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
        this.combo.on(BI.SingleTreeCombo.EVENT_BEFORE_POPUPVIEW, function(){
            self._beforeLoginComboPopup();
        });
        this.combo.on(BI.SingleTreeCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.LoginInfoCombo.EVENT_CHANGE);
        });
    },

    _beforeLoginComboPopup: function(){
        var self = this, o = this.options;
        var loginInfo = BI.Utils.getAuthorityLoginInfo();
        var fieldType = o.field_type;
        if(BI.isNotNull(loginInfo) && BI.isNotNull(loginInfo.field_name)) {
            var table = loginInfo.table;
            BI.Utils.getPrimaryTablesByTable4Conf(table, function(tables){
                var items = [];
                tables = tables || [];
                tables.push(table);
                BI.each(tables, function(i, t){
                    items.push({
                        text: t.table_name,
                        id: t.md5
                    });
                    BI.each(t.fields, function(j, fs){
                        BI.each(fs, function(k, field){
                            if(field.field_type === fieldType) {
                                items.push({
                                    text: field.field_name,
                                    value: {
                                        table: t,
                                        field_name: field.field_name
                                    },
                                    id: field.id,
                                    pId: t.md5
                                })
                            }
                        });
                    })
                });
                self.combo.populate(items);
            });
            return;
        }
        var items = [];
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
    
    getValue: function(){
        return this.combo.getValue();
    }
});
BI.extend(BI.LoginInfoCombo, {
    SYSTEM_USER: "__system_user__"
});
BI.LoginInfoCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.login_info_combo", BI.LoginInfoCombo);
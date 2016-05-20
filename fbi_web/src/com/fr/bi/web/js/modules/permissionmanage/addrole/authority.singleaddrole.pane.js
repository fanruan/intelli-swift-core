/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthoritySingleAddRolePane = BI.inherit(BI.Widget, {

    _constants: {
        SHOW_EMPTY: 1,
        SHOW_PANE: 2,
        SET_FILTER: 3,
        REMOVE_ROLE: 4,
        AUTHORITY_FILTER: "__authority_filter__"
    },

    _defaultConfig: function(){
        return BI.extend(BI.AuthoritySingleAddRolePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-single-add-role-pane"
        })
    },

    _init: function(){
        BI.AuthoritySingleAddRolePane.superclass._init.apply(this, arguments);
        var self = this;
        var addRoleButton = BI.createWidget({
            type: "bi.button",
            text: "+" + BI.i18nText("Add_Role"),
            height: 30
        });
        addRoleButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.AuthoritySingleAddRolePane.EVENT_ADD_ROLE);
        });

        this.rolesTab = BI.createWidget({
            type: "bi.tab",
            tab: "",
            direction: "custom",
            cardCreator: BI.bind(this._createRolesTab, this)
        });
        this.rolesTab.setSelect(this._constants.SHOW_EMPTY);

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    items: [addRoleButton],
                    vgap: 10
                },
                height: 50
            }, {
                el: this.rolesTab,
                height: "fill"
            }]
        })
    },

    _createRolesTab: function(v) {
        switch (v) {
            case this._constants.SHOW_EMPTY:
                return BI.createWidget({
                    type: "bi.center_adapt",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Click_Add_Button_Package"),
                        height: 30,
                        cls: "empty-tip"
                    }]
                });
            case this._constants.SHOW_PANE:
                this.roles = BI.createWidget({
                    type: "bi.button_group",
                    items: [],
                    layouts: [{
                        type: "bi.left",
                        rgap: 10,
                        vgap: 5
                    }]
                });
                return this.roles;
        }
    },

    _getRoleNameByRoleIdType: function(id, type){
        var roles = Data.SharingPool.get("authority_settings", "all_roles");
        var name = "";
        BI.some(roles, function(i, role){
            if(role.id === id && role.role_type === type) {
                name = role.text || (role.department_name + ", " + role.post_name);
                return true;
            }
        });
        return name;
    },
    
    populate: function(v){
        var self = this;
        if(BI.isNotNull(v)) {
            this.rolesTab.setSelect(this._constants.SHOW_PANE);
            var roles = BI.Utils.getPackageAuthorityByID(v);
            var items = [];
            BI.each(roles, function(i, role) {
                var filter = role.filter;
                var trigger = BI.createWidget({
                    type: "bi.text_button",
                    cls: "role-item",
                    text: self._getRoleNameByRoleIdType(role.role_id, role.role_type),
                    height: 30,
                    hgap: 5
                });
                if(BI.isNotNull(filter)) {
                    trigger = BI.createWidget({
                        type: "bi.text_icon_item",
                        cls: "role-item filter-font",
                        logic: {
                            dynamic: true
                        },
                        text: self._getRoleNameByRoleIdType(role.role_id, role.role_type),
                        height: 30,
                        textHgap: 5
                    });
                }
                var combo = BI.createWidget({
                    type: "bi.down_list_combo",
                    el: trigger,
                    height: 30,
                    adjustLength: 2,
                    items: [
                        [{
                            text: BI.i18nText("BI-Filter_Setting"),
                            value: self._constants.SET_FILTER,
                            cls: "filter-font"
                        }],
                        [{
                            text: BI.i18nText("BI-Remove_Roles"),
                            value: self._constants.REMOVE_ROLE,
                            cls: "delete-field-font"
                        }]
                    ]
                });
                combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
                    self._operatorRole(v, role);
                });
                items.push(combo);
            });
            this.roles.populate(items);
        }
    },
    
    _operatorRole: function(operator, role) {
        var self = this;
        switch (operator) {
            case this._constants.SET_FILTER:
                BI.Popovers.remove(this._constants.AUTHORITY_FILTER);
                var popup = BI.createWidget({
                    type: "bi.authority_filter_popup",
                    name: this._getRoleNameByRoleIdType(role.role_id, role.role_type)
                });
                popup.on(BI.AuthorityFilterPopup.EVENT_CHANGE, function (v) {
                    role.filter = v;
                    self.fireEvent(BI.AuthoritySingleAddRolePane.EVENT_FILTER_CHANGE, role);
                });
                BI.Popovers.create(this._constants.AUTHORITY_FILTER, popup).open(this._constants.AUTHORITY_FILTER);
                BI.isNotNull(role.filter) && popup.populate([role.filter]);
                break;
            case this._constants.REMOVE_ROLE:
                this.fireEvent(BI.AuthoritySingleAddRolePane.EVENT_REMOVE_ROLE, role);
                break;
        }
    }
});
BI.AuthoritySingleAddRolePane.EVENT_ADD_ROLE = "EVENT_ADD_ROLE";
BI.AuthoritySingleAddRolePane.EVENT_REMOVE_ROLE = "EVENT_REMOVE_ROLE";
BI.AuthoritySingleAddRolePane.EVENT_FILTER_CHANGE = "EVENT_FILTER_CHANGE";
$.shortcut("bi.authority_single_add_role_pane", BI.AuthoritySingleAddRolePane);
/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthorityBatchAddRolePane = BI.inherit(BI.Widget, {

    _constants: {
        SET_FILTER: 1,
        REMOVE_ROLE: 2,
        AUTHORITY_FILTER: "__authority_filter__"
    },

    _defaultConfig: function () {
        return BI.extend(BI.AuthorityBatchAddRolePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-batch-add-role-pane"
        })
    },

    _init: function () {
        BI.AuthorityBatchAddRolePane.superclass._init.apply(this, arguments);
        var self = this;
        var addRoleButton = BI.createWidget({
            type: "bi.button",
            text: "+" + BI.i18nText("Add_Role"),
            height: 30
        });
        addRoleButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.AuthorityBatchAddRolePane.EVENT_ADD_ROLE);
        });

        this.rolesGroup = BI.createWidget({
            type: "bi.button_group",
            items: [],
            layouts: [{
                type: "bi.left",
                rgap: 10,
                vgap: 5
            }]
        });

        this.selectedTree = BI.createWidget({
            type: "bi.display_tree",
            cls: "selected-packages"
        });

        this.roles = [];

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: this.selectedTree,
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 19
                    }]
                },
                width: 220
            }, {
                el: {
                    type: "bi.vtape",
                    items: [{
                        el: {
                            type: "bi.left",
                            items: [addRoleButton],
                            vgap: 10
                        },
                        height: 50
                    }, {
                        el: this.rolesGroup,
                        height: "fill"
                    }, {
                        el: {
                            type: "bi.left_right_vertical_adapt",
                            items: {
                                left: [{
                                    type: "bi.button",
                                    level: "ignore",
                                    text: BI.i18nText("BI-Basic_Cancel"),
                                    height: 30,
                                    handler: function () {
                                        self.fireEvent(BI.AuthorityBatchAddRolePane.EVENT_CANCEL);
                                    }
                                }],
                                right: [{
                                    type: "bi.button",
                                    text: BI.i18nText("BI-Base_Sure"),
                                    height: 30,
                                    handler: function () {
                                        self.fireEvent(BI.AuthorityBatchAddRolePane.EVENT_SAVE);
                                    }
                                }]
                            }
                        },
                        height: 40
                    }]
                },
                width: "fill"
            }]
        });
    },

    _operatorRole: function (operator, role) {
        var self = this;
        switch (operator) {
            case this._constants.SET_FILTER:
                BI.Popovers.remove(this._constants.AUTHORITY_FILTER);
                var popup = BI.createWidget({
                    type: "bi.authority_filter_popup",
                    name: role.role_id
                });
                popup.on(BI.AuthorityFilterPopup.EVENT_CHANGE, function (v) {
                    BI.some(self.roles, function (i, roleOb) {
                        if (role.role_id === roleOb.role_id && role.role_type === roleOb.role_type) {
                            roleOb.filter = v;
                            return true;
                        }
                    });
                    self._refreshRolesGroup();
                });
                BI.Popovers.create(this._constants.AUTHORITY_FILTER, popup).open(this._constants.AUTHORITY_FILTER);
                BI.isNotNull(role.filter) && popup.populate([role.filter]);
                break;
            case this._constants.REMOVE_ROLE:
                BI.remove(this.roles, role);
                self._refreshRolesGroup();
                break;
        }
    },

    populatePackageTree: function (packages) {
        var treeItems = [];
        var allGroupIds = BI.Utils.getPackageGroupIDs4Conf();
        var pgMap = {}, gIds = [];
        BI.each(allGroupIds, function (i, gId) {
            var pids = BI.Utils.getGroupChildrenByGroupId4Conf(gId);
            BI.each(pids, function (i, pack) {
                if (packages.contains(pack.id)) {
                    var gs = pgMap[pack.id] || [];
                    pgMap[pack.id] = BI.uniq(gs.concat([gId]));
                    !gIds.contains(gId) && gIds.push(gId);
                }
            });
        });

        BI.each(gIds, function (i, gId) {
            treeItems.push({
                id: gId,
                text: BI.Utils.getGroupNameById4Conf(gId),
                open: true
            });
        });

        var hasUngroup = false;
        BI.each(BI.uniq(packages), function (i, pId) {
            var gIds = pgMap[pId];
            if (BI.isNull(pgMap[pId])) {
                hasUngroup = true;
                gIds = [-1];
            }
            BI.each(gIds, function (i, gid) {
                treeItems.push({
                    id: pId,
                    text: BI.Utils.getPackageNameByID4Conf(pId),
                    pId: gid
                });
            })
        });
        if (hasUngroup === true) {
            treeItems.push({
                id: -1,
                text: BI.i18nText("BI-Ungrouped"),
                open: true
            });
        }
        this.selectedTree.initTree(treeItems);
    },

    _refreshRolesGroup: function () {
        var self = this;
        var items = [];
        BI.each(this.roles, function (i, role) {
            var roleName = role.role_id;
            if (BI.isNull(roleName) || roleName === "") {
                return;
            }
            var filter = role.filter;
            var trigger = BI.createWidget({
                type: "bi.text_button",
                cls: "role-item",
                text: roleName,
                height: 30,
                hgap: 5
            });
            if (BI.isNotNull(filter)) {
                trigger = BI.createWidget({
                    type: "bi.horizontal",
                    cls: "role-item",
                    items: [{
                        type: "bi.text_button",
                        text: roleName,
                        height: 30,
                        textHgap: 5
                    }, {
                        type: "bi.icon_button",
                        cls: "filter-font",
                        handler: function () {
                            self._operatorRole(self._constants.SET_FILTER, role);
                        },
                        stopPropagation: true,
                        height: 30
                    }],
                    hgap: 5
                })
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
            combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
                self._operatorRole(v, role);
            });
            items.push(combo);
        });
        this.rolesGroup.populate(items);
    },

    clearRoles: function () {
        this.roles = [];
    },

    addRoles: function (roles) {
        this.roles = this.roles.concat(roles);
        this._refreshRolesGroup();
    },

    getRoles: function () {
        return this.roles;
    }
});
BI.AuthorityBatchAddRolePane.EVENT_ADD_ROLE = "EVENT_ADD_ROLE";
BI.AuthorityBatchAddRolePane.EVENT_SAVE = "EVENT_SAVE";
BI.AuthorityBatchAddRolePane.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.authority_batch_add_role_pane", BI.AuthorityBatchAddRolePane);
/**
 * Created by Young's on 2016/5/17.
 */
BI.SingleAddRoleSearcher = BI.inherit(BI.Widget, {

    _constant: {
        SHOW_TIP: 1,
        SHOW_ROLE: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.SingleAddRoleSearcher.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-single-add-role-searcher"
        })
    },

    _init: function () {
        BI.SingleAddRoleSearcher.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.tab = BI.createWidget({
            type: "bi.tab",
            defaultShowIndex: this._constant.SHOW_ROLE,
            cardCreator: function (v) {
                switch (v) {
                    case self._constant.SHOW_ROLE:
                        self.roles = BI.createWidget({
                            type: "bi.button_group",
                            items: [],
                            chooseType: BI.Selection.Multi,
                            layouts: [{
                                type: "bi.left",
                                scrollable: true,
                                rgap: 10,
                                vgap: 5
                            }]
                        });
                        self.roles.on(BI.ButtonGroup.EVENT_CHANGE, function () {
                            self.saveButton.setText(BI.i18nText("BI-Sen_Confirm_Use_Selected_1", this.getValue().length));
                        });
                        return self.roles;
                    case self._constant.SHOW_TIP:
                        self.tip = BI.createWidget({
                            type: "bi.label",
                            cls: "tip-comment",
                            height: 50
                        });
                        return self.tip;
                }
            }
        });

        this.searcher = BI.createWidget({
            type: "bi.searcher",
            el: {
                type: "bi.search_editor",
                width: 260,
                height: 30
            },
            chooseType: BI.Selection.Multi,
            isAutoSearch: true,
            isAutoSync: true,
            popup: {
                type: "bi.searcher_view",
                searcher: {
                    type: "bi.button_group",
                    chooseType: BI.Selection.Multi,
                    behaviors: {
                        redmark: function () {
                            return true;
                        }
                    },
                    items: [],
                    layouts: [{
                        type: "bi.left",
                        rgap: 10,
                        vgap: 5
                    }]
                },
                matcher: {
                    type: "bi.button_group",
                    chooseType: BI.Selection.Multi,
                    behaviors: {
                        redmark: function () {
                            return true;
                        }
                    },
                    items: [],
                    layouts: [{
                        type: "bi.left",
                        rgap: 10,
                        vgap: 5
                    }]
                }
            }
        });
        this.searcher.setAdapter(this.tab);
        this.searcher.on(BI.Searcher.EVENT_CHANGE, function () {
            self.saveButton.setText(BI.i18nText("BI-Sen_Confirm_Use_Selected_1", this.getValue().length));
        });

        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Basic_Cancel"),
            height: 30
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.SingleAddRoleSearcher.EVENT_CANCEL);
        });

        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sen_Confirm_Use_Selected_1", 0),
            height: 30
        });
        this.saveButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.SingleAddRoleSearcher.EVENT_SAVE, self.roles.getValue());
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    items: [this.searcher],
                    tgap: 10
                },
                height: 50
            }, {
                el: this.tab,
                height: "fill"
            }, {
                el: {
                    type: "bi.left_right_vertical_adapt",
                    items: {
                        left: [cancelButton],
                        right: [this.saveButton]
                    },
                    vgap: 10

                },
                height: 50
            }]
        });
    },

    populate: function (packageId) {
        var allRoles = BI.Utils.getAuthorityRoles();
        var sortedRoles = BI.sortBy(allRoles, function (index, item) {
            return item.departmentid;
        });
        var setRoles = BI.Utils.getPackageAuthorityByID(packageId);
        if (BI.isNotNull(setRoles) && sortedRoles.length === setRoles.length) {
            this.tab.setSelect(this._constant.SHOW_TIP);
            this.tip.setText(BI.i18nText("BI-All_Roles_Set_To_Package"));
            return;
        }
        this.tab.setSelect(this._constant.SHOW_ROLE);
        var items = [];
        BI.each(sortedRoles, function (i, role) {
            var roleName = role.text || (role.departmentname + "," + role.postname);
            var found = BI.some(setRoles, function (j, r) {
                if (roleName === r.role_id && r.role_type === role.role_type) {
                    return true;
                }
            });
            if (found === true) {
                return;
            }
            items.push({
                type: "bi.text_button",
                cls: "role-item",
                text: roleName,
                value: {
                    role_id: roleName,   //id会变？用名字了
                    role_type: role.role_type
                },
                height: 30,
                hgap: 5
            });
        });
        this.roles.populate(items);
        this.searcher.stopSearch();
        this.saveButton.setText(BI.i18nText("BI-Sen_Confirm_Use_Selected_1", this.roles.getValue().length));
    }
});
BI.SingleAddRoleSearcher.EVENT_CANCEL = "EVENT_CANCEL";
BI.SingleAddRoleSearcher.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.single_add_role_searcher", BI.SingleAddRoleSearcher);
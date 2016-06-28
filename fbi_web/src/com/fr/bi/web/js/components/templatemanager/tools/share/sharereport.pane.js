/**
 * create by young
 * 分享
 */
BI.ShareReportPane = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.ShareReportPane.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.ShareReportPane.superclass._init.apply(this, arguments);

    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Template_Share_To"),
            height: 50,
            lgap: 10,
            textAlign: "left"
        })
    },

    rebuildCenter: function (center) {
        var self = this;
        var oldValue = [], oldSearchValue = [];
        this.searchResultPane = BI.createWidget({
            type: "bi.user_search_result_pane"
        });
        var searcher = BI.createWidget({
            type: "bi.searcher",
            width: 270,
            onSearch: function (op, callback) {
                var keyword = op.keyword.toLowerCase();
                var searchResult = BI.Func.getSearchResult(self.allUsers, keyword, "user_name");
                var items = self._formatUserRole(searchResult.matched.concat(searchResult.finded), true);
                var suIds = self.getValue();
                var seaUserIds = [];
                BI.each(self.allSearchUserIds, function (i, asu) {
                    if (suIds.contains(asu.slice(BI.UUID().length))) {
                        seaUserIds.push(asu);
                    }
                });
                oldSearchValue = seaUserIds;
                callback(items, keyword, seaUserIds);
            },
            isAutoSearch: false,
            isAutoSync: false,
            popup: this.searchResultPane
        });
        this.searchResultPane.on(BI.UserSearchResultPane.EVENT_CHANGE, function () {
            var isRemove = false;
            //先同步一下自己
            var vs = this.getValue();
            var values = [], ids = [], oIds = [];
            BI.each(vs, function (i, v) {
                ids.push(v.slice(BI.UUID().length));
            });
            BI.each(oldSearchValue, function (i, v) {
                oIds.push(v.slice(BI.UUID().length));
            });
            if (ids.length < oIds.length) {
                BI.each(ids, function (i, v) {
                    oIds.splice(oIds.indexOf(v), 1);
                });
                ids = BI.uniq(ids);
                BI.each(oIds, function (i, oId) {
                    ids.indexOf(oId) > -1 && ids.splice(ids.indexOf(oId), 1);
                });
                isRemove = true;
            }
            BI.each(self.allSearchUserIds, function (i, au) {
                ids.contains(au.slice(BI.UUID().length)) && values.push(au);
            });

            this.setValue(values);

            //搜索结果面板的选中项变化的时候  要与原面板的选中项做对比 结果集再set回原始面板
            var value = self.getValue();
            if (isRemove) {
                BI.each(oIds, function (i, id) {
                    if (!ids.contains(id)) {
                        BI.remove(value, id);
                    }
                });
            } else {
                BI.each(ids, function (i, id) {
                    if (!oIds.contains(id)) {
                        value.push(id);
                    }
                });
            }
            oldValue = [];
            BI.each(self.allUserIds, function (i, auId) {
                if (value.contains(auId.slice(BI.UUID().length))) {
                    oldValue.push(auId);
                }
            });
            self.allUsersTree.setValue(oldValue);
            oldSearchValue = values;
            self._refreshSelectedUserList();
        });

        this.allUsersTree = BI.createWidget({
            type: "bi.simple_tree",
            cls: "all-users-list"
        });
        BI.requestAsync("fr_bi", "get_all_user_info", {}, function (res) {
            self.allUsers = res;
            self.allUsersTree.populate(self._formatUserRole(res));
        });
        this.allUsersTree.on(BI.SimpleTreeView.EVENT_CHANGE, function () {
            var vs = this.getValue();
            var values = [], ids = [], oIds = [];
            BI.each(vs, function (i, v) {
                ids.push(v.slice(BI.UUID().length));
            });
            BI.each(oldValue, function (i, v) {
                oIds.push(v.slice(BI.UUID().length));
            });
            if (ids.length < oIds.length) {
                BI.each(ids, function (i, v) {
                    oIds.splice(oIds.indexOf(v), 1);
                });
                ids = BI.uniq(ids);
                BI.each(oIds, function (i, oId) {
                    ids.indexOf(oId) > -1 && ids.splice(ids.indexOf(oId), 1);
                });
            }
            BI.each(self.allUserIds, function (i, au) {
                ids.contains(au.slice(BI.UUID().length)) && values.push(au);
            });

            this.setValue(values);
            oldValue = this.getValue();
            self._refreshSelectedUserList();
        });
        searcher.setAdapter(this.allUsersTree);

        this.tab = BI.createWidget({
            type: "bi.tab",
            cls: "selected-users-list",
            defaultShowIndex: BI.ShareReportPane.USER_EMPTY,
            cardCreator: function (v) {
                return self._createSelectedUsers(v);
            }
        });
        BI.createWidget({
            type: "bi.grid",
            element: center,
            cls: "bi-share-report",
            columns: 2,
            rows: 1,
            items: [{
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: searcher,
                        top: 5,
                        left: 10,
                        right: 10
                    }, {
                        el: this.allUsersTree,
                        top: 45,
                        left: 10,
                        right: 10,
                        bottom: 0
                    }]
                },
                column: 0,
                row: 0
            }, {
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: this.tab,
                        top: 5,
                        left: 10,
                        right: 10,
                        bottom: 0
                    }]
                },
                column: 1,
                row: 0
            }]
        });
    },

    _createSelectedUsers: function (v) {
        var self = this;
        switch (v) {
            case BI.ShareReportPane.USER_EMPTY:
                return BI.createWidget({
                    type: "bi.center_adapt",
                    items: [{
                        type: "bi.label",
                        cls: "empty-tip",
                        text: BI.i18nText("BI-Share_Template_Role_Box_Default_Text")
                    }]
                });
            case BI.ShareReportPane.USER_EXIST:
                this.selectedUserList = BI.createWidget({
                    type: "bi.selected_user_group_list"
                });
                this.selectedUserList.on(BI.Controller.EVENT_CHANGE, function () {
                    var userId = arguments[1];
                    var sUserIds = self.getValue();
                    var suIds = [];
                    BI.remove(sUserIds, userId);
                    BI.each(self.allUserIds, function (i, au) {
                        if (sUserIds.contains(au.slice(BI.UUID().length))) {
                            suIds.push(au);
                        }
                    });

                    self.allUsersTree.setValue(suIds);
                    self.searchResultPane.setValue(suIds);
                    self._refreshSelectedUserList();
                });
                return this.selectedUserList;

        }
    },

    //将用户和角色分开
    _formatUserRole: function (users, isSearch) {
        var self = this, roles = [];
        var treeItems = [];
        var departPosts = [];
        this.roleUserMap = {};
        isSearch ? (this.allSearchUserIds = []) : (this.allUserIds = []);
        BI.each(users, function (i, user) {
            var department = user.user_department;
            if (BI.isNotNull(department) && department !== "") {
                var userId = BI.UUID() + user.user_id;
                isSearch ? (self.allSearchUserIds.push(userId)) : (self.allUserIds.push(userId));
                treeItems.push({
                    id: userId,
                    text: user.user_name,
                    value: userId,
                    pId: department
                });
                if (!departPosts.contains(department)) {
                    treeItems.push({
                        id: department,
                        text: department,
                        value: department,
                        isParent: true,
                        open: isSearch === true
                    });
                }
                departPosts.push(department);
                if (BI.isNull(self.roleUserMap[department])) {
                    self.roleUserMap[department] = [user];
                } else {
                    self.roleUserMap[department].push(user);
                }
            }

            if (BI.isNotNull(user.roles) && user.roles.length > 0) {
                BI.each(user.roles, function (j, role) {
                    if (!roles.contains(role)) {
                        roles.push(role);
                        treeItems.push({
                            id: role,
                            text: role,
                            value: role,
                            isParent: true,
                            open: isSearch === true
                        })
                    }
                    if (BI.isNull(self.roleUserMap[role])) {
                        self.roleUserMap[role] = [user];
                    } else {
                        self.roleUserMap[role].push(user);
                    }
                    var userId = BI.UUID() + user.user_id;
                    isSearch ? (self.allSearchUserIds.push(userId)) : (self.allUserIds.push(userId));
                    treeItems.push({
                        id: userId,
                        pId: role,
                        text: user.user_name,
                        value: userId
                    });
                })
            }

            //无角色 无职位的用户 放到其他
            if ((BI.isNull(department) || department === "") &&
                (BI.isNull(user.roles) || user.roles.length === 0)) {
                var userId = BI.UUID() + user.user_id;
                isSearch ? (self.allSearchUserIds.push(userId)) : (self.allUserIds.push(userId));
                treeItems.push({
                    id: userId,
                    pId: BI.ShareReportPane.USER_NO_ROLE,
                    text: user.user_name,
                    value: userId
                });
                if (BI.isNull(self.roleUserMap[BI.ShareReportPane.USER_NO_ROLE])) {
                    treeItems.push({
                        id: BI.ShareReportPane.USER_NO_ROLE,
                        text: BI.i18nText("BI-User_Without_Role"),
                        value: BI.ShareReportPane.USER_NO_ROLE
                    });
                    self.roleUserMap[BI.ShareReportPane.USER_NO_ROLE] = [user];
                } else {
                    self.roleUserMap[BI.ShareReportPane.USER_NO_ROLE].push(user);
                }
            }
        });
        treeItems = BI.sortBy(treeItems, function (index, item) {
            return item.text;
        });
        return treeItems;
    },

    _refreshSelectedUserList: function () {
        var self = this;
        var selectedUser = this.allUsersTree.getValue();
        var uniqIds = [];
        BI.each(selectedUser, function (i, su) {
            uniqIds.push(su.slice(BI.UUID().length));
        });
        uniqIds = BI.uniq(uniqIds);
        if (uniqIds.length > 0) {
            this.tab.setSelect(BI.ShareReportPane.USER_EXIST);
            //找到所有已选的用户 和 对应的角色
            var selectedRolesMap = {}, noRoleUsers = [];
            BI.each(uniqIds, function (i, id) {
                BI.each(self.allUsers, function (i, item) {
                    if (item.user_id.toString() === id) {
                        if (BI.isNotNull(item.roles) && BI.isNotEmptyArray(item.roles)) {
                            BI.each(item.roles, function (j, role) {
                                if (BI.isNull(selectedRolesMap[role])) {
                                    selectedRolesMap[role] = [];
                                }
                                selectedRolesMap[role].push(item);
                            })
                        }
                        var department = item.user_department;
                        if (BI.isNotNull(department) && department !== "") {
                            if (BI.isNull(selectedRolesMap[department])) {
                                selectedRolesMap[department] = [];
                            }
                            selectedRolesMap[department].push(item);
                        }
                        if ((BI.isNull(item.roles) || item.roles.length === 0) && (BI.isNull(department) || department === "")) {
                            noRoleUsers.push(item);
                        }
                    }
                });
            });
            var noRoleMap = {};
            if (noRoleUsers.length > 0) {
                noRoleMap[BI.i18nText("BI-User_Without_Role")] = noRoleUsers;
            }
            BI.extend(selectedRolesMap, noRoleMap);
            self.selectedUserList.populate(selectedRolesMap);
        } else {
            this.tab.setSelect(BI.ShareReportPane.USER_EMPTY);
        }
    },

    getValue: function () {
        var selectedUser = this.allUsersTree.getValue();
        var sUserIds = [];
        BI.each(selectedUser, function (i, su) {
            sUserIds.push(su.slice(BI.UUID().length));
        });
        sUserIds = BI.uniq(sUserIds);
        return sUserIds;
    },

    end: function () {
        this.fireEvent(BI.ShareReportPane.EVENT_SHARE, this.getValue());
    },
    
    close: function(){
        this.fireEvent(BI.ShareReportPane.EVENT_CLOSE);
    }
});
BI.extend(BI.ShareReportPane, {
    USER_EMPTY: 1,
    USER_EXIST: 2,
    USER_NO_ROLE: "__no_role_user__"
});
BI.ShareReportPane.EVENT_CLOSE = "EVENT_CLOSE";
BI.ShareReportPane.EVENT_SHARE = "EVENT_SHARE";
$.shortcut("bi.share_report_pane", BI.ShareReportPane);
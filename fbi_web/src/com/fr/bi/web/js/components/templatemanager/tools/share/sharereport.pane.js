/**
 * create by young
 * 分享
 */
BI.ShareReportPane = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.ShareReportPane.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.ShareReportPane.superclass._init.apply(this, arguments);

    },

    rebuildNorth: function(north){
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Template_Share_To"),
            height: 50,
            lgap: 10,
            textAlign: "left"
        })
    },

    rebuildCenter: function(center){
        var self = this;
        this.searchResultPane = BI.createWidget({
            type: "bi.user_search_result_pane"
        });
        var searcher = BI.createWidget({
            type: "bi.searcher",
            width: 270,
            onSearch: function(op, callback){
                var keyword = op.keyword.toLowerCase();
                var searchResult = BI.Func.getSearchResult(self.allUsers, keyword, "user_name");
                callback(self._formatUserRole(searchResult.matched.concat(searchResult.finded)), keyword, self.allUsersTree.getValue());
            },
            isAutoSearch: false,
            isAutoSync: false,
            popup: this.searchResultPane
        });
        this.searchResultPane.on(BI.UserSearchResultPane.EVENT_CHANGE, function(){
            //搜索结果面板的选中项变化的时候  要与原面板的选中项做对比 结果集再set回原始面板
            var currOb = arguments[0];
            var currId = currOb.value, isChecked = currOb.checked;
            var oldValue = self.allUsersTree.getValue();
            isChecked === true ? oldValue.push(currId) : BI.remove(oldValue, currId);
            self.allUsersTree.setValue(oldValue);
            self._refreshSelectedUserList();
        });

        this.allUsersTree = BI.createWidget({
            type: "bi.simple_tree",
            cls: "all-users-list"
        });
        BI.requestAsync("fr_bi", "get_all_user_info", {}, function(res){
            self.allUsers = res;
            self.allUsersTree.populate(self._formatUserRole(res));
        });
        this.allUsersTree.on(BI.SimpleTreeView.EVENT_CHANGE, function(){
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

    _createSelectedUsers: function(v){
        var self = this;
        switch (v){
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
                this.selectedUserList.on(BI.Controller.EVENT_CHANGE, function(){
                    var userId = arguments[1];
                    var selectedUser = self.allUsersTree.getValue();
                    BI.some(selectedUser, function(i, id){
                        if(id === userId){
                            selectedUser.splice(i, 1);
                            return true;
                        }
                    });
                    self.allUsersTree.setValue(selectedUser);
                    self.searchResultPane.setValue(selectedUser);
                    self._refreshSelectedUserList();
                });
                return this.selectedUserList;

        }
    },

    //将用户和角色分开
    _formatUserRole: function(users){
        var self = this, roles = [];
        var treeItems = [];
        this.roleUserMap = {};
        var departPosts = [];
        BI.each(users, function(i, user){
            var item = {
                id: user.user_id.toString(),
                text: user.user_name,
                value: user.user_id.toString(),
                pId: user.user_department
            };
            treeItems.push({
                id: user.user_id.toString(),
                text: user.user_name,
                value: user.user_id.toString(),
                pId: user.user_department
            });
            if(!departPosts.contains(user.user_department)){
                departPosts.push(user.user_department);
                treeItems.push({
                    id: user.user_department,
                    text: user.user_department,
                    value: user.user_department,
                    isParent: true
                });
                if(BI.isNull(self.roleUserMap[user.user_department])){
                    self.roleUserMap[user.user_department] = [user];
                } else {
                    self.roleUserMap[user.user_department].push(user);
                }
            }

            if(BI.isNotNull(user.roles) && user.roles.length > 0) {
                BI.each(user.roles, function(j, role){
                    if(!roles.contains(role)){
                        roles.push(role);
                        treeItems.push({
                            id: role,
                            text: role,
                            value: role,
                            isParent: true
                        })
                    }
                    if(BI.isNull(self.roleUserMap[role])){
                        self.roleUserMap[role] = [user];
                    } else {
                        self.roleUserMap[role].push(user);
                    }
                    treeItems.push({
                        id: user.user_id.toString(),
                        pId: role,
                        text: user.user_name,
                        value: user.user_id.toString()
                    });
                })
            }
        });
        treeItems = BI.sortBy(treeItems, function(index, item){
            return item.text;
        });
        return treeItems;
    },

    _refreshSelectedUserList: function(){
        var self = this;
        var selectedUser = this.allUsersTree.getValue();
        if(selectedUser.length > 0){
            this.tab.setSelect(BI.ShareReportPane.USER_EXIST);
            //找到所有已选的用户 和 对应的角色
            var selectedRolesMap = {}, noRoleUsers = [];
            BI.each(selectedUser, function(i, id){
                    BI.each(self.allUsers, function(i, item) {
                        if (item.user_id.toString() === id) {
                            if (BI.isNotNull(item.roles) && BI.isNotEmptyArray(item.roles)) {
                                BI.each(item.roles, function (j, role) {
                                    if (BI.isNotNull(selectedRolesMap[role])) {
                                        selectedRolesMap[role].push(item);
                                    } else {
                                        selectedRolesMap[role] = [item];
                                    }
                                })
                            }
                            if (BI.isNotNull(item.user_department)) {
                                if(BI.isNull(selectedRolesMap[item.user_department])) {
                                    selectedRolesMap[item.user_department] = [];
                                }
                                selectedRolesMap[item.user_department].push(item);
                            }
                            if(BI.isNull(item.roles) && BI.isNull(item.user_department)) {
                                noRoleUsers.push(item);
                            }
                        }
                    });
            });
            var noRoleMap = {};
            if(noRoleUsers.length > 0){
                noRoleMap[BI.i18nText("BI-User_Without_Role")] = noRoleUsers;
            }
            BI.extend(selectedRolesMap, noRoleMap);
            self.selectedUserList.populate(selectedRolesMap);
        } else {
            this.tab.setSelect(BI.ShareReportPane.USER_EMPTY);
        }
    },

    end: function(){
        this.fireEvent(BI.ShareReportPane.EVENT_SHARE, this.allUsersTree.getValue());
    }
});
BI.extend(BI.ShareReportPane, {
    USER_EMPTY: 1,
    USER_EXIST: 2,
    USER_NO_ROLE: -1
});
BI.ShareReportPane.EVENT_SHARE = "EVENT_SHARE";
$.shortcut("bi.share_report_pane", BI.ShareReportPane);
/**
 * Created by Young's on 2016/5/30.
 */
BI.AllReportsFilter = BI.inherit(BI.Widget, {

    _constant: {
        REPORT_STATUS_APPLYING: 1,
        REPORT_STATUS_NORMAL: 2,
        REPORT_STATUS_HANGOUT: 3
    },

    _defaultConfig: function () {
        return BI.extend(BI.AllReportsFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-reports-filter"
        })
    },

    _init: function () {
        BI.AllReportsFilter.superclass._init.apply(this, arguments);
        var self = this;
        this.depart = BI.createWidget({
            type: "bi.tree_value_chooser_combo",
            itemsCreator: BI.bind(this._departCreator, this),
            cache: false,
            height: 24,
            width: 180
        });
        this.depart.on(BI.TreeValueChooserCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AllReportsFilter.EVENT_CHANGE);
        });
        this.role = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: BI.bind(this._rolesCreator, this),
            cache: false,
            height: 24,
            width: 180
        });
        this.role.on(BI.ValueChooserCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AllReportsFilter.EVENT_CHANGE);
        });
        this.name = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: BI.bind(this._usersCreator, this),
            cache: false,
            height: 24,
            width: 180
        });
        this.name.on(BI.ValueChooserCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AllReportsFilter.EVENT_CHANGE);
        });
        this.status = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: BI.bind(this._statusCreator, this),
            cache: false,
            height: 24,
            width: 180
        });
        this.status.on(BI.ValueChooserCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AllReportsFilter.EVENT_CHANGE);
        });
        this.lastModify = BI.createWidget({
            type: "bi.time_interval",
            height: 24,
            width: 380
        });
        this.lastModify.on(BI.TimeInterval.EVENT_CHANGE, function () {
            self.fireEvent(BI.AllReportsFilter.EVENT_CHANGE);
        });
        var reset = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Reset"),
            height: 25,
            width: 90
        });
        reset.on(BI.Button.EVENT_CHANGE, function () {
            self.depart.setValue();
            self.role.setValue();
            self.name.setValue();
            self.status.setValue();
            self.lastModify.setValue();
            self.fireEvent(BI.AllReportsFilter.EVENT_CHANGE, true);
        });
        this.filterPane = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            cls: "filter-pane",
            items: [{
                el: {
                    type: "bi.left",
                    cls: "inner-wrapper",
                    scrollable: true,
                    items: [{
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Department"),
                            cls: "filter-type",
                            height: 24,
                            hgap: 10
                        }, this.depart]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Role"),
                            cls: "filter-type",
                            height: 24,
                            hgap: 10
                        }, this.role]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Users"),
                            cls: "filter-type",
                            height: 24,
                            hgap: 10
                        }, this.name]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Status"),
                            cls: "filter-type",
                            height: 24,
                            hgap: 10
                        }, this.status]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Last_Modify_Date"),
                            cls: "filter-type",
                            height: 24,
                            hgap: 10
                        }, this.lastModify]
                    }, reset],
                    rgap: 24,
                    tgap: 10
                },
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        });
    },

    _departCreator: function (op, callback) {
        var items = [];
        //坑爹说好的联动不做了。。。先注释了
        // var sRole = this.role.getValue(), sUser = this.name.getValue();
        // var sRoleType = sRole.type || BI.Selection.Multi, sRoleValue = sRole.value || [];
        // var sUserType = sUser.type || BI.Selection.Multi, sUserValue = sUser.value || [];
        // var selectedRoles = sRoleValue, selectedUsers = sUserValue;
        // if (sRoleValue.length !== 0 && sRoleType === BI.Selection.All) {
        //     BI.each(this.roles, function (i, role) {
        //         selectedRoles = [];
        //         if (!sRoleValue.contains(role.id)) {
        //             selectedRoles.push(role.id);
        //         }
        //     });
        // }
        // if (sUserValue.length !== 0 && sUserType === BI.Selection.All) {
        //     selectedUsers = [];
        //     BI.each(this.users, function (i, user) {
        //         if (!sRoleValue.contains(user.id)) {
        //             selectedUsers.push(user.id);
        //         }
        //     });
        // }
        // //选中的角色中包含的部门，选中的用户所在的部门，交集
        // var rFilter = [], uFilter = [];
        // BI.each(this.roles, function (i, role) {
        //     var dId = role.departmentid;
        //     if (!rFilter.contains(dId) && selectedRoles.contains(role.id)) {
        //         rFilter.push(dId);
        //     }
        //     BI.some(role.users, function (j, uId) {
        //         if (!uFilter.contains(dId) && selectedUsers.contains(uId)) {
        //             uFilter.push(dId);
        //             return true;
        //         }
        //     });
        // });
        // var departIds = [];
        // if (rFilter.length === 0 || uFilter.length === 0) {
        //     departIds = rFilter.concat(uFilter);
        // } else {
        //     BI.each(rFilter, function (i, fId) {
        //         if (uFilter.contains(fId)) {
        //             departIds.push(fId);
        //         }
        //     });
        // }

        function formatItems(items) {
            var result = [];
            BI.each(items, function (i, item) {
                if (BI.isNotNull(item.ChildNodes)) {
                    result = result.concat(formatItems(item.ChildNodes));
                }
                result.push({
                    text: item.text,
                    value: item.id,
                    title: item.text,
                    id: item.id,
                    pId: item.parentID
                })
            });
            return result;
        }

        callback(formatItems(this.departs));
    },

    _rolesCreator: function (options, callback) {
        var items = [];
        // var sDepart = this.depart.getValue(), sUser = this.name.getValue();
        // var sDepartType = sDepart.type || BI.Selection.Multi, sUserType = sUser.type || BI.Selection.Multi;
        // var sDepartValue = sDepart.value || [], sUserValue = sUser.value || [];
        // var selectedDepart = sDepartValue, selectedUser = sUserValue;
        // if (sDepartValue.length !== 0 && sDepartType === BI.Selection.All) {
        //     selectedDepart = [];
        //     BI.each(this.departs, function (i, depart) {
        //         if (!sDepartValue.contains(depart.id)) {
        //             selectedDepart.push(depart.id);
        //         }
        //     });
        // }
        // if (sUserValue.length !== 0 && sUserType === BI.Selection.All) {
        //     selectedUser = [];
        //     BI.each(this.departs, function (i, depart) {
        //         if (!sUserValue.contains(depart.id)) {
        //             selectedUser.push(depart.id);
        //         }
        //     });
        // }
        //
        // //过滤条件
        // var dFilter = [], uFilter = [];
        // BI.each(this.roles, function (i, role) {
        //     var roleId = role.id;
        //     if (!dFilter.contains(roleId) && selectedDepart.contains(role.departmentid)) {
        //         dFilter.push(roleId);
        //     }
        //     BI.some(role.users, function (j, user) {
        //         if (!uFilter.contains(roleId) && selectedUser.contains(user.id)) {
        //             uFilter.push(roleId);
        //         }
        //     });
        // });
        // var roleIds = [];
        // if (dFilter.length === 0 || uFilter.length === 0) {
        //     roleIds = dFilter.concat(uFilter);
        // } else {
        //     BI.each(dFilter, function (i, fId) {
        //         if (uFilter.contains(fId)) {
        //             roleIds.push(fId);
        //         }
        //     });
        // }

        BI.each(this.roles, function (i, role) {
            var roleName = role.text;
            if (BI.isNotNull(role.departmentname)) {
                roleName = role.departmentname + role.postname
            }
            items.push({
                text: roleName,
                value: role.id,
                title: roleName
            });
        });
        callback(items);
    },

    _usersCreator: function (options, callback) {
        var items = [];
        // var sDepart = this.depart.getValue(), sRole = this.role.getValue();
        // var sDepartType = sDepart.type || BI.Selection.Multi, sRoleType = sRole.type || BI.Selection.Multi;
        // var sDepartValue = sDepart.value || [], sRoleValue = sRole.value || [];
        // var selectedDepart = sDepartValue, selectedRole = sRoleValue;
        // if (sDepartValue.length !== 0 && sDepartType === BI.Selection.All) {
        //     selectedDepart = [];
        //     BI.each(this.departs, function (i, depart) {
        //         if (!sDepartValue.contains(depart.id)) {
        //             selectedDepart.push(depart.id);
        //         }
        //     });
        // }
        // if (sRoleValue.length !== 0 && sRoleType === BI.Selection.All) {
        //     selectedRole = [];
        //     BI.each(this.roles, function (i, role) {
        //         if (!sRoleValue.contains(role.id)) {
        //             selectedRole.push(role.id);
        //         }
        //     });
        // }
        //
        // //过滤条件
        // var userIds = [];
        // if (selectedDepart.length !== 0 || selectedRole.length !== 0) {
        //     BI.each(this.roles, function (i, role) {
        //         if (selectedDepart.length === 0 && selectedRole.contains(role.id)) {
        //             BI.each(role.users, function(j, uId){
        //                 !userIds.contains(uId) && (userIds.push(uId));
        //             });
        //         }
        //         if(selectedRole.length === 0 && selectedDepart.contains(role.departmentid)){
        //             BI.each(role.users, function(j, uId){
        //                 !userIds.contains(uId) && (userIds.push(uId));
        //             });
        //         }
        //         if(selectedDepart.contains(role.departmentid) && selectedRole.contains(role.id)){
        //             BI.each(role.users, function(j, uId){
        //                 !userIds.contains(uId) && (userIds.push(uId));
        //             });
        //         }
        //     });
        // }

        BI.each(this.users, function (i, user) {
            items.push({
                text: user.realname,
                value: user.id,
                title: user.realname
            });
        });

        callback(items);
    },

    _statusCreator: function (options, callback) {
        var items = [{
            text: BI.i18nText("BI-Report_Hangout_Applying"),
            value: BICst.REPORT_STATUS.APPLYING,
            title: BI.i18nText("BI-Report_Hangout_Applying")
        }, {
            text: BI.i18nText("BI-Not_Apply_Hangout"),
            value: BICst.REPORT_STATUS.NORMAL,
            title: BI.i18nText("BI-Not_Apply_Hangout")
        }, {
            text: BI.i18nText("BI-Hangouted"),
            value: BICst.REPORT_STATUS.HANGOUT,
            title: BI.i18nText("BI-Hangouted")
        }];
        callback(items);
    },

    getValue: function () {
        var self = this;
        var sDepart = this.depart.getValue(), sRole = this.role.getValue(), sUser = this.name.getValue(), sStatus = this.status.getValue();
        var sRoleType = sRole.type || BI.Selection.Multi,
            sUserType = sUser.type || BI.Selection.Multi,
            sStatusType = sStatus.type || BI.Selection.Multi;
        var sUserValue = sUser.value || [],
            sRoleValue = sRole.value || [],
            sStatusValue = sStatus.value || [];
        var selectedUser = sUserValue, selectedRole = sRoleValue, selectedStatus = sStatusValue;

        function getAllChildIds(nodes) {
            var ids = [];
            BI.each(nodes, function(i, node){
                if(BI.isNotNull(node.ChildNodes)) {
                    ids = ids.concat(getAllChildIds(node.ChildNodes));
                }
                ids.push(node.id.toString());
            });
            return ids;
        }

        function getChildNodes(id, nodes) {
            var childNodes = [];
            BI.some(nodes, function(i, node){
                if(node.id.toString() === id) {
                    childNodes = node.ChildNodes || [];
                    return true;
                }
                if(BI.isNotNull(node.ChildNodes)) {
                    childNodes = getChildNodes(id, node.ChildNodes);
                }
            });
            return childNodes;
        }

        function getTreeIds(ob) {
            var ids = [];
            BI.each(ob, function (id, v) {
                if(BI.isEmptyObject(v)) {
                    ids.push(id);
                    return;
                }
                ids = ids.concat(getTreeIds(v));
            });
            return ids;
        }

        var pIds = getTreeIds(sDepart);
        var selectedDepart = [];
        BI.each(pIds, function(i, pId){
            selectedDepart.push(pId);
            selectedDepart = selectedDepart.concat(getAllChildIds(getChildNodes(pId, self.departs)));
        });

        if (sRoleValue.length !== 0 && sRoleType === BI.Selection.All) {
            selectedRole = [];
            BI.each(this.roles, function (i, role) {
                if (!sRoleValue.contains(role.id)) {
                    selectedRole.push(role.id);
                }
            });
        }
        if (sUserValue.length !== 0 && sUserType === BI.Selection.All) {
            selectedUser = [];
            BI.each(this.users, function (i, user) {
                if (!sUserValue.contains(user.id)) {
                    selectedUser.push(user.id);
                }
            });
        }
        if (sStatusValue.length !== 0 && sStatusType === BI.Selection.All) {
            selectedStatus = [];
            var status = [this._constant.REPORT_STATUS_APPLYING,
                this._constant.REPORT_STATUS_HANGOUT,
                this._constant.REPORT_STATUS_NORMAL];
            BI.each(status, function (i, s) {
                if (!sStatusValue.contains(s)) {
                    selectedStatus.push(s);
                }
            });
        }


        //获取复杂日期的值
        function parseComplexDate(v) {
            if (BI.isNull(v)) {
                return;
            }
            if (v.type === BICst.MULTI_DATE_PARAM) {
                return parseComplexDateForParam(v.value);
            } else {
                return parseComplexDateCommon(v);
            }
            function parseComplexDateForParam(value) {
                var wWid = value.wId, se = value.startOrEnd;
                if (BI.isNotNull(wWid) && BI.isNotNull(se)) {
                    var wWValue = BI.Utils.getWidgetValueByID(wWid);
                    if (se === BI.MultiDateParamPane.start && BI.isNotNull(wWValue.start)) {
                        return parseComplexDateCommon(wWValue.start);
                    } else {
                        return parseComplexDateCommon(wWValue.end);
                    }
                } else {
                    return parseComplexDateCommon(BI.Utils.getWidgetValueByID(value));
                }
            }

            function parseComplexDateCommon(v) {
                var type = v.type, value = v.value;
                var date = new Date();
                var currY = date.getFullYear(), currM = date.getMonth(), currD = date.getDate();
                var tool = new BI.MultiDateParamTrigger();
                if (BI.isNull(type) && BI.isNotNull(v.year)) {
                    return new Date(v.year, v.month, v.day).getTime();
                }
                switch (type) {
                    case BICst.MULTI_DATE_YEAR_PREV:
                        return new Date(currY - 1 * value, currM, currD).getTime();
                    case BICst.MULTI_DATE_YEAR_AFTER:
                        return new Date(currY + 1 * value, currM, currD).getTime();
                    case BICst.MULTI_DATE_YEAR_BEGIN:
                        return new Date(currY, 1, 1).getTime();
                    case BICst.MULTI_DATE_YEAR_END:
                        return new Date(currY, 11, 31).getTime();

                    case BICst.MULTI_DATE_MONTH_PREV:
                        return tool._getBeforeMultiMonth(value).getTime();
                    case BICst.MULTI_DATE_MONTH_AFTER:
                        return tool._getAfterMultiMonth(value).getTime();
                    case BICst.MULTI_DATE_MONTH_BEGIN:
                        return new Date(currY, currM, 1).getTime();
                    case BICst.MULTI_DATE_MONTH_END:
                        return new Date(currY, currM, (date.getLastDateOfMonth()).getDate()).getTime();

                    case BICst.MULTI_DATE_QUARTER_PREV:
                        return tool._getBeforeMulQuarter(value).getTime();
                    case BICst.MULTI_DATE_QUARTER_AFTER:
                        return tool._getAfterMulQuarter(value).getTime();
                    case BICst.MULTI_DATE_QUARTER_BEGIN:
                        return tool._getQuarterStartDate().getTime();
                    case BICst.MULTI_DATE_QUARTER_END:
                        return tool._getQuarterEndDate().getTime();

                    case BICst.MULTI_DATE_WEEK_PREV:
                        return date.getOffsetDate(-7 * value).getTime();
                    case BICst.MULTI_DATE_WEEK_AFTER:
                        return date.getOffsetDate(7 * value).getTime();

                    case BICst.MULTI_DATE_DAY_PREV:
                        return date.getOffsetDate(-1 * value).getTime();
                    case BICst.MULTI_DATE_DAY_AFTER:
                        return date.getOffsetDate(1 * value).getTime();
                    case BICst.MULTI_DATE_DAY_TODAY:
                        return date.getTime();
                    case BICst.MULTI_DATE_CALENDAR:
                        return new Date(value.year, value.month, value.day).getTime();

                }
            }
        }

        var start = this.lastModify.getValue().start, end = this.lastModify.getValue().end;
        return {
            departs: selectedDepart,
            roles: selectedRole,
            users: selectedUser,
            status: selectedStatus,
            start: parseComplexDate(start),
            end: parseComplexDate(end)
        }
    },

    populate: function (departs, roles, users) {
        this.departs = departs;
        this.roles = roles;
        this.users = users;
    }
});
BI.AllReportsFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.all_reports_filter", BI.AllReportsFilter);
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
            text: BI.i18nText("BI-Basic_Reset"),
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
                            text: BI.i18nText("BI-Basic_Department"),
                            cls: "filter-type",
                            height: 24,
                            hgap: 10
                        }, this.depart]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Basic_Role"),
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
                            text: BI.i18nText("BI-Basic_Status"),
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
        var items = [], pIds = [], ids = [];
        BI.each(this.roles, function (i, role) {
            if (BI.isNotNull(role.departmentname) && BI.isNotNull(role.postname)) {
                var pName = role.departmentname;
                if (!pIds.contains(pName)) {
                    pIds.push(pName)
                    items.push({
                        text: pName,
                        title: pName,
                        value: pName,
                        id: pName
                    });
                }
                items.push({
                    text: role.postname,
                    title: role.postname,
                    value: role.postname,
                    id: role.postname,
                    pId: pName
                })
            }
        });

        callback(items);
    },

    _rolesCreator: function (options, callback) {
        var items = [];

        BI.each(this.roles, function (i, role) {
            var roleName = role.text;
            if (BI.isNotNull(role.departmentname)) {
                return;
            }
            items.push({
                text: roleName,
                value: roleName,
                title: roleName
            });
        });
        callback(items);
    },

    _usersCreator: function (options, callback) {
        var items = [];
        BI.each(this.users, function (i, user) {
            items.push({
                text: user.realname,
                value: user.realname,
                title: user.realname,
                id: user.id
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
            text: BI.i18nText("BI-Has_Hangouted"),
            value: BICst.REPORT_STATUS.HANGOUT,
            title: BI.i18nText("BI-Has_Hangouted")
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
            BI.each(nodes, function (i, node) {
                if (BI.isNotNull(node.ChildNodes)) {
                    ids = ids.concat(getAllChildIds(node.ChildNodes));
                }
                ids.push(node.id.toString());
            });
            return ids;
        }

        function getChildNodes(id, nodes) {
            var childNodes = [];
            BI.some(nodes, function (i, node) {
                if (node.id.toString() === id) {
                    childNodes = node.ChildNodes || [];
                    return true;
                }
                if (BI.isNotNull(node.ChildNodes)) {
                    childNodes = getChildNodes(id, node.ChildNodes);
                }
            });
            return childNodes;
        }

        function getTreeIds(ob) {
            var ids = [];
            BI.each(ob, function (id, v) {
                if (BI.isEmptyObject(v)) {
                    ids.push(id);
                    return;
                }
                ids = ids.concat(getTreeIds(v));
            });
            return ids;
        }

        var pIds = getTreeIds(sDepart);
        var selectedDepart = [];
        BI.each(pIds, function (i, pId) {
            selectedDepart.push(pId);
            selectedDepart = selectedDepart.concat(getAllChildIds(getChildNodes(pId, self.departs)));
        });

        if (sRoleType === BI.Selection.All) {
            selectedRole = [];
            BI.each(this.roles, function (i, role) {
                if (BI.isNotNull(role.text) && !sRoleValue.contains(role.text)) {
                    selectedRole.push(role.text);
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
                        return new Date().getBeforeMultiMonth(value).getTime();
                    case BICst.MULTI_DATE_MONTH_AFTER:
                        return new Date().getAfterMultiMonth(value).getTime();
                    case BICst.MULTI_DATE_MONTH_BEGIN:
                        return new Date(currY, currM, 1).getTime();
                    case BICst.MULTI_DATE_MONTH_END:
                        return new Date(currY, currM, (date.getLastDateOfMonth()).getDate()).getTime();

                    case BICst.MULTI_DATE_QUARTER_PREV:
                        return new Date().getBeforeMulQuarter(value).getTime();
                    case BICst.MULTI_DATE_QUARTER_AFTER:
                        return new Date().getAfterMulQuarter(value).getTime();
                    case BICst.MULTI_DATE_QUARTER_BEGIN:
                        return new Date().getQuarterStartDate().getTime();
                    case BICst.MULTI_DATE_QUARTER_END:
                        return new Date().getQuarterEndDate().getTime();

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